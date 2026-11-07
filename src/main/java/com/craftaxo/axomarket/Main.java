package com.craftaxo.axomarket;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Main extends JavaPlugin implements Listener {

    private static Economy econ = null;
    
    private final Map<UUID, ItemStack> selectedItem = new HashMap<>();
    private final Map<UUID, Integer> selectedAmount = new HashMap<>();
    private final Map<UUID, Double> selectedUnitPrice = new HashMap<>();

    // Düzenleme (Edit) işlemleri için geçici veri tutucular
    private final Map<UUID, String> editingState = new HashMap<>();
    private final Map<UUID, ItemStack> tempEditingItem = new HashMap<>();

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault eklentisi bulunamadi! Eklenti kapatiliyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("AxoMarket basariyla aktif edildi!");
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("market")) {
            openMainMenu(player, false);
            return true;
        } else if (command.getName().equalsIgnoreCase("marketedit")) {
            if (!player.hasPermission("axomarket.admin")) {
                player.sendMessage(ChatColor.RED + "Bu komutu kullanmak için yetkiniz yok!");
                return true;
            }
            openMainMenu(player, true);
            return true;
        }
        return false;
    }

    // --- MENÜLER ---

    public void openMainMenu(Player player, boolean editMode) {
        String title = editMode ? ChatColor.DARK_RED + "Market Düzenleme (Admin)" : ChatColor.DARK_GRAY + "Sunucu Mağazası";
        Inventory gui = Bukkit.createInventory(null, 36, title);

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));

        if (editMode) {
            gui.setItem(0, createGuiItem(Material.NETHER_STAR, ChatColor.GREEN + "+ Yeni Kategori / Ürün Ekle", ChatColor.GRAY + "Buraya tıklayarak yeni eşya ekleyin."));
        }

        gui.setItem(10, createGuiItem(Material.NETHERITE_SWORD, ChatColor.GOLD + "Kılıçlar", ChatColor.GRAY + (editMode ? "İsmi değiştirmek için SHIFT + Sol Tık. Silmek için Sağ Tık." : "Kılıç kategorisini açmak için tıkla.")));
        gui.setItem(11, createGuiItem(Material.NETHERITE_CHESTPLATE, ChatColor.GOLD + "Setler", ChatColor.GRAY + (editMode ? "İsmi değiştirmek için SHIFT + Sol Tık. Silmek için Sağ Tık." : "Set kategorisini açmak için tıkla.")));
        gui.setItem(12, createGuiItem(Material.NETHERITE_PICKAXE, ChatColor.GOLD + "Kazmalar", ChatColor.GRAY + (editMode ? "İsmi değiştirmek için SHIFT + Sol Tık. Silmek için Sağ Tık." : "Kazma kategorisini açmak için tıkla.")));
        gui.setItem(13, createGuiItem(Material.TRAPPED_CHEST, ChatColor.GOLD + "Trap Eşyaları", ChatColor.GRAY + (editMode ? "İsmi değiştirmek için SHIFT + Sol Tık. Silmek için Sağ Tık." : "Tuzak eşyalarını açmak için tıkla.")));
        gui.setItem(14, createGuiItem(Material.WHITE_CONCRETE, ChatColor.GOLD + "Bloklar", ChatColor.GRAY + (editMode ? "İsmi değiştirmek için SHIFT + Sol Tık. Silmek için Sağ Tık." : "Blok kategorisini açmak için tıkla.")));
        gui.setItem(15, createGuiItem(Material.ENCHANTED_GOLDEN_APPLE, ChatColor.GOLD + "Özel Eşyalar", ChatColor.GRAY + (editMode ? "İsmi değiştirmek için SHIFT + Sol Tık. Silmek için Sağ Tık." : "Özel eşyaları açmak için tıkla.")));

        player.openInventory(gui);
    }

    public void openSubMenu(Player player, String categoryName, boolean editMode) {
        String titlePrefix = editMode ? ChatColor.DARK_RED + "[EDİT] " : "";
        Inventory gui = Bukkit.createInventory(null, 36, titlePrefix + categoryName);

        // Örnek içerikler
        if (categoryName.contains("Kılıç")) {
            gui.setItem(10, createMarketItem(Material.NETHERITE_SWORD, ChatColor.RED + "Keskinlik V", 1.5, Map.of(Enchantment.DAMAGE_ALL, 5)));
            gui.setItem(11, createMarketItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "Elmas Kılıç (Keskinlik V)", 1.5, Map.of(Enchantment.DAMAGE_ALL, 5)));
        } else {
            gui.setItem(10, createStackMarketItem(Material.GOLDEN_APPLE, "Altın Elma (1 Stak)", 130.0, 64));
            gui.setItem(11, createMarketItem(Material.ELYTRA, "Elitra", 200000.0, Map.of(Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        }

        if (editMode) {
            gui.setItem(26, createGuiItem(Material.NETHER_STAR, ChatColor.GREEN + "+ Bu Kategoriye Ürün Ekle", ""));
        }

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    // --- MİKTAR SEÇME VE SATIN ALMA MENÜSÜ ---

    public void openBuyMenu(Player player, ItemStack targetItem, double unitPrice) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Satın Alma Yeri");

        selectedItem.put(player.getUniqueId(), targetItem);
        selectedAmount.put(player.getUniqueId(), 1);
        selectedUnitPrice.put(player.getUniqueId(), unitPrice);

        updateBuyMenuGUI(player, gui);
        player.openInventory(gui);
    }

    private void updateBuyMenuGUI(Player player, Inventory gui) {
        int amount = selectedAmount.getOrDefault(player.getUniqueId(), 1);
        double unitPrice = selectedUnitPrice.getOrDefault(player.getUniqueId(), 0.0);
        double totalPrice = unitPrice * amount;
        ItemStack target = selectedItem.get(player.getUniqueId());

        gui.setItem(18, createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-32", ""));
        gui.setItem(19, createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-16", ""));
        gui.setItem(20, createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "-1", ""));

        ItemStack paper = new ItemStack(Material.PAPER, amount > 0 ? Math.min(amount, 64) : 1);
        ItemMeta pMeta = paper.getItemMeta();
        pMeta.setDisplayName(ChatColor.GREEN + "Satın Almak İçin Tıkla");
        pMeta.setLore(List.of(ChatColor.AQUA + "Toplam: ₺" + String.format("%.2f", totalPrice)));
        paper.setItemMeta(pMeta);
        gui.setItem(22, paper);

        gui.setItem(24, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+1", ""));
        gui.setItem(25, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+16", ""));
        gui.setItem(26, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+32", ""));
        gui.setItem(23, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "64 Yap", ""));

        ItemStack displayItem = target.clone();
        displayItem.setAmount(Math.min(Math.max(target.getAmount() * amount, 1), 64));
        gui.setItem(13, displayItem);

        gui.setItem(40, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
    }

    // --- EVENT LISTENER & CHAT INPUTS ---

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        boolean isEditMode = title.contains("Düzenleme") || title.contains("[EDİT]");

        if (title.contains("Mağazası") || title.contains("Kategorisi") || title.contains("Eşyalar") || title.contains("Satın Alma Yeri") || isEditMode) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();

            if (clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            // Düzenleme Modu Tıklamaları
            if (isEditMode) {
                if (clicked.getType() == Material.NETHER_STAR) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.YELLOW + "Lütfen chat üzerinden eklenecek öğe adını yazın:");
                    editingState.put(player.getUniqueId(), "WAITING_NEW_ITEM_NAME");
                    return;
                }

                if (event.getClick() == ClickType.SHIFT_LEFT) {
                    player.closeInventory();
                    player.sendMessage(ChatColor.YELLOW + "Lütfen chat üzerinden yeni ismi yazın:");
                    editingState.put(player.getUniqueId(), "WAITING_RENAME");
                    tempEditingItem.put(player.getUniqueId(), clicked);
                    return;
                }

                if (event.getClick() == ClickType.RIGHT) {
                    player.sendMessage(ChatColor.GREEN + "Eşya başarıyla kaldırıldı!");
                    player.closeInventory();
                    return;
                }
            }

            // Normal Menü Açılışları
            if (title.equals(ChatColor.DARK_GRAY + "Sunucu Mağazası") || title.equals(ChatColor.DARK_RED + "Market Düzenleme (Admin)")) {
                boolean edit = title.contains("Admin");
                if (clicked.getType() == Material.NETHERITE_SWORD) openSubMenu(player, "Kılıç Kategorisi", edit);
                else if (clicked.getType() == Material.NETHERITE_CHESTPLATE) openSubMenu(player, "Set Kategorisi", edit);
                else if (clicked.getType() == Material.NETHERITE_PICKAXE) openSubMenu(player, "Kazma Kategorisi", edit);
                else if (clicked.getType() == Material.TRAPPED_CHEST) openSubMenu(player, "Trap Kategorisi", edit);
                else if (clicked.getType() == Material.WHITE_CONCRETE) openSubMenu(player, "Blok Kategorisi", edit);
                else if (clicked.getType() == Material.ENCHANTED_GOLDEN_APPLE) openSubMenu(player, "Özel Eşyalar", edit);
                return;
            }

            if (!title.contains("Satın Alma Yeri")) {
                if (isEditMode) return; // Düzenleme modunda satın alma ekranına geçilmez

                double price = getPriceFromLore(clicked);
                if (price > 0) {
                    ItemStack clone = clicked.clone();
                    ItemMeta meta = clone.getItemMeta();
                    if (meta != null && meta.hasLore()) {
                        meta.setLore(null);
                        clone.setItemMeta(meta);
                    }
                    openBuyMenu(player, clone, price);
                }
                return;
            }

            // Satın Alma Yeri Miktar İşlemleri
            if (title.contains("Satın Alma Yeri")) {
                int currentAmt = selectedAmount.getOrDefault(player.getUniqueId(), 1);

                if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE && clicked.hasItemMeta()) {
                    String name = clicked.getItemMeta().getDisplayName();
                    if (name.contains("+1")) currentAmt += 1;
                    else if (name.contains("+16")) currentAmt += 16;
                    else if (name.contains("+32")) currentAmt += 32;
                    else if (name.contains("64 Yap")) currentAmt = 64;
                } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE && clicked.hasItemMeta()) {
                    String name = clicked.getItemMeta().getDisplayName();
                    if (name.contains("-1")) currentAmt -= 1;
                    else if (name.contains("-16")) currentAmt -= 16;
                    else if (name.contains("-32")) currentAmt -= 32;
                }

                if (currentAmt < 1) currentAmt = 1;
                if (currentAmt > 64) currentAmt = 64;

                selectedAmount.put(player.getUniqueId(), currentAmt);

                if (clicked.getType() == Material.PAPER) {
                    executePurchase(player);
                    return;
                }

                updateBuyMenuGUI(player, event.getInventory());
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!editingState.containsKey(uuid)) return;

        event.setCancelled(true);
        String message = event.getMessage();
        String state = editingState.get(uuid);

        Bukkit.getScheduler().runTask(this, () -> {
            if (state.equals("WAITING_NEW_ITEM_NAME")) {
                player.sendMessage(ChatColor.GREEN + "Öğe adı kaydedildi! Şimdi simgesini (örneğin: meşale / TORCH) yazın:");
                editingState.put(uuid, "WAITING_NEW_ITEM_MATERIAL");
            } else if (state.equals("WAITING_NEW_ITEM_MATERIAL")) {
                player.sendMessage(ChatColor.GREEN + "Simge seçildi! Market düzenleme menüsüne yönlendiriliyorsunuz.");
                editingState.remove(uuid);
                openMainMenu(player, true);
            } else if (state.equals("WAITING_RENAME")) {
                player.sendMessage(ChatColor.GREEN + "Başarıyla kaydedildi!");
                editingState.remove(uuid);
                tempEditingItem.remove(uuid);
                openMainMenu(player, true);
            }
        });
    }

    private void executePurchase(Player player) {
        int totalAmount = selectedAmount.getOrDefault(player.getUniqueId(), 1);
        double unitPrice = selectedUnitPrice.getOrDefault(player.getUniqueId(), 0.0);
        double totalCost = unitPrice * totalAmount;
        
        ItemStack itemToGive = selectedItem.get(player.getUniqueId());
        if (itemToGive == null) return;

        if (econ.getBalance(player) < totalCost) {
            player.sendMessage(ChatColor.RED + "Yetersiz bakiye! Gerekli: ₺" + String.format("%.2f", totalCost));
            return;
        }

        if (!hasEnoughSpace(player, itemToGive, totalAmount)) {
            player.sendMessage(ChatColor.RED + "Envanterinizde yeterli yer yok! Lütfen boşluk açıp tekrar deneyin.");
            return;
        }

        econ.withdrawPlayer(player, totalCost);

        int packSize = Math.max(1, itemToGive.getAmount());
        int actualAmount = totalAmount * packSize;
        ItemStack itemTemplate = itemToGive.clone();
        itemTemplate.setAmount(1);

        if (!hasEnoughSpace(player, itemTemplate, actualAmount)) {
            player.sendMessage(ChatColor.RED + "Envanteriniz dolu! Eşyalar envantere sığmıyor.");
            return;
        }

        econ.withdrawPlayer(player, totalCost);
        int remaining = actualAmount;
        int maxStack = itemTemplate.getMaxStackSize();
        while (remaining > 0) {
            int batchSize = Math.min(remaining, maxStack);
            ItemStack stack = itemTemplate.clone();
            stack.setAmount(batchSize);
            player.getInventory().addItem(stack);
            remaining -= batchSize;
        }

        player.sendMessage(ChatColor.GREEN + "Başarıyla satın aldın: " + actualAmount + " adet! Ödenen: ₺" + String.format("%.2f", totalCost));
        player.sendMessage(ChatColor.GREEN + "Başarıyla " + totalAmount + " adet satın aldın! Ödenen: ₺" + String.format("%.2f", totalCost));
        player.closeInventory();
    }

    private boolean hasEnoughSpace(Player player, ItemStack item, int amountNeeded) {
        int freeSpace = 0;
        int maxStack = item.getMaxStackSize();

        for (ItemStack content : player.getInventory().getStorageContents()) {
            if (content == null || content.getType() == Material.AIR) {
                freeSpace += maxStack;
            } else if (content.isSimilar(item)) {
                freeSpace += (maxStack - content.getAmount());
            }
        }
        return freeSpace >= amountNeeded;
    }

    // --- YARDIMCI METOTLAR ---

    private ItemStack createGuiItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (!lore.isEmpty()) meta.setLore(List.of(lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createMarketItem(Material material, String name, double price, Map<Enchantment, Integer> enchants) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (enchants != null) {
            enchants.forEach((ench, lvl) -> meta.addEnchant(ench, lvl, true));
        }
        meta.setLore(List.of(ChatColor.GREEN + "Fiyat: ₺" + price, ChatColor.YELLOW + "Satın almak için tıkla"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createStackMarketItem(Material material, String name, double price, int defaultStackSize) {
        ItemStack item = new ItemStack(material, defaultStackSize);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(ChatColor.GREEN + "Fiyat: ₺" + price, ChatColor.YELLOW + "Satın almak için tıkla"));
        item.setItemMeta(meta);
        return item;
    }

    private double getPriceFromLore(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String line : item.getItemMeta().getLore()) {
                if (line.contains("Fiyat: ₺")) {
                    try {
                        return Double.parseDouble(ChatColor.stripColor(line).replace("Fiyat: ₺", "").trim());
                    } catch (Exception ignored) {}
                }
            }
        }
        return 0.0;
    }
}
