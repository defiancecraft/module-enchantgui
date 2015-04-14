package com.defiancecraft.modules.enchantgui.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.menus.EnchantMenu;

public class EnchantListener implements Listener {

	private Plugin plugin;
	
	public EnchantListener(Plugin p) {
		this.plugin = p;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e) {

		// Return if cancelled, not enchant table, not right click, or not whitelisted world
		if (e.isCancelled()
				|| !e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)
				|| !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| !EnchantGUIPlugin.getConfiguration().isWhitelistedWorld(e.getPlayer().getWorld().getName()))
			return;

		final Player player = e.getPlayer();
		
		// Show the menu
		new BukkitRunnable() {
			public void run() {
				new EnchantMenu(plugin).openMenu(player);
			}
		}.runTask(plugin);
		
		e.setCancelled(true);
		
	}
	
}
