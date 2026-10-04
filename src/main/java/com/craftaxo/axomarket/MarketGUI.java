package com.craftaxo.axomarket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MarketGUI {

    // Kapatma ve Çıkış Butonu
    public static ItemStack getCloseButton() {
        ItemStack barrier = new ItemStack(Material.BARRIER);
        ItemMeta meta = barrier.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Kapat");
            barrier.setItemMeta(meta);
        }
        return barrier;
    }

    // Ana Menü ( /market )
    public static void openMainMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Sunucu Marketi");

        // Kategori İtemleri
        gui.setItem(19, createCategory(Material.NETHERITE_SWORD, "⚔ Kılıçlar", "Kılıç kategorisine git"));
        gui.setItem(20, createCategory(Material.NETHERITE_PICKAXE, "⛏ Kazmalar", "Kazma kategorisine git"));
        gui.setItem(21, createCategory(Material.NETHERITE_CHESTPLATE, "🛡 Setler & Zırhlar", "Zırh ve şablon kategorisine git"));
        gui.setItem(22, createCategory(Material.ENCHANTED_GOLDEN_APPLE, "✨ Özel Eşyalar", "Elitra, Altın Elma, Pot ve Oklar"));
        gui.setItem(23, createCategory(Material.OAK_TRAPDOOR, "🪤 Trap Eşyaları", "Tuzak ve Kızıltaş malzemeleri"));
        gui.setItem(24, createCategory(Material.WHITE_CONCRETE, "🧱 Bloklar", "Beton, Odun ve Yünler"));

        // Kapat Butonu (Sağ Alt)
        gui.setItem(44, getCloseButton());

        player.openInventory(gui);
    }

    // KILIÇ KATEGORİSİ
    public static void openSwordCategory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Kılıç Kategorisi");

        gui.setItem(10, createMarketItem(Material.NETHERITE_SWORD, "Netherite Kılıç", 10000, "Keskinlik 5", "Alevden Çehre 2"));
        gui.setItem(11, createMarketItem(Material.DIAMOND_SWORD, "Elmas Kılıç", 7000, "Keskinlik 5", "Alevden Çehre 2", "Kırılmazlık 3"));
        gui.setItem(12, createMarketItem(Material.NETHERITE_SWORD, "Kes6 Netherite Kılıç", 45000, "Keskinlik 6", "Kırılmazlık 3", "Alevden Çehre 2"));
        gui.setItem(13, createMarketItem(Material.NETHERITE_SWORD, "Kes6 Savurma Kılıç", 45000, "Keskinlik 6", "Savurma 1", "Kırılmazlık 3"));
        gui.setItem(14, createMarketItem(Material.NETHERITE_SWORD, "Kes7 Netherite Kılıç", 150000, "Keskinlik 7", "Kırılmazlık 3", "Alevden Çehre 2"));

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // KAZMA KATEGORİSİ
    public static void openPickaxeCategory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Kazma Kategorisi");

        gui.setItem(10, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V5", 1000, "Verimlilik 5", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(11, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V6", 2500, "Verimlilik 6", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(12, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V7", 4000, "Verimlilik 7", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(13, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V8", 6000, "Verimlilik 8", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(14, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V9", 10000, "Verimlilik 9", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(15, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V15", 70000, "Verimlilik 15", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(16, createMarketItem(Material.NETHERITE_PICKAXE, "Netherite Kazma V25", 130000, "Verimlilik 25", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(19, createMarketItem(Material.NETHERITE_PICKAXE, "EFSANEVİ KAZMA V50", 750000, "Verimlilik 50", "Kırılmazlık 3", "Servet 3", "Tamir 1"));

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // SETLER KATEGORİSİ
    public static void openArmorCategory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Zırh Kategorisi");

        gui.setItem(10, createMarketItem(Material.NETHERITE_HELMET, "Netherite Kask", 10000, "Koruma 4", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(11, createMarketItem(Material.NETHERITE_CHESTPLATE, "Netherite Zırh", 10000, "Koruma 4", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(12, createMarketItem(Material.NETHERITE_LEGGINGS, "Netherite Pantolon", 10000, "Koruma 4", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(13, createMarketItem(Material.NETHERITE_BOOTS, "Netherite Bot", 10000, "Koruma 4", "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(15, createMarketItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, "Netherite Yükseltme Şablonu", 15000));

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // ÖZEL EŞYALAR KATEGORİSİ
    public static void openSpecialCategory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Özel Eşya Kategorisi");

        gui.setItem(10, createMarketItem(Material.GOLDEN_APPLE, "Büyülü Altın Elma", 5));
        gui.setItem(11, createMarketItem(Material.ELYTRA, "Elitra", 200000, "Kırılmazlık 3", "Tamir 1"));
        gui.setItem(12, createPotionItem("Ateş Direnci İksiri (3 Dk)", 2000, false, 180));
        gui.setItem(13, createPotionItem("Atılabilir Ateş Direnci (3 Dk)", 2000, true, 180));
        gui.setItem(14, createPotionItem("Atılabilir Ateş Direnci (8 Dk)", 2000, true, 480));
        gui.setItem(15, createMarketItem(Material.SHIELD, "Kalkan", 500));
        gui.setItem(19, createMarketItem(Material.FIREWORK_ROCKET, "Havai Fişek", 15));
        gui.setItem(20, createMarketItem(Material.EXPERIENCE_BOTTLE, "Tecrübe Şişesi (1 Stack)", 800));
        gui.setItem(21, createMarketItem(Material.BOW, "Yay", 50));
        gui.setItem(22, createMarketItem(Material.TIPPED_ARROW, "Ateş Direnci Oku (1 Dk)", 100));

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // TRAP KATEGORİSİ
    public static void openTrapCategory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Trap Kategorisi");

        gui.setItem(10, createMarketItem(Material.OAK_TRAPDOOR, "Meşe Tuzak Kapısı (64x)", 100));
        gui.setItem(11, createMarketItem(Material.DARK_OAK_TRAPDOOR, "Koyu Meşe Tuzak Kapısı (64x)", 100));
        gui.setItem(12, createMarketItem(Material.SPRUCE_TRAPDOOR, "Ladin Tuzak Kapısı (64x)", 100));
        gui.setItem(13, createMarketItem(Material.STRING, "İp (64x)", 100));
        gui.setItem(14, createMarketItem(Material.REDSTONE, "Kızıltaş (64x)", 100));
        gui.setItem(15, createMarketItem(Material.PISTON, "Piston (64x)", 100));
        gui.setItem(16, createMarketItem(Material.STICKY_PISTON, "Yapışkan Piston (64x)", 100));
        gui.setItem(19, createMarketItem(Material.LEVER, "Şalter", 5));
        gui.setItem(20, createMarketItem(Material.WATER_BUCKET, "Su Kovası", 20));

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // BLOK KATEGORİSİ
    public static void openBlockCategory(Player player) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Blok Kategorisi");

        gui.setItem(10, createMarketItem(Material.WHITE_CONCRETE, "Beyaz Beton (64x)", 100));
        gui.setItem(11, createMarketItem(Material.BLACK_CONCRETE, "Siyah Beton (64x)", 100));
        gui.setItem(12, createMarketItem(Material.RED_CONCRETE, "Kırmızı Beton (64x)", 100));
        gui.setItem(13, createMarketItem(Material.OAK_LOG, "Meşe Odunu (64x)", 50));
        gui.setItem(14, createMarketItem(Material.WHITE_WOOL, "Beyaz Yün (64x)", 50));
        gui.setItem(15, createMarketItem(Material.RED_WOOL, "Kırmızı Yün (64x)", 50));
        gui.setItem(16, createMarketItem(Material.BLUE_WOOL, "Mavi Yün (64x)", 50));
        gui.setItem(19, createMarketItem(Material.BLACK_WOOL, "Siyah Yün (64x)", 50));

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // MİKTARLI SATIN ALMA EKRANI (Görsel 3 ve 4[span_8](start_span)[span_8](end_span)[span_9](start_span)[span_9](end_span))
    public static void openBuyMenu(Player player, ItemStack targetItem, double singlePrice, int currentAmount) {
        Inventory gui = Bukkit.createInventory(null, 45, ChatColor.DARK_GRAY + "Satın Alma Ekranı");

        double totalPrice = singlePrice * currentAmount;

        // Miktar Eksiltme (Kırmızı Camlar)
        gui.setItem(18, createAmountButton(Material.RED_STAINED_GLASS_PANE, "-32", -32));
        gui.setItem(19, createAmountButton(Material.RED_STAINED_GLASS_PANE, "-16", -16));
        gui.setItem(20, createAmountButton(Material.RED_STAINED_GLASS_PANE, "-1", -1));

        // Ortada Alınacak İtem Gösterimi
        ItemStack preview = targetItem.clone();
        preview.setAmount(Math.max(1, Math.min(64, currentAmount)));
        gui.setItem(22, preview);

        // Miktar Arttırma (Yeşil Camlar)
        gui.setItem(24, createAmountButton(Material.LIME_STAINED_GLASS_PANE, "+1", 1));
        gui.setItem(25, createAmountButton(Material.LIME_STAINED_GLASS_PANE, "+16", 16));
        gui.setItem(26, createAmountButton(Material.LIME_STAINED_GLASS_PANE, "+32", 32));

        // Satın Al Kağıdı (Alt Ortada)[span_10](start_span)[span_10](end_span)
        ItemStack buyPaper = new ItemStack(Material.PAPER);
        ItemMeta pMeta = buyPaper.getItemMeta();
        if (pMeta != null) {
            pMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Almak için tıkla");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.AQUA + "Miktar: " + ChatColor.WHITE + currentAmount);
            lore.add(ChatColor.GOLD + "Toplam Fiyat: " + ChatColor.YELLOW + "₺" + String.format("%.2f", totalPrice));
            pMeta.setLore(lore);
            buyPaper.setItemMeta(pMeta);
        }
        gui.setItem(31, buyPaper);

        gui.setItem(44, getCloseButton());
        player.openInventory(gui);
    }

    // Yardımcı Metotlar
    private static ItemStack createCategory(Material mat, String title, String desc) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + title);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + desc);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createMarketItem(Material mat, String name, double price, String... enchants) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + name);
            List<String> lore = new ArrayList<>();
            for (String ench : enchants) {
                lore.add(ChatColor.BLUE + ench);
            }
            lore.add("");
            lore.add(ChatColor.GREEN + "Alış Fiyatı: " + ChatColor.DARK_GREEN + "₺" + String.format("%.2f", price));
            lore.add(ChatColor.YELLOW + "» Tıkla ve Satın Al");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createPotionItem(String name, double price, boolean isSplash, int durationSec) {
        Material mat = isSplash ? Material.SPLASH_POTION : Material.POTION;
        ItemStack item = new ItemStack(mat);
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + name);
            meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, durationSec * 20, 0), true);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GREEN + "Alış Fiyatı: " + ChatColor.DARK_GREEN + "₺" + String.format("%.2f", price));
            lore.add(ChatColor.YELLOW + "» Tıkla ve Satın Al");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createAmountButton(Material mat, String name, int change) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName((change > 0 ? ChatColor.GREEN : ChatColor.RED) + name);
            item.setItemMeta(meta);
        }
        return item;
    }
}
