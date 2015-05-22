package com.defiancecraft.modules.enchantgui.menus;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.defiancecraft.core.menu.Menu;
import com.defiancecraft.core.menu.impl.AbsoluteMenuLayout;
import com.defiancecraft.core.menu.impl.SimpleMenuOption;
import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig.CategoryConfig;

public class PageSelectCategory extends Menu {

	// EnchantGUIPlugin instance
	private Plugin plugin;

	// UUID of player - needed to check balance in selection menu
	private UUID playerUUID;
	
	public PageSelectCategory(Plugin plugin, UUID uuid) {
		super(
			ChatColor.translateAlternateColorCodes('&', EnchantGUIPlugin.getConfiguration().menuCategoriesTitle),
			EnchantGUIPlugin.getConfiguration().getCategoriesMenuRows(),
			new AbsoluteMenuLayout(EnchantGUIPlugin.getConfiguration().getCategoriesMenuRows())
		);
		
		this.plugin = plugin;
		this.playerUUID = uuid;
		
		super.init();
	}
	
	protected void addMenuOptions() {
		
		List<CategoryConfig> categories = EnchantGUIPlugin.getConfiguration().categories;
		
		int i = 0;
		int rows = EnchantGUIPlugin.getConfiguration().getCategoriesMenuRows();
		int centrePadding = (9 - categories.size() % 9) / 2;
		
		for (CategoryConfig category : categories) {
		
			// If slot is in last row, centre it. Otherwise, shove it in there.
			int slot = i >= (rows - 1) * 9 ? ((rows - 1) * 9) + centrePadding + i % 9 :
										     i;
			
			addMenuOption(new SimpleMenuOption(category.getItem(), (p) -> onSelectCategory(p, category)), slot);
			i++;
		}
		
	}
	
	private boolean onSelectCategory(Player p, CategoryConfig category) {
		Menu.switchMenu(p, this, new PageSelectEnchantment(this.plugin, this.playerUUID, category.name), plugin);
		return true;
	}
	
}
