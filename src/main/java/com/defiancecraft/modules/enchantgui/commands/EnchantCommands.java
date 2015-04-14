package com.defiancecraft.modules.enchantgui.commands;

import java.util.HashSet;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.defiancecraft.core.DefianceCore;
import com.defiancecraft.core.util.FileUtils;
import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;
import com.defiancecraft.modules.enchantgui.menus.EnchantMenu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

public class EnchantCommands {

	public static boolean help(CommandSender sender, String[] args) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
			"&9&lEnchantGUI Help:\n" +
			"&b/enchantgui\n" +
			"&b/enchantgui table\n" +
			"&b/enchantgui reload\n" +
			"&b/enchant"
		));
		return true;
	}
	
	public static boolean table(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) return false;
		
		Player p = ((Player)sender);
		
		@SuppressWarnings("deprecation")
		Block target = p.getTargetBlock((HashSet<Byte>)null, 20);
		
		if (target == null || !target.getType().equals(Material.ENCHANTMENT_TABLE)) {
			sender.sendMessage(ChatColor.RED + "You have to look at an enchantment table.");
			return true;
		}

		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		EnchantGUIConfig config = EnchantGUIPlugin.getConfiguration();
		config.tableWorld = target.getWorld().getName();
		config.tableX = target.getX();
		config.tableY = target.getY();
		config.tableZ = target.getZ();
		 
		// Save the config
		DefianceCore.getModuleConfig().configs.put("EnchantGUI", gson.fromJson(gson.toJsonTree(config), new TypeToken<Map<String, JsonElement>>(){}.getType()));
		DefianceCore.getModuleConfig().save(FileUtils.getSharedConfig("modules.json"));
		
		sender.sendMessage(ChatColor.GREEN + "Set the enchantment table location.");
		
		return true;
	}
	
	public static boolean reload(EnchantGUIPlugin plugin, CommandSender sender, String[] args) {
		
		plugin.reloadConfiguration();
		sender.sendMessage(ChatColor.GREEN + "Reloaded configuration!");
		
		return true;
		
	}
	
	public static boolean enchant(EnchantGUIPlugin plugin, CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player))
			return false;
		
		Player p = (Player) sender;
		EnchantGUIConfig config = EnchantGUIPlugin.getConfiguration();
		
		// No perm message prioritizes over non-whitelisted world denial
		if (!p.hasPermission(config.enchantCommandPermission)) {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.enchantCommandMsg));
			return true;
		} else if (config.isWhitelistedWorld(p.getWorld().getName())) {
			
			// Show them the menu
			new BukkitRunnable() {
				public void run() {
					new EnchantMenu(plugin).openMenu(p);
				}
			}.runTask(plugin);
			
		}
		
		return true;
		
	}
	
}
