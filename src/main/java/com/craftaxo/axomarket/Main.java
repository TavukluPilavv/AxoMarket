package com.craftaxo.axomarket;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault veya bir Ekonomi eklentisi bulunamadı! Eklenti kapatılıyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Event dinleyicisini kaydet
        getServer().getPluginManager().registerEvents(new MarketListener(this), this);

        getLogger().info("AxoMarket başarıyla aktif edildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AxoMarket devredışı bırakıldı.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("market")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                MarketGUI.openMainMenu(player);
            } else {
                sender.sendMessage(ChatColor.RED + "Bu komutu sadece oyuncular kullanabilir!");
            }
            return true;
        }
        return false;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
