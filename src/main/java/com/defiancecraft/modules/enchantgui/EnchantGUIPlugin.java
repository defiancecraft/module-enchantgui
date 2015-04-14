package com.defiancecraft.modules.enchantgui;

import org.bukkit.plugin.java.JavaPlugin;

import com.defiancecraft.core.DefianceCore;
import com.defiancecraft.core.command.CommandRegistry;
import com.defiancecraft.core.database.collections.Collection;
import com.defiancecraft.core.menu.MenuListener;
import com.defiancecraft.core.modules.Module;
import com.defiancecraft.modules.enchantgui.commands.EnchantCommands;
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
    	
    	// Register commands
    	CommandRegistry.registerUniversalCommand(this, "enchantgui", "defiancecraft.enchantgui", EnchantCommands::help);
    	CommandRegistry.registerPlayerSubCommand("enchantgui", "defiancecraft.enchantgui.table", EnchantCommands::table);
    	CommandRegistry.registerUniversalSubCommand("enchantgui", "defiancecraft.enchantgui.reload", (s, a) -> { return EnchantCommands.reload(this, s, a); });
    	CommandRegistry.registerPlayerCommand(this, "enchant", (s, a) -> { return EnchantCommands.enchant(this, s, a); });	
    	
    }
    
    public void onDisable() {
    	
    	EnchantListener.restoreXpLevels();
    	
    }
    
    public void reloadConfiguration() {
    	DefianceCore.reloadModuleConfig();
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
