package com.defiancecraft.modules.enchantgui.menus;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.defiancecraft.core.menu.Menu;
import com.defiancecraft.core.menu.impl.AbsoluteMenuLayout;
import com.defiancecraft.core.menu.impl.SimpleMenuOption.SimpleMenuOptionBuilder;
import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;

public class EnchantMenu extends Menu {

	private static final int SLOT_TABLE = 11;
	private static final int SLOT_TOKEN = 15;
	
	private Plugin plugin;
	
	public EnchantMenu(Plugin p) {
		super(
			ChatColor.translateAlternateColorCodes('&', EnchantGUIPlugin.getConfiguration().menuTitle),
			3,
			new AbsoluteMenuLayout(3)
		);
		this.plugin = p;
		this.setCloseOnClickOutside(true);
	}

	@Override
	protected void addMenuOptions() {
		
		EnchantGUIConfig config = EnchantGUIPlugin.getConfiguration();
		
		addMenuOption(
				new SimpleMenuOptionBuilder(config.getTableItem().getType(), this::onTableClick)
					.durability(config.getTableItem().getDurability())
					.text(config.menuTableText)
					.lore(config.menuTableLore.toArray(new String[]{}))
					.build(), SLOT_TABLE);
		addMenuOption(
				new SimpleMenuOptionBuilder(config.getTokenItem().getType(), this::onTokenClick)
					.durability(config.getTokenItem().getDurability())
					.text(config.menuTokenText)
					.lore(config.menuTokenLore.toArray(new String[]{}))
					.build(), SLOT_TOKEN);
		
	}
	
	private void onTableClick(Player player) {
		
		this.closeMenu(player);
		Location loc = EnchantGUIPlugin.getConfiguration().getTableLocation();
		
		new BukkitRunnable() {
			public void run() {
				player.openEnchanting(loc, true);
			}
		}.runTask(plugin);
		
	}
	
	private void onTokenClick(Player player) {
		Menu.switchMenu(player, this, new PageSelectEnchantment(plugin, player.getUniqueId()), plugin);
	}

}
