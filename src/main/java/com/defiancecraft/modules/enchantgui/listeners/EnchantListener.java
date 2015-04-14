package com.defiancecraft.modules.enchantgui.listeners;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
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

import com.defiancecraft.core.api.Economy;
import com.defiancecraft.modules.enchantgui.EnchantGUIPlugin;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig;
import com.defiancecraft.modules.enchantgui.config.EnchantGUIConfig.RandomEnchantment;
import com.defiancecraft.modules.enchantgui.menus.EnchantMenu;
import com.defiancecraft.modules.enchantgui.util.ItemClass;

public class EnchantListener implements Listener {

	// Temporary user XP level storage
	// Entry is <XP level, XP>
	private static Map<UUID, Entry<Integer, Float>> xpLevels = new HashMap<UUID, Entry<Integer, Float>>();
	
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

		// Store XP level for retrieval later
		if (!xpLevels.containsKey(e.getEnchanter().getUniqueId())) {
			
			xpLevels.put(e.getEnchanter().getUniqueId(), new AbstractMap.SimpleEntry<Integer, Float>(e.getEnchanter().getLevel(), e.getEnchanter().getExp()));
			int balance;
			
			// Get their tokens, and set their XP level to that
			try {
				double ecoBalance = Economy.getBalance(e.getEnchanter().getUniqueId());
				balance = (int) Math.floor(ecoBalance);
			} catch (Exception ex) {
				ex.printStackTrace();
				e.setCancelled(true);
				return;
			}

			// Set level to balance, so that they can only buy enchantments
			// they have the balance for
			e.getEnchanter().setExp(0);
			e.getEnchanter().setLevel(balance);
			
		}
		
		e.getExpLevelCostsOffered()[0] = config.randomCost1;
		e.getExpLevelCostsOffered()[1] = config.randomCost2;
		e.getExpLevelCostsOffered()[2] = config.randomCost3;
		
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEnchantItem(EnchantItemEvent e) {

		EnchantGUIConfig config = EnchantGUIPlugin.getConfiguration();
		
		// Set XP level cost to the correct cost
		e.setExpLevelCost(
			new int[] { config.randomCost1, config.randomCost2, config.randomCost3 }
			[ e.whichButton() ]
		);
		
		try {
			
			// Don't allow enchanting if they don't have the balance for it
			double balance = Economy.getBalance(e.getEnchanter().getUniqueId());
			if (balance < e.getExpLevelCost())
				return;
			
			// Withdraw the cost of the enchant
			Economy.setBalance(e.getEnchanter().getName(), balance - e.getExpLevelCost());
			
			// Take away levels manually
			e.getEnchanter().setLevel(e.getEnchanter().getLevel() - e.getExpLevelCost() + e.whichButton() + 1);
			e.setExpLevelCost(0);
			
		} catch (Exception ex) {
			Bukkit.getLogger().severe("Error while enchanting item!");
			ex.printStackTrace();
			e.setCancelled(true);
			return;
		}
		
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
		for (RandomEnchantment re : randoms)
			e.getEnchantsToAdd().put(re.type, re.level);
		
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		
		if (!(e.getPlayer() instanceof Player))
			return;
		
		// Set their XP back to what it was
		if (xpLevels.containsKey(e.getPlayer().getUniqueId())) {
			Player p = (Player) e.getPlayer();
			p.setLevel(xpLevels.get(p.getUniqueId()).getKey());
			p.setExp(xpLevels.get(p.getUniqueId()).getValue());
			xpLevels.remove(p.getUniqueId());
		}
		
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
	
	/**
	 * Restore XP levels for all online users so they don't just keep their XP
	 */
	public static void restoreXpLevels() {
		
		for (Entry<UUID, Entry<Integer, Float>> entry : xpLevels.entrySet()) {
			Player p = Bukkit.getPlayer(entry.getKey());
			if (p != null) {
				p.setLevel(entry.getValue().getKey());
				p.setExp(entry.getValue().getValue());
			}
		}

		xpLevels.clear();
		
	}
	
}
