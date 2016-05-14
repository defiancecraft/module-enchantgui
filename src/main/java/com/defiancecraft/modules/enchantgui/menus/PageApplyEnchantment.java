package com.defiancecraft.modules.enchantgui.menus;

import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.defiancecraft.core.menu.Menu;
import com.defiancecraft.core.menu.MenuOption;
import com.defiancecraft.core.menu.impl.AbsoluteMenuLayout;
import com.defiancecraft.core.menu.impl.SimpleMenuOption;
import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.util.MaterialParser;

public class PageApplyEnchantment extends Menu {

	private static final int MENU_ROWS = 3;
	private static final int ENCHANT_SLOT = 13;
	
	private Enchantment enchantment;
	private int level;
	private double cost;
	
	protected PageApplyEnchantment(Enchantment ench, int level, double cost) {
		super(EnchantGUIPlugin.getConfiguration().menuApplyTitle, MENU_ROWS, new AbsoluteMenuLayout(MENU_ROWS));
		this.enchantment = ench;
		this.level = level;
		this.cost = cost;
		super.init();
	}

	@Override
	protected void addMenuOptions() {
		
		ItemStack filler = MaterialParser.parseMaterialString(EnchantGUIPlugin.getConfiguration().menuApplyItem);
		ItemMeta meta = filler.getItemMeta();
		meta.setDisplayName(EnchantGUIPlugin.getConfiguration().menuApplyText);
		meta.setLore(EnchantGUIPlugin.getConfiguration().menuApplyLore
				.stream()
				.map((s) -> { return ChatColor.translateAlternateColorCodes('&', s); })
				.collect(Collectors.toList()));
		filler.setItemMeta(meta);
		
		for (int i = 0; i < 27; i++)
			if (i != ENCHANT_SLOT)
				addMenuOption(new SimpleMenuOption(filler, (p) -> { return true; }), i);
		
		addMenuOption(new EnchanterMenuOption(this), ENCHANT_SLOT);
		
	}

	static class EnchanterMenuOption implements MenuOption {

		private PageApplyEnchantment menu;
		
		EnchanterMenuOption(PageApplyEnchantment menu) {
			this.menu = menu;
		}
		
		@Override
		public ItemStack getItemStack() {
			return new ItemStack(Material.AIR, 1);
		}

		@Override
		public boolean onClick(Player player, InventoryClickEvent event) {
			
			ItemStack cursor = event.getCursor();
			
			// Disallow stacks of items
			if (cursor.getAmount() > 1) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', EnchantGUIPlugin.getConfiguration().onlyOneItemMsg));
				return true;
			}
			
			// Disallow same level enchants (don't charge!) and notify
			if (cursor.getEnchantmentLevel(menu.enchantment) == menu.level) {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', EnchantGUIPlugin.getConfiguration().sameLevelMsg));
				return true;
			}
			
			// Check that they have the balance for the enchantment.
			// If not, close the menu.
			if (player.getLevel() < menu.cost) {
				menu.closeMenu(player);
				return true;
			}

			// Charge the player the cost
			player.setLevel(player.getLevel() - (int)menu.cost);

			// Allow 'downgrades'
			if (cursor.getEnchantmentLevel(menu.enchantment) >=	 menu.level)
				cursor.removeEnchantment(menu.enchantment);

			cursor.addUnsafeEnchantment(menu.enchantment, menu.level);
			player.setItemOnCursor(cursor);
			
			return true;
			
		}
		
		
		
	}
	
}
