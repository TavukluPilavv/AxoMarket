package com.craftaxo.axomarket;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Economy econ = null;

    @Override
    public void onEnable() {
        if (!setupEconomy()) {
            getLogger().severe("Vault veya bir Ekonomi eklentisi (Vault uyumlu) bulunamadı! Eklenti kapatılıyor.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Komut tanımı
        if (getCommand("market") != null) {
            getCommand("market").setExecutor(new MarketCommand());
        }

        // Event dinleyicisi
        getServer().getPluginManager().registerEvents(new MarketListener(this), this);

        getLogger().info("AxoMarket başarıyla aktif edildi!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AxoMarket devredışı bırakıldı.");
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
