package com.craftaxo.axomarket;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MarketGUI {

    // ANA MENÜ
    public static void openMainMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "AxoMarket - Ana Menü");

        inv.setItem(10, createGuiItem(Material.STONE, ChatColor.GREEN + "Bloklar", ChatColor.GRAY + "Tüm bloklar: 20 TL"));
        inv.setItem(12, createGuiItem(Material.DIAMOND_SWORD, ChatColor.RED + "Kılıçlar & Ekipmanlar", ChatColor.GRAY + "Özel Büyülü Ekipmanlar"));
        inv.setItem(14, createGuiItem(Material.DIAMOND_CHESTPLATE, ChatColor.BLUE + "Zırh Setleri", ChatColor.GRAY + "Koruma Setleri"));
        inv.setItem(16, createGuiItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ChatColor.GOLD + "Madenler & Şablonlar", ChatColor.GRAY + "Maden: 50 TL | Şablon: 2.000 TL"));

        player.openInventory(inv);
    }

    // BLOKLAR MENÜSÜ (20 TL)
    public static void openBlocksMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "AxoMarket - Bloklar");

        Material[] blocks = {
                Material.STONE, Material.COBBLESTONE, Material.SMOOTH_STONE, Material.GRASS_BLOCK,
                Material.DIRT, Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG,
                Material.GLASS, Material.WHITE_WOOL, Material.BRICKS, Material.NETHER_BRICKS,
                Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.SAND, Material.GRAVEL
        };

        for (int i = 0; i < blocks.length; i++) {
            inv.setItem(i, createPriceItem(blocks[i], 1, 20.0, "Blok"));
        }

        inv.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Geri Dön"));
        player.openInventory(inv);
    }

    // KILIÇLAR & EKİPMANLAR MENÜSÜ
    public static void openWeaponsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "AxoMarket - Ekipmanlar");

        // Elmas Kılıç (Savurma 2 Büyülü)
        ItemStack diamondSword = new ItemStack(Material.DIAMOND_SWORD);
        diamondSword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
        diamondSword.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
        inv.setItem(10, createPriceItemCustom(diamondSword, ChatColor.AQUA + "Elmas Kılıç (Savurma II)", 500.0));

        // Keskinlik 6 Netherite Kılıç
        ItemStack kes6Sword = new ItemStack(Material.NETHERITE_SWORD);
        kes6Sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 6);
        inv.setItem(12, createPriceItemCustom(kes6Sword, ChatColor.GOLD + "Netherite Kılıç (Keskinlik VI)", 1500.0));

        // Keskinlik 7 Netherite Kılıç
        ItemStack kes7Sword = new ItemStack(Material.NETHERITE_SWORD);
        kes7Sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 7);
        inv.setItem(13, createPriceItemCustom(kes7Sword, ChatColor.LIGHT_PURPLE + "Netherite Kılıç (Keskinlik VII)", 3000.0));

        // Kazma (Servet 3 / Verimlilik 6)
        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        pickaxe.addUnsafeEnchantment(Enchantment.DIG_SPEED, 6);
        pickaxe.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
        inv.setItem(14, createPriceItemCustom(pickaxe, ChatColor.YELLOW + "Verimlilik VI Kazma", 1200.0));

        // Elytra
        ItemStack elytra = new ItemStack(Material.ELYTRA);
        elytra.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        inv.setItem(16, createPriceItemCustom(elytra, ChatColor.DARK_PURPLE + "Elytra (Kırılmazlık III)", 2500.0));

        inv.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Geri Dön"));
        player.openInventory(inv);
    }

    // ZIRH SETLERİ MENÜSÜ
    public static void openArmorMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GRAY + "AxoMarket - Zırhlar");

        ItemStack helm = new ItemStack(Material.NETHERITE_HELMET);
        helm.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        inv.setItem(10, createPriceItemCustom(helm, ChatColor.GOLD + "Netherite Kask (Koruma V)", 800.0));

        ItemStack chest = new ItemStack(Material.NETHERITE_CHESTPLATE);
        chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        inv.setItem(11, createPriceItemCustom(chest, ChatColor.GOLD + "Netherite Göğüslük (Koruma V)", 1200.0));

        ItemStack legs = new ItemStack(Material.NETHERITE_LEGGINGS);
        legs.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        inv.setItem(12, createPriceItemCustom(legs, ChatColor.GOLD + "Netherite Pantolon (Koruma V)", 1000.0));

        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
        inv.setItem(13, createPriceItemCustom(boots, ChatColor.GOLD + "Netherite Bot (Koruma V)", 800.0));

        inv.setItem(22, createGuiItem(Material.BARRIER, ChatColor.RED + "Geri Dön"));
        player.openInventory(inv);
    }

    // MADENLER (50 TL) VE DEMİRCİ ŞABLONLARI (2000 TL)
    public static void openOresAndTemplatesMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36, ChatColor.DARK_GRAY + "AxoMarket - Maden & Şablon");

        // Madenler (50 TL)
        inv.setItem(0, createPriceItem(Material.DIAMOND, 1, 50.0, "Maden"));
        inv.setItem(1, createPriceItem(Material.NETHERITE_INGOT, 1, 50.0, "Maden"));
        inv.setItem(2, createPriceItem(Material.REDSTONE, 1, 50.0, "Maden"));
        inv.setItem(3, createPriceItem(Material.GOLD_INGOT, 1, 50.0, "Maden"));
        inv.setItem(4, createPriceItem(Material.EMERALD, 1, 50.0, "Maden"));
        inv.setItem(5, createPriceItem(Material.AMETHYST_SHARD, 1, 50.0, "Maden (Mor Cevher)"));

        // 1.20 Demirci Şablonları (Smithing Templates - 2000 TL)
        Material[] templates = {
                Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.HOST_ARMOR_TRIM_SMITHING_TEMPLATE, Material.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
                Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE
        };

        int slot = 9;
        for (Material t : templates) {
            if (slot < 27) {
                inv.setItem(slot++, createPriceItem(t, 1, 2000.0, "Şablon"));
            }
        }

        inv.setItem(31, createGuiItem(Material.BARRIER, ChatColor.RED + "Geri Dön"));
        player.openInventory(inv);
    }

    private static ItemStack createGuiItem(Material mat, String name, String... lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String l : lore) loreList.add(l);
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createPriceItem(Material mat, int amount, double price, String category) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            if (category != null) lore.add(ChatColor.DARK_GRAY + "[" + category + "]");
            lore.add(ChatColor.GOLD + "Fiyat: " + ChatColor.YELLOW + price + " TL");
            lore.add(" ");
            lore.add(ChatColor.GREEN + " Sol Tık: 1 Adet Satın Al");
            lore.add(ChatColor.AQUA + " Sağ Tık: 64 Adet Satın Al");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack createPriceItemCustom(ItemStack item, String customName, double price) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(customName);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GOLD + "Fiyat: " + ChatColor.YELLOW + price + " TL");
            lore.add(" ");
            lore.add(ChatColor.GREEN + " Sol Tık: 1 Adet Satın Al");
            lore.add(ChatColor.AQUA + " Sağ Tık: 64 Adet Satın Al");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
}
