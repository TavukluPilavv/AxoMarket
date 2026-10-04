package com.craftaxo.axomarket;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MarketListener implements Listener {

    private final Main plugin;
    private final Map<UUID, TradeSession> activeSessions = new HashMap<>();

    public MarketListener(Main plugin) {
        this.plugin = plugin;
    }

    private static class TradeSession {
        ItemStack item;
        double singlePrice;
        int amount;

        TradeSession(ItemStack item, double singlePrice, int amount) {
            this.item = item;
            this.singlePrice = singlePrice;
            this.amount = amount;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle() == null) return;
        String title = event.getView().getTitle();

        if (!title.contains("Market") && !title.contains("Kategorisi") && !title.contains("Satın Alma Ekranı")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();

        // Kapat Butonu
        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        // ANA MENÜ TIKLAMALARI
        if (title.contains("Sunucu Marketi")) {
            if (clicked.getType() == Material.NETHERITE_SWORD) MarketGUI.openSwordCategory(player);
            else if (clicked.getType() == Material.NETHERITE_PICKAXE) MarketGUI.openPickaxeCategory(player);
            else if (clicked.getType() == Material.NETHERITE_CHESTPLATE) MarketGUI.openArmorCategory(player);
            else if (clicked.getType() == Material.ENCHANTED_GOLDEN_APPLE) MarketGUI.openSpecialCategory(player);
            else if (clicked.getType() == Material.OAK_TRAPDOOR) MarketGUI.openTrapCategory(player);
            else if (clicked.getType() == Material.WHITE_CONCRETE) MarketGUI.openBlockCategory(player);
            return;
        }

        // KATEGORİ İÇİ TIKLAMALARI -> SATIN ALMA EKRANINA GEÇİŞ
        if (title.contains("Kategorisi")) {
            double price = extractPriceFromLore(clicked);
            if (price > 0) {
                activeSessions.put(player.getUniqueId(), new TradeSession(clicked.clone(), price, 1));
                MarketGUI.openBuyMenu(player, clicked, price, 1);
            }
            return;
        }

        // SATIN ALMA EKRANI TIKLAMALARI (+1, -1, Kağıt)
        if (title.contains("Satın Alma Ekranı")) {
            TradeSession session = activeSessions.get(player.getUniqueId());
            if (session == null) return;

            // Camlar ile Miktar Değişimi
            if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE || clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                int delta = Integer.parseInt(name);
                session.amount = Math.max(1, Math.min(64, session.amount + delta));
                MarketGUI.openBuyMenu(player, session.item, session.singlePrice, session.amount);
                return;
            }

            // Satın Alma Onayı (Kağıt)
            if (clicked.getType() == Material.PAPER) {
                double total = session.singlePrice * session.amount;
                Economy econ = Main.getEconomy();

                if (econ.getBalance(player) < total) {
                    player.sendMessage(ChatColor.RED + "Yetersiz bakiye! Gerekli: ₺" + total);
                    return;
                }

                econ.withdrawPlayer(player, total);

                // Eşyayı Hazırla ve Büyüleri Bas (InfiniteEnchant Uygunluğu)
                ItemStack finalGive = cleanAndEnchantItem(session.item, session.amount);
                player.getInventory().addItem(finalGive);

                player.sendMessage(ChatColor.GREEN + "Başarıyla " + session.amount + " adet alındı! Ödenen: ₺" + total);
                player.closeInventory();
                activeSessions.remove(player.getUniqueId());
            }
        }
    }

    private double extractPriceFromLore(ItemStack item) {
        if (!item.hasItemMeta() || !item.getItemMeta().hasLore()) return 0.0;
        List<String> lore = item.getItemMeta().getLore();
        for (String line : lore) {
            if (line.contains("Alış Fiyatı:")) {
                String clean = ChatColor.stripColor(line).replaceAll("[^0-9.]", "");
                try {
                    return Double.parseDouble(clean);
                } catch (Exception ignored) {}
            }
        }
        return 0.0;
    }

    private ItemStack cleanAndEnchantItem(ItemStack rawItem, int amount) {
        ItemStack item = new ItemStack(rawItem.getType(), amount);
        ItemMeta meta = item.getItemMeta();
        ItemMeta rawMeta = rawItem.getItemMeta();

        if (meta != null && rawMeta != null) {
            meta.setDisplayName(rawMeta.getDisplayName());
            item.setItemMeta(meta);

            // Lore'daki yazılara göre yüksek seviye büyüleri doğrudan ekle (InfiniteEnchanted uyumlu)
            if (rawMeta.hasLore()) {
                for (String line : rawMeta.getLore()) {
                    String clean = ChatColor.stripColor(line);
                    if (clean.contains("Keskinlik")) addUnsafeEnchant(item, Enchantment.DAMAGE_ALL, getLevel(clean));
                    if (clean.contains("Verimlilik")) addUnsafeEnchant(item, Enchantment.DIG_SPEED, getLevel(clean));
                    if (clean.contains("Koruma")) addUnsafeEnchant(item, Enchantment.PROTECTION_ENVIRONMENTAL, getLevel(clean));
                    if (clean.contains("Kırılmazlık")) addUnsafeEnchant(item, Enchantment.DURABILITY, getLevel(clean));
                    if (clean.contains("Tamir")) addUnsafeEnchant(item, Enchantment.MENDING, 1);
                    if (clean.contains("Alevden Çehre")) addUnsafeEnchant(item, Enchantment.FIRE_ASPECT, getLevel(clean));
                    if (clean.contains("Savurma")) addUnsafeEnchant(item, Enchantment.KNOCKBACK, getLevel(clean));
                    if (clean.contains("Servet")) addUnsafeEnchant(item, Enchantment.LOOT_BONUS_BLOCKS, getLevel(clean));
                }
            }
        }
        return item;
    }

    private void addUnsafeEnchant(ItemStack item, Enchantment ench, int level) {
        if (level > 0) {
            item.addUnsafeEnchantment(ench, level);
        }
    }

    private int getLevel(String text) {
        String num = text.replaceAll("[^0-9]", "");
        return num.isEmpty() ? 1 : Integer.parseInt(num);
    }
}
