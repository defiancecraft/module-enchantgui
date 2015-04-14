package com.defiancecraft.modules.enchantgui;

import org.bukkit.plugin.java.JavaPlugin;

import com.defiancecraft.core.database.collections.Collection;
import com.defiancecraft.core.menu.MenuListener;
import com.defiancecraft.core.modules.Module;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;
import com.defiancecraft.modules.enchantgui.listeners.EnchantListener;

public class EnchantGUIPlugin extends JavaPlugin implements Module {

	private static EnchantGUIConfig config;
	
    public void onEnable() {

    	// Initialize config
    	EnchantGUIPlugin.config = getConfig(EnchantGUIConfig.class);

    	// Register events
    	MenuListener.register(this);
    	getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
    	
    	// TODO more pages for menu, could embed in class? Idk 
    	
    }
    
    public static EnchantGUIConfig getConfiguration() {
    	return config;
    }

    @Override
    public String getCanonicalName() {
        return "EnchantGUI";
    }

    @Override
    public Collection[] getCollections() {
        return new Collection[] {};
    }

}
