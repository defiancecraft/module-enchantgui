package com.defiancecraft.modules.enchantgui.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.defiancecraft.core.api.Economy;
import com.defiancecraft.core.menu.Menu;
import com.defiancecraft.core.menu.impl.AbsoluteMenuLayout;
import com.defiancecraft.core.menu.impl.SimpleMenuOption;
import com.defiancecraft.core.menu.impl.SimpleMenuOption.SimpleMenuOptionBuilder;
import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig.EnchantmentLevelConfig;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig.EnchantmentTypeConfig;
import com.defiancecraft.modules.enchantgui.util.MaterialParser;

public class PageSelectEnchantment extends Menu {

	private Plugin plugin;
	private UUID playerUUID;
	private String category;
	private double playerBalance = 0;
	
	public PageSelectEnchantment(Plugin p, UUID uuid, String category) {
		super(
			ChatColor.translateAlternateColorCodes('&', EnchantGUIPlugin.getConfiguration().menuSelectTitle),
			EnchantGUIPlugin.getConfiguration().getSelectMenuRows(category),
			new AbsoluteMenuLayout(EnchantGUIPlugin.getConfiguration().getSelectMenuRows(category)));
		this.plugin = p;
		this.playerUUID = uuid;
		this.category = category;
		this.setCloseOnClickOutside(true);
		this.calculateTokens();
		this.init();
	}

	private void calculateTokens() {
		
		try {
			playerBalance = Economy.getBalance(playerUUID);
		} catch (Exception e) {}
		
	}
	
	private boolean hasTokens(double tokens) {
		return playerBalance >= tokens;
	}
	
	@Override
	protected void addMenuOptions() {

		// Return if not initialized
		if (playerUUID == null)
			return;
		
		Player player = Bukkit.getPlayer(playerUUID);
		
		if (player == null)
			throw new IllegalStateException("Player is not online");

		int row = 0;
		
		// Fill the menu with enchantment types and levels; each enchantment type goes on its own row.
		for (Entry<String, EnchantmentTypeConfig> entry : EnchantGUIPlugin.getConfiguration().enchantmentTypes.entrySet()) {

			int column = 0;
			Enchantment ench = Enchantment.getByName(entry.getKey());
			EnchantmentTypeConfig config = entry.getValue();
			
			// Do not show the enchantment type if it is not the correct category!
			if (!config.category.equalsIgnoreCase(this.category))
				continue;
			
			for (Entry<Integer, EnchantmentLevelConfig> levelEntry : config.levels.entrySet()) {
				
				EnchantmentLevelConfig levelConfig = levelEntry.getValue();
				Usability usability = Usability.NO_PERMISSION;
				
				// Enchantment types define lore for if a player has
				// permission for it; levels define lore for if a player does
				// not have the permission for it.
				// 
				// If they have permission, and they do not have enough tokens, a line
				// is added, defined in the config.
				String[] lore;
				if (levelConfig.permission.isEmpty() || player.hasPermission(levelConfig.permission)) {
					
					List<String> loreList = new ArrayList<String>(config.lore);
					
					// Player has perm, but no tokens
					if (!hasTokens(levelConfig.cost)) {
						loreList.add(EnchantGUIPlugin.getConfiguration().enchantmentPoorLore);
						usability = Usability.NO_MONEY;
						
					// Player has tokens & perm
					} else {
						usability = Usability.USABLE;
					}
					
					lore = loreList.toArray(new String[]{});
					
				// Player doesn't have perm
				} else {
					
					lore = levelConfig.lore.toArray(new String[]{});
					
				}
				
				// Create the option text; this should contain the level
				// and cost of the enchantment.
				String optionText = config.friendlyName
						.replace("{level}", levelEntry.getKey() + "")
						.replace("{cost}", levelConfig.cost + "");
				
				ItemStack item = MaterialParser.parseMaterialString(config.item, Material.WEB);
				
				// Generate a menu option for the level
				SimpleMenuOption option = new SimpleMenuOptionBuilder(
						item.getType(),
						generateCallback(ench, levelEntry.getKey(), levelConfig.cost, String.join(" ", levelConfig.lore), usability))
					.durability(item.getDurability())
					.text(optionText)
					.lore(lore)
					.build();
				
				this.addMenuOption(option, row * 9 + column);
				column++;
				
			}
			
			// Increment the row
			row++;
			
		}
		
	}
	
	private Predicate<Player> generateCallback(Enchantment enchantment, int level, double cost, String message, Usability usability) {
		
		return (p) -> {
			
			if (Usability.USABLE.equals(usability))
				Menu.switchMenu(p, this, new PageApplyEnchantment(enchantment, level, cost), plugin);
			else if (Usability.NO_PERMISSION.equals(usability))
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
			else
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', EnchantGUIPlugin.getConfiguration().enchantmentPoorLore));
			
			return true;
			
		};
		
	}
	
	enum Usability {
		NO_PERMISSION,
		NO_MONEY,
		USABLE
	}
	
}
