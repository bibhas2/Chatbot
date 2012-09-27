package com.mobiarch.chatbot;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {

    private Events listener;

    @Override
    public void onEnable() {
        this.listener = new Events(this);
        this.getServer().getPluginManager().registerEvents(this.listener, this);
		log("Using bot: " + getConfig().getString("botid"));
    }
    
    public void log(String data) {
        Logger.getLogger("Minecraft").info("[Chatbot] " + data);
    }
    
    public String formatMessage(String msg) {
        return "<" + ChatColor.RED + "Chatbot" + ChatColor.WHITE + "> " + msg;
    }
}
