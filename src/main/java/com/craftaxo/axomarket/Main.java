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
    private final Map<UUID, ItemStack> tempEditingItem = new HashMap<>();\n    private final Map<UUID, String> editorTitle = new HashMap<>();\n    private final Map<UUID, Integer> editorSlot = new HashMap<>();

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

    private void loadEditorContents(String key, Inventory inv) {
        Object raw=getConfig().get("editor."+key+".contents");
        if(raw instanceof ItemStack[]){ItemStack[] arr=(ItemStack[])raw;for(int i=0;i<Math.min(arr.length,inv.getSize());i++)inv.setItem(i,arr[i]);}
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
        int size = categoryName.contains("Blok") ? 54 : 36;
        Inventory gui = Bukkit.createInventory(null, size, titlePrefix + categoryName);

        if (categoryName.contains("Kılıç")) {
            gui.setItem(10, createMarketItem(Material.NETHERITE_SWORD, ChatColor.RED + "Keskinlik V Alevden Çehre II", 1.5, Map.of(Enchantment.DAMAGE_ALL, 5, Enchantment.FIRE_ASPECT, 2)));
            gui.setItem(11, createMarketItem(Material.DIAMOND_SWORD, ChatColor.AQUA + "Keskinlik V Savurma I", 1.5, Map.of(Enchantment.DAMAGE_ALL, 5, Enchantment.KNOCKBACK, 1)));
            gui.setItem(12, createMarketItem(Material.NETHERITE_SWORD, ChatColor.DARK_PURPLE + "Keskinlik VI Kırılmazlık III Alevden Çehre II", 40000.0, Map.of(Enchantment.DAMAGE_ALL, 6, Enchantment.DURABILITY, 3, Enchantment.FIRE_ASPECT, 2)));
            gui.setItem(13, createMarketItem(Material.NETHERITE_SWORD, ChatColor.DARK_PURPLE + "Keskinlik VI Savurma I Kırılmazlık III", 40000.0, Map.of(Enchantment.DAMAGE_ALL, 6, Enchantment.KNOCKBACK, 1, Enchantment.DURABILITY, 3)));
            gui.setItem(14, createMarketItem(Material.NETHERITE_SWORD, ChatColor.GOLD + "Keskinlik VII Kırılmazlık III Alevden Çehre II", 150000.0, Map.of(Enchantment.DAMAGE_ALL, 7, Enchantment.DURABILITY, 3, Enchantment.FIRE_ASPECT, 2)));
        } else if (categoryName.contains("Set")) {
            Map<Enchantment, Integer> ench = Map.of(Enchantment.PROTECTION_ENVIRONMENTAL, 4, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1);
            gui.setItem(10, createMarketItem(Material.NETHERITE_HELMET, "Koruma IV Kırılmazlık III Onarım", 1.5, ench));
            gui.setItem(11, createMarketItem(Material.NETHERITE_CHESTPLATE, "Koruma IV Kırılmazlık III Onarım", 1.5, ench));
            gui.setItem(12, createMarketItem(Material.NETHERITE_LEGGINGS, "Koruma IV Kırılmazlık III Onarım", 1.5, ench));
            gui.setItem(13, createMarketItem(Material.NETHERITE_BOOTS, "Koruma IV Kırılmazlık III Onarım", 1.5, ench));
            gui.setItem(15, createMarketItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, "Netherite Yükseltme Şablonu", 1.5, null));
        } else if (categoryName.contains("Kazma")) {
            int[] levels = {5, 6, 7, 8, 9, 15, 25, 50};
            double[] prices = {1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1.5, 1000000.0};
            for (int i = 0; i < levels.length; i++) {
                int level = levels[i];
                Map<Enchantment, Integer> ench = level == 50 ? Map.of(Enchantment.DIG_SPEED, level, Enchantment.DURABILITY, 3, Enchantment.LOOT_BONUS_BLOCKS, 3, Enchantment.MENDING, 1) : Map.of(Enchantment.DIG_SPEED, level, Enchantment.DURABILITY, 3, Enchantment.MENDING, 1);
                String label = "Verimlilik " + (level == 50 ? "L" : level == 15 ? "XV" : level == 25 ? "XXV" : level) + " Kırılmazlık III" + (level == 50 ? " Servet III" : "") + " Onarım";
                gui.setItem(9 + i, createMarketItem(Material.NETHERITE_PICKAXE, label, prices[i], ench));
            }
        } else if (categoryName.contains("Trap")) {
            gui.setItem(10, createStackMarketItem(Material.OAK_TRAPDOOR, "Meşe Tuzak Kapısı", 1.5, 64));
            gui.setItem(11, createStackMarketItem(Material.DARK_OAK_TRAPDOOR, "Koyu Meşe Tuzak Kapısı", 1.5, 64));
            gui.setItem(13, createStackMarketItem(Material.STRING, "İp", 1.5, 64));
            gui.setItem(14, createStackMarketItem(Material.REDSTONE, "Kızıltaş", 1.5, 64));
            gui.setItem(15, createStackMarketItem(Material.PISTON, "Piston", 1.5, 64));
            gui.setItem(16, createStackMarketItem(Material.STICKY_PISTON, "Yapışkan Piston", 1.5, 64));
            gui.setItem(19, createStackMarketItem(Material.LEVER, "Şalter", 1.5, 32));
            gui.setItem(20, createStackMarketItem(Material.WATER_BUCKET, "Su Kovası", 1.5, 1));
        } else if (categoryName.contains("Blok")) {
            Material[] mats = {Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE, Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE, Material.GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE, Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE, Material.BLACK_CONCRETE, Material.OAK_LOG, Material.SPRUCE_LOG, Material.WHITE_WOOL, Material.RED_WOOL, Material.BLACK_WOOL, Material.BLUE_WOOL};
            int slot = 0;
            for (Material mat : mats) gui.setItem(slot++, createStackMarketItem(mat, getCleanName(mat.name()), 1.5, 64));
        } else if (categoryName.contains("Özel")) {
            gui.setItem(10, createStackMarketItem(Material.GOLDEN_APPLE, "Altın Elma (1 Stak)", 130.0, 64));
            gui.setItem(11, createMarketItem(Material.ELYTRA, "Elitra (Kırılmazlık III Onarım)", 200000.0, Map.of(Enchantment.DURABILITY, 3, Enchantment.MENDING, 1)));
            gui.setItem(12, createMarketItem(Material.TOTEM_OF_UNDYING, "Totem", 150000.0, null));
            gui.setItem(13, createMarketItem(Material.SHIELD, "Kalkan", 1.5, null));
            gui.setItem(14, createStackMarketItem(Material.FIREWORK_ROCKET, "Fişek", 1.5, 1));
            gui.setItem(15, createStackMarketItem(Material.EXPERIENCE_BOTTLE, "XP Şişesi (1 Stak)", 1.5, 64));
            gui.setItem(16, createMarketItem(Material.BOW, "Yay", 10.0, null));
            gui.setItem(19, createPotionItem("Ateş Direnci 5 dk (İçilen)", PotionEffectType.FIRE_RESISTANCE, 5 * 60 * 20, false, 2000.0));
            gui.setItem(20, createPotionItem("Ateş Direnci 5 dk (Atılan)", PotionEffectType.FIRE_RESISTANCE, 5 * 60 * 20, true, 2000.0));
            gui.setItem(21, createPotionItem("Ateş Direnci 8 dk (İçilen)", PotionEffectType.FIRE_RESISTANCE, 8 * 60 * 20, false, 2000.0));
            gui.setItem(22, createPotionArrowItem("Ateş Direnci Oku (32 adet)", 40.0, 32));
        }

        loadEditorContents(titlePrefix + categoryName, gui);\n        if (editMode) gui.setItem(26, createGuiItem(Material.NETHER_STAR, ChatColor.GREEN + "+ Bu Kategoriye Ürün Ekle", ""));
        gui.setItem(size == 54 ? 49 : 31, createGuiItem(Material.BARRIER, ChatColor.RED + "Kapat", ""));
        player.openInventory(gui);
    }

    private ItemStack createPotionArrowItem(String name, double price, int amount) {
        ItemStack arrows = new ItemStack(Material.TIPPED_ARROW, amount);
        PotionMeta meta = (PotionMeta) arrows.getItemMeta();
        meta.setDisplayName(name);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 60 * 20, 0), true);
        meta.setLore(List.of(ChatColor.GREEN + "Fiyat: ₺" + price, ChatColor.YELLOW + "Satın almak için tıkla"));
        arrows.setItemMeta(meta);
        return arrows;
    }

    private String getCleanName(String name) {
        String[] words = name.toLowerCase(Locale.ROOT).split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        return sb.toString().trim();
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

            if (isEditMode) {
                if (clicked.getType()==Material.NETHER_STAR) {
                    player.closeInventory();
                    if(title.contains("Market Düzenleme")) { editingState.put(player.getUniqueId(),"WAITING_NEW_ITEM_NAME"); editorTitle.put(player.getUniqueId(),title); player.sendMessage(ChatColor.YELLOW+"Yeni kategori/ürün adını yazın:"); }
                    else { editingState.put(player.getUniqueId(),"WAITING_NEW_ITEM_NAME"); editorTitle.put(player.getUniqueId(),title); player.sendMessage(ChatColor.YELLOW+"Yeni ürün adını yazın:"); }
                    return;
                }
                if(event.getClick()==ClickType.SHIFT_LEFT) { editingState.put(player.getUniqueId(),"WAITING_RENAME");editorTitle.put(player.getUniqueId(),title);editorSlot.put(player.getUniqueId(),event.getSlot());player.closeInventory();player.sendMessage(ChatColor.YELLOW+"Yeni ismi yazın:");return; }
                if(event.getClick()==ClickType.LEFT) { editingState.put(player.getUniqueId(),"WAITING_PRICE");editorTitle.put(player.getUniqueId(),title);editorSlot.put(player.getUniqueId(),event.getSlot());player.closeInventory();player.sendMessage(ChatColor.YELLOW+"Yeni fiyatı yazın (10k/1500):");return; }
                if(event.getClick()==ClickType.RIGHT) { event.getInventory().setItem(event.getSlot(),null);getConfig().set("editor."+title+".contents",event.getInventory().getContents());saveConfig();player.sendMessage(ChatColor.GREEN+"Menüden kaldırıldı.");return; }
                return;
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
        Player player=event.getPlayer(); UUID id=player.getUniqueId(); String state=editingState.get(id);
        if(state==null)return; event.setCancelled(true); String msg=event.getMessage().trim();
        Bukkit.getScheduler().runTask(this,()->{
            if(state.equals("WAITING_NEW_ITEM_NAME")){
                ItemStack it=new ItemStack(Material.PAPER); ItemMeta m=it.getItemMeta();m.setDisplayName(ChatColor.WHITE+msg);it.setItemMeta(m);tempEditingItem.put(id,it);
                editingState.put(id,"WAITING_NEW_ITEM_MATERIAL");player.sendMessage(ChatColor.YELLOW+"Simge materyalini yazın (TORCH/MEŞALE):");
            } else if(state.equals("WAITING_NEW_ITEM_MATERIAL")){
                Material mat=Material.matchMaterial(msg.toUpperCase(Locale.ROOT).replace(' ','_')); if(msg.equalsIgnoreCase("meşale"))mat=Material.TORCH;
                if(mat==null){player.sendMessage(ChatColor.RED+"Materyal tanınmadı, tekrar yazın.");return;}
                tempEditingItem.get(id).setType(mat);editingState.put(id,"WAITING_NEW_ITEM_PRICE");player.sendMessage(ChatColor.YELLOW+"Fiyatı yazın (10k/1500):");
            } else if(state.equals("WAITING_NEW_ITEM_PRICE")||state.equals("WAITING_PRICE")){
                String t=editorTitle.get(id); int slot=editorSlot.getOrDefault(id,-1);
                double price; try{String v=msg.toLowerCase(Locale.ROOT).replace(",",".");double mult=1;if(v.endsWith("k")){mult=1000;v=v.substring(0,v.length()-1);}else if(v.endsWith("m")){mult=1000000;v=v.substring(0,v.length()-1);}price=Double.parseDouble(v)*mult;}catch(Exception e){player.sendMessage(ChatColor.RED+"Fiyat hatalı, tekrar yazın.");return;}
                Inventory inv=Bukkit.createInventory(null,t!=null&&t.contains("Blok")?54:36,t);ItemStack[] saved=getConfig().getList("editor."+t+".contents",new ArrayList<>()).toArray(new ItemStack[0]);
                for(int i=0;i<Math.min(saved.length,inv.getSize());i++)inv.setItem(i,saved[i]);
                ItemStack item=state.equals("WAITING_NEW_ITEM_PRICE")?tempEditingItem.get(id):(slot>=0?inv.getItem(slot):null);
                if(item!=null){ItemMeta m=item.getItemMeta();List<String> lore=m.hasLore()?new ArrayList<>(m.getLore()):new ArrayList<>();lore.removeIf(x->ChatColor.stripColor(x).startsWith("Fiyat:"));lore.add(0,ChatColor.GREEN+"Fiyat: ₺"+price);m.setLore(lore);item.setItemMeta(m);if(state.equals("WAITING_NEW_ITEM_PRICE")){int free=-1;for(int i=0;i<inv.getSize();i++)if(inv.getItem(i)==null){free=i;break;}if(free>=0)inv.setItem(free,item);}else inv.setItem(slot,item);
                    getConfig().set("editor."+t+".contents",inv.getContents());saveConfig();player.sendMessage(ChatColor.GREEN+"Fiyat/ürün kaydedildi!");}
                editingState.remove(id);tempEditingItem.remove(id);if(t!=null)openSubMenu(player,t.replace(ChatColor.DARK_RED+"[EDİT] ",""),true);
            } else if(state.equals("WAITING_RENAME")){
                String t=editorTitle.get(id);int slot=editorSlot.getOrDefault(id,-1);Inventory inv=Bukkit.createInventory(null,t!=null&&t.contains("Blok")?54:36,t);
                Object raw=getConfig().get("editor."+t+".contents");if(raw instanceof ItemStack[]){ItemStack[] arr=(ItemStack[])raw;for(int i=0;i<Math.min(arr.length,inv.getSize());i++)inv.setItem(i,arr[i]);}
                ItemStack it=slot>=0?inv.getItem(slot):null;if(it!=null){ItemMeta m=it.getItemMeta();m.setDisplayName(ChatColor.WHITE+msg);it.setItemMeta(m);inv.setItem(slot,it);getConfig().set("editor."+t+".contents",inv.getContents());saveConfig();}
                editingState.remove(id);player.sendMessage(ChatColor.GREEN+"İsim kaydedildi!");if(t!=null)openSubMenu(player,t.replace(ChatColor.DARK_RED+"[EDİT] ",""),true);
            }
        });
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
