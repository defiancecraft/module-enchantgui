package com.defiancecraft.modules.enchantgui;

import org.bukkit.plugin.java.JavaPlugin;

import com.defiancecraft.core.database.collections.Collection;
import com.defiancecraft.core.modules.Module;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;

public class EnchantGUIPlugin extends JavaPlugin implements Module {

	private static EnchantGUIConfig config;
	
    public void onEnable() {

    	// Initialize config
    	EnchantGUIPlugin.config = getConfig(EnchantGUIConfig.class);

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
