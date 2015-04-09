package com.defiancecraft.modules.enchantgui.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;

public class EnchantListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent e) {

		// Return if cancelled, not enchant table, not right click, or not whitelisted world
		if (!e.isCancelled()
				|| !e.getClickedBlock().getType().equals(Material.ENCHANTMENT_TABLE)
				|| !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| !EnchantGUIPlugin.getConfiguration().isWhitelistedWorld(e.getPlayer().getWorld().getName()))
			return;

		// TODO: open menu
		
	}
	
}
