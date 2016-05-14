package com.defiancecraft.modules.enchantgui.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig.RandomEnchantment;
import com.defiancecraft.modules.enchantgui.menus.EnchantMenu;
import com.defiancecraft.modules.enchantgui.util.ItemClass;

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
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {

		if (!(e.getInventory() instanceof EnchantingInventory))
			return;
					
		EnchantGUIConfig config = EnchantGUIPlugin.getConfiguration();
		
		if (e.isCancelled() || !config.isWhitelistedWorld(e.getEnchanter().getWorld().getName()))
			return;
		
		// Ensure that enchantment table is setup
		if (!config.getTableLocation().getBlock().getType().equals(Material.ENCHANTMENT_TABLE)) {
			e.getEnchanter().sendMessage(ChatColor.RED + "Please notify semmeess that he has not set up this correctly.");
			e.setCancelled(true);
			return;
		}

		e.getExpLevelCostsOffered()[0] = config.randomCost1;
		e.getExpLevelCostsOffered()[1] = config.randomCost2;
		e.getExpLevelCostsOffered()[2] = config.randomCost3;
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEnchantItem(EnchantItemEvent e) {

		EnchantGUIConfig config = EnchantGUIPlugin.getConfiguration();
		int cost = new int[] { config.randomCost1, config.randomCost2, config.randomCost3 }
			[ e.whichButton() ]; 
		
		// Set XP level cost to the correct cost
		e.setExpLevelCost(cost);

		// Theoretically, the above line should have set the cost... but it doesn't, so
		// we'll take away the levels manually.
		if (e.getEnchanter().getLevel() < cost) {
			e.setCancelled(true);
			e.getEnchanter().closeInventory();
			return;
		}

		// Note that the expression below is necessary, as vanilla functionality causes (whichButton + 1)
		// levels to be taken away from the enchanter (e.g. first button = -3 levels); we must then
		// compensate for this.
		e.getEnchanter().setLevel(e.getEnchanter().getLevel() - (cost - e.whichButton() - 1));
		e.setExpLevelCost(0);
		
		ItemClass itemClass = ItemClass.getItemClass(e.getItem());
		List<RandomEnchantment> randoms = EnchantGUIPlugin.getConfiguration().getRandomEnchantments();
		Random random = new Random();

		// Get any random enchantments if they are the right item class, and if
		// the random chance occurs.
		randoms = randoms.stream()
				.filter((r) -> Arrays.asList(r.classes).contains(itemClass))
				.filter((r) -> random.nextDouble() * r.chance < 1)
				.collect(Collectors.toList());
		
		// If they were lucky enough to get a random enchantment, add it!
		for (RandomEnchantment re : randoms) {
			e.getEnchantsToAdd().put(re.type, re.level);
			
			// Send message if set
			if (re.message != null && !re.message.isEmpty())
				e.getEnchanter().sendMessage(ChatColor.translateAlternateColorCodes('&', re.message));
			
			// Broadcast if set
			if (re.broadcast != null && !re.broadcast.isEmpty())
				Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', re.broadcast).replace("{player}", e.getEnchanter().getName()));
		}
		
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		
		if (!(e.getPlayer() instanceof Player))
			return;
		
		// Clear lapis
		if (e.getInventory() instanceof EnchantingInventory) {
			((EnchantingInventory)e.getInventory()).setSecondary(null);
			new BukkitRunnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					((Player)e.getPlayer()).updateInventory();
				}
			}.runTask(plugin);
		}
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (!(e.getInventory() instanceof EnchantingInventory) || e.isCancelled())
			return;
		
		// Prevent taking lapis lazuli
		if (e.getRawSlot() == 1)
			e.setCancelled(true);
		
	}
	
}
