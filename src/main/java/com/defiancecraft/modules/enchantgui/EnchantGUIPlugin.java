package com.defiancecraft.modules.enchantgui;

import com.defiancecraft.core.command.CommandRegistry;
import com.defiancecraft.core.database.collections.Collection;
import com.defiancecraft.core.menu.MenuListener;
import com.defiancecraft.core.modules.impl.JavaModule;
import com.defiancecraft.modules.enchantgui.commands.EnchantCommands;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;
import com.defiancecraft.modules.enchantgui.listeners.EnchantListener;

public class EnchantGUIPlugin extends JavaModule {

	private static EnchantGUIConfig config;
	
    public void onEnable() {

    	// Initialize config
    	EnchantGUIPlugin.config = getConfig(EnchantGUIConfig.class);

    	// Register events
    	MenuListener.register(this);
    	getServer().getPluginManager().registerEvents(new EnchantListener(this), this);
    	
    	// Register commands
    	CommandRegistry.registerUniversalCommand(this, "enchantgui", "defiancecraft.enchantgui", EnchantCommands::help);
    	CommandRegistry.registerPlayerSubCommand("enchantgui", "table", "defiancecraft.enchantgui.table", (s, a) -> { return EnchantCommands.table(this, s, a); });
    	CommandRegistry.registerUniversalSubCommand("enchantgui", "reload", "defiancecraft.enchantgui.reload", (s, a) -> { return EnchantCommands.reload(this, s, a); });
    	CommandRegistry.registerPlayerCommand(this, "enchant", (s, a) -> { return EnchantCommands.enchant(this, s, a); });	
    	
    }
    
    public void onDisable() {
    	
    	EnchantListener.restoreXpLevels();
    	
    }
    
    public void reloadConfiguration() {
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
    
    public boolean saveConfig(EnchantGUIConfig instance) {
    	config = instance;
    	return super.saveConfig(instance);
    }

}
