package com.craftaxo.axomarket;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MarketListener implements Listener {

    private final Main plugin;

    // Main.java'dan gelen 'this' (plugin) parametresini kabul eden constructor
    public MarketListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.startsWith(ChatColor.DARK_GRAY + "AxoMarket")) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        ItemMeta meta = clicked.getItemMeta();

        if (meta == null) return;

        // GERİ DÖN
        if (clicked.getType() == Material.BARRIER && meta.getDisplayName().contains("Geri Dön")) {
            MarketGUI.openMainMenu(player);
            return;
        }

        // ANA MENÜ TIKLAMALARI
        if (title.endsWith("Ana Menü")) {
            switch (clicked.getType()) {
                case STONE: MarketGUI.openBlocksMenu(player); break;
                case DIAMOND_SWORD: MarketGUI.openWeaponsMenu(player); break;
                case DIAMOND_CHESTPLATE: MarketGUI.openArmorMenu(player); break;
                case NETHERITE_UPGRADE_SMITHING_TEMPLATE: MarketGUI.openOresAndTemplatesMenu(player); break;
            }
            return;
        }

        // FİYAT OKUMA VE SATIN ALMA
        List<String> lore = meta.getLore();
        if (lore == null) return;

        double basePrice = -1;
        for (String line : lore) {
            if (line.contains("Fiyat:")) {
                String priceStr = ChatColor.stripColor(line).replaceAll("[^0-9.]", "");
                try {
                    basePrice = Double.parseDouble(priceStr);
                } catch (Exception ignored) {}
                break;
            }
        }

        if (basePrice <= 0) return;

        int amountToBuy = (event.getClick() == ClickType.RIGHT) ? 64 : 1;
        double totalPrice = basePrice * amountToBuy;

        Economy econ = Main.getEconomy();
        if (econ == null || econ.getBalance(player) < totalPrice) {
            player.sendMessage(ChatColor.RED + "Yetersiz Bakiye! Gerekli: " + totalPrice + " TL");
            return;
        }

        ItemStack buyItem = clicked.clone();
        ItemMeta buyMeta = buyItem.getItemMeta();

        if (buyMeta != null && buyMeta.hasLore()) {
            List<String> cleanLore = buyMeta.getLore();
            cleanLore.removeIf(l -> l.contains("Fiyat:") || l.contains("Satın Al") || l.contains("["));
            buyMeta.setLore(cleanLore);
            buyItem.setItemMeta(buyMeta);
        }

        boolean isStackable = buyItem.getMaxStackSize() > 1;

        if (isStackable) {
            buyItem.setAmount(amountToBuy);
            if (hasInventorySpace(player, buyItem)) {
                econ.withdrawPlayer(player, totalPrice);
                player.getInventory().addItem(buyItem);
                player.sendMessage(ChatColor.GREEN + "Başarıyla " + amountToBuy + " adet satın alındı! Ödenen: " + totalPrice + " TL");
            } else {
                player.sendMessage(ChatColor.RED + "Envanterinizde yeterli boş yer yok!");
            }
        } else {
            buyItem.setAmount(1);
            int givenCount = 0;

            for (int i = 0; i < amountToBuy; i++) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(buyItem.clone());
                    givenCount++;
                } else {
                    break;
                }
            }

            if (givenCount > 0) {
                double finalCost = basePrice * givenCount;
                econ.withdrawPlayer(player, finalCost);
                player.sendMessage(ChatColor.GREEN + "Başarıyla " + givenCount + " adet satın alındı! Ödenen: " + finalCost + " TL");

                if (givenCount < amountToBuy) {
                    player.sendMessage(ChatColor.YELLOW + "Envanteriniz dolduğu için sadece " + givenCount + " adet alabildiniz.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "Envanteriniz tamamen dolu!");
            }
        }
    }

    private boolean hasInventorySpace(Player player, ItemStack item) {
        int freeSpace = 0;
        for (ItemStack invItem : player.getInventory().getStorageContents()) {
            if (invItem == null || invItem.getType() == Material.AIR) {
                freeSpace += item.getMaxStackSize();
            } else if (invItem.isSimilar(item)) {
                freeSpace += (item.getMaxStackSize() - invItem.getAmount());
            }
        }
        return freeSpace >= item.getAmount();
    }
}
