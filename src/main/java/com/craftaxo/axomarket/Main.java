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
import org.bukkit.event.inventory.InventoryClickEvent;
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
        if (command.getName().equalsIgnoreCase("market") && sender instanceof Player) {
            openMainMenu((Player) sender);
            return true;
        }
        return false;
    }

    // --- MENÜLER ---

    public void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "Sunucu Magazasi");

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));

        gui.setItem(10, createGuiItem(Material.NETHERITE_SWORD, ChatColor.GOLD + "Kiliclar", ChatColor.GRAY + "Kilic kategorisini acmak icin tikla."));
        gui.setItem(11, createGuiItem(Material.NETHERITE_CHESTPLATE, ChatColor.GOLD + "Setler", ChatColor.GRAY + "Set kategorisini acmak icin tikla."));
        gui.setItem(12, createGuiItem(Material.NETHERITE_PICKAXE, ChatColor.GOLD + "Kazmalar", ChatColor.GRAY + "Kazma kategorisini acmak icin tikla."));
        gui.setItem(13, createGuiItem(Material.TRAPPED_CHEST, ChatColor.GOLD + "Trap Esyalari", ChatColor.GRAY + "Tuzak esyalarini acmak icin tikla."));
        gui.setItem(14, createGuiItem(Material.WHITE_CONCRETE, ChatColor.GOLD + "Bloklar", ChatColor.GRAY + "Blok kategorisini acmak icin tikla."));
        gui.setItem(15, createGuiItem(Material.ENCHANTED_GOLDEN_APPLE, ChatColor.GOLD + "Ozel Esyalar", ChatColor.GRAY + "Ozel esyalari acmak icin tikla."));

        player.openInventory(gui);
    }

    public void openSwordMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "Kilic Kategorisi");

        gui.setItem(10, createMarketItem(Material.NETHERITE_SWORD, ChatColor.RED + "Kes5 Alev2 Kır3 Netherite", 10000, Map.of(Enchantment.DAMAGE_ALL, 5, Enchantment.FIRE_ASPECT, 2, Enchantment.DURABILITY, 3)));
        gui.setItem(11, createMarketItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "Kes5 Al2 Kır3 Sav2 Elmas Kılıç", 7000, Map.of(Enchantment.DAMAGE_ALL, 5, Enchantment.FIRE_ASPECT, 2, Enchantment.DURABILITY, 3, Enchantment.KNOCKBACK, 2)));
        gui.setItem(12, createMarketItem(Material.NETHERITE_SWORD, ChatColor.DARK_PURPLE + "Kes6 Kır3 Al2 Netherite", 45000, Map.of(Enchantment.DAMAGE_ALL, 6, Enchantment.DURABILITY, 3, Enchantment.FIRE_ASPECT, 2)));
        gui.setItem(13, createMarketItem(Material.NETHERITE_SWORD, ChatColor.DARK_PURPLE + "Kes6 Savurma1 Kır3 Netherite", 45000, Map.of(Enchantment.DAMAGE_ALL, 6, Enchantment.KNOCKBACK, 1, Enchantment.DURABILITY, 3)));
        gui.setItem(14, createMarketItem(Material.NETHERITE_SWORD, ChatColor.GOLD + "Kes7 Kır3 Al2 Netherite", 150000, Map.of(Enchantment.DAMAGE_ALL, 7, Enchantment.DURABILITY, 3, Enchantment.FIRE_ASPECT, 2)));

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    public void openArmorMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "Set Kategorisi");
        Map<Enchantment, Integer> setEnchants = Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1);

        gui.setItem(10, createMarketItem(Material.NETHERITE_HELMET, ChatColor.GREEN + "Netherite Kask (P4 Kır3 Onarım)", 10000, setEnchants));
        gui.setItem(11, createMarketItem(Material.NETHERITE_CHESTPLATE, ChatColor.GREEN + "Netherite Zırh (P4 Kır3 Onarım)", 10000, setEnchants));
        gui.setItem(12, createMarketItem(Material.NETHERITE_LEGGINGS, ChatColor.GREEN + "Netherite Pantolon (P4 Kır3 Onarım)", 10000, setEnchants));
        gui.setItem(13, createMarketItem(Material.NETHERITE_BOOTS, ChatColor.GREEN + "Netherite Çizme (P4 Kır3 Onarım)", 10000, setEnchants));

        gui.setItem(15, createMarketItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ChatColor.GOLD + "Netherite Yükseltme Şablonu", 15000, null));

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    public void openPickaxeMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "Kazma Kategorisi");

        gui.setItem(9, createMarketItem(Material.NETHERITE_PICKAXE, "Ver5 Kır3 Onarım", 1000, Map.of(Enchantment.DIG_SPEED, 5, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(10, createMarketItem(Material.NETHERITE_PICKAXE, "Ver6 Kır3 Onarım", 2500, Map.of(Enchantment.DIG_SPEED, 6, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(11, createMarketItem(Material.NETHERITE_PICKAXE, "Ver7 Kır3 Onarım", 4000, Map.of(Enchantment.DIG_SPEED, 7, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(12, createMarketItem(Material.NETHERITE_PICKAXE, "Ver8 Kır3 Onarım", 6000, Map.of(Enchantment.DIG_SPEED, 8, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(13, createMarketItem(Material.NETHERITE_PICKAXE, "Ver9 Kır3 Onarım", 10000, Map.of(Enchantment.DIG_SPEED, 9, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(14, createMarketItem(Material.NETHERITE_PICKAXE, "Ver15 Kır3 Onarım", 70000, Map.of(Enchantment.DIG_SPEED, 15, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(15, createMarketItem(Material.NETHERITE_PICKAXE, "Ver25 Kır3 Onarım", 130000, Map.of(Enchantment.DIG_SPEED, 25, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(16, createMarketItem(Material.NETHERITE_PICKAXE, "Ver50 Kır3 Servet3 Onarım", 750000, Map.of(Enchantment.DIG_SPEED, 50, Enchantment.DURABILITY, 3, Enchantment.LOOT_BONUS_BLOCKS, 3, Enchantment.MENDING, 1)));

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    public void openTrapMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "Trap Kategorisi");

        gui.setItem(10, createStackMarketItem(Material.OAK_TRAPDOOR, "Tuzak Kapısı (Meşe)", 100, 64));
        gui.setItem(11, createStackMarketItem(Material.DARK_OAK_TRAPDOOR, "Tuzak Kapısı (Karanlık Meşe)", 100, 64));
        gui.setItem(12, createStackMarketItem(Material.IRON_TRAPDOOR, "Demir Tuzak Kapısı", 100, 64));
        gui.setItem(13, createStackMarketItem(Material.STRING, "İp", 100, 64));
        gui.setItem(14, createStackMarketItem(Material.REDSTONE, "Kızıltaş", 100, 64));
        gui.setItem(15, createStackMarketItem(Material.PISTON, "Piston", 100, 64));
        gui.setItem(16, createStackMarketItem(Material.STICKY_PISTON, "Yapışkan Piston", 100, 64));
        gui.setItem(19, createStackMarketItem(Material.LEVER, "Şalter", 320, 64));
        gui.setItem(20, createStackMarketItem(Material.WATER_BUCKET, "Su Kovası", 20, 1));

        gui.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    public void openBlockMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_GRAY + "Blok Kategorisi");

        Material[] concretes = {
                Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE,
                Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE, Material.GRAY_CONCRETE,
                Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE,
                Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE, Material.BLACK_CONCRETE
        };
        int slot = 0;
        for (Material c : concretes) {
            gui.setItem(slot++, createStackMarketItem(c, getCleanName(c.name()), 100, 64));
        }

        gui.setItem(slot++, createStackMarketItem(Material.OAK_LOG, "Meşe Odunu", 50, 64));
        gui.setItem(slot++, createStackMarketItem(Material.SPRUCE_LOG, "Ladin Odunu", 50, 64));

        gui.setItem(slot++, createStackMarketItem(Material.WHITE_WOOL, "Beyaz Yün", 50, 64));
        gui.setItem(slot++, createStackMarketItem(Material.RED_WOOL, "Kırmızı Yün", 50, 64));
        gui.setItem(slot++, createStackMarketItem(Material.BLACK_WOOL, "Siyah Yün", 50, 64));
        gui.setItem(slot++, createStackMarketItem(Material.BLUE_WOOL, "Mavi Yün", 50, 64));

        gui.setItem(49, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    public void openSpecialMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "Ozel Esyalar");

        gui.setItem(10, createMarketItem(Material.ENCHANTED_GOLDEN_APPLE, "Büyülü Altın Elma", 500, null));
        gui.setItem(11, createMarketItem(Material.ELYTRA, "Elitra (Kır3 Onarım)", 200000, Map.of(Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
        gui.setItem(12, createMarketItem(Material.SHIELD, "Kalkan", 500, null));
        gui.setItem(13, createStackMarketItem(Material.FIREWORK_ROCKET, "Fişek", 15, 1));
        gui.setItem(14, createStackMarketItem(Material.EXPERIENCE_BOTTLE, "XP Şişesi (1 Stak)", 800, 64));
        gui.setItem(15, createMarketItem(Material.BOW, "Yay", 50, null));
        gui.setItem(16, createPotionItem("Ateş Direnci (İçilen 3dk)", PotionEffectType.FIRE_RESISTANCE, 3 * 60 * 20, false, 2000));
        gui.setItem(19, createPotionItem("Ateş Direnci (Atılan 3dk)", PotionEffectType.FIRE_RESISTANCE, 3 * 60 * 20, true, 2000));
        gui.setItem(20, createPotionItem("Ateş Direnci (8dk)", PotionEffectType.FIRE_RESISTANCE, 8 * 60 * 20, false, 2000));

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

        ItemStack paper = new ItemStack(Material.PAPER, amount);
        ItemMeta pMeta = paper.getItemMeta();
        pMeta.setDisplayName(ChatColor.GREEN + "Almak için tıkla");
        pMeta.setLore(List.of(ChatColor.AQUA + "₺" + String.format("%.2f", totalPrice)));
        paper.setItemMeta(pMeta);
        gui.setItem(22, paper);

        gui.setItem(24, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+1", ""));
        gui.setItem(25, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+16", ""));
        gui.setItem(26, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "+32", ""));
        gui.setItem(23, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.GREEN + "64 Yap", ""));

        ItemStack displayItem = target.clone();
        displayItem.setAmount(amount);
        gui.setItem(13, displayItem);

        gui.setItem(40, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
    }

    // --- EVENT LISTENER ---

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Magazasi") ||
            event.getView().getTitle().contains("Kategorisi") ||
            event.getView().getTitle().contains("Esyalar") ||
            event.getView().getTitle().contains("Satın Alma Yeri")) {

            event.setCancelled(true);

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();

            if (clicked.getType() == Material.BARRIER) {
                player.closeInventory();
                return;
            }

            if (event.getView().getTitle().contains("Sunucu Magazasi")) {
                if (clicked.getType() == Material.NETHERITE_SWORD) openSwordMenu(player);
                else if (clicked.getType() == Material.NETHERITE_CHESTPLATE) openArmorMenu(player);
                else if (clicked.getType() == Material.NETHERITE_PICKAXE) openPickaxeMenu(player);
                else if (clicked.getType() == Material.TRAPPED_CHEST) openTrapMenu(player);
                else if (clicked.getType() == Material.WHITE_CONCRETE) openBlockMenu(player);
                else if (clicked.getType() == Material.ENCHANTED_GOLDEN_APPLE) openSpecialMenu(player);
                return;
            }

            if (!event.getView().getTitle().contains("Satın Alma Yeri")) {
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

            if (event.getView().getTitle().contains("Satın Alma Yeri")) {
                int currentAmt = selectedAmount.getOrDefault(player.getUniqueId(), 1);

                if (clicked.getType() == Material.LIME_STAINED_GLASS_PANE) {
                    String name = clicked.getItemMeta().getDisplayName();
                    if (name.contains("+1")) currentAmt += 1;
                    else if (name.contains("+16")) currentAmt += 16;
                    else if (name.contains("+32")) currentAmt += 32;
                    else if (name.contains("64 Yap")) currentAmt = 64;
                } else if (clicked.getType() == Material.RED_STAINED_GLASS_PANE) {
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

    private void executePurchase(Player player) {
        int amount = selectedAmount.getOrDefault(player.getUniqueId(), 1);
        double unitPrice = selectedUnitPrice.getOrDefault(player.getUniqueId(), 0.0);
        double totalCost = unitPrice * amount;
        ItemStack itemToGive = selectedItem.get(player.getUniqueId()).clone();

        if (econ.getBalance(player) < totalCost) {
            player.sendMessage(ChatColor.RED + "Yetersiz bakiye! Gerekli: ₺" + totalCost);
            return;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(ChatColor.RED + "Envanterin dolu! Lütfen yer açıp tekrar dene.");
            return;
        }

        econ.withdrawPlayer(player, totalCost);
        itemToGive.setAmount(amount * itemToGive.getAmount());
        player.getInventory().addItem(itemToGive);

        player.sendMessage(ChatColor.GREEN + "Başarıyla " + amount + " adet satın aldın! Ödenen: ₺" + totalCost);
        player.closeInventory();
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

    private ItemStack createStackMarketItem(Material material, String name, double price, int stackSize) {
        ItemStack item = new ItemStack(material, stackSize);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(List.of(ChatColor.GREEN + "Fiyat: ₺" + price, ChatColor.YELLOW + "Satın almak için tıkla"));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPotionItem(String name, PotionEffectType type, int duration, boolean splash, double price) {
        ItemStack potion = new ItemStack(splash ? Material.SPLASH_POTION : Material.POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setDisplayName(name);
        meta.addCustomEffect(new PotionEffect(type, duration, 0), true);
        meta.setLore(List.of(ChatColor.GREEN + "Fiyat: ₺" + price, ChatColor.YELLOW + "Satın almak için tıkla"));
        potion.setItemMeta(meta);
        return potion;
    }

    private double getPriceFromLore(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            for (String line : item.getItemMeta().getLore()) {
                if (line.contains("Fiyat: ₺")) {
                    try {
                        return Double.parseDouble(ChatColor.stripColor(line).replace("Fiyat: ₺", ""));
                    } catch (Exception ignored) {}
                }
            }
        }
        return 0.0;
    }

    private String getCleanName(String name) {
        String[] words = name.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}

