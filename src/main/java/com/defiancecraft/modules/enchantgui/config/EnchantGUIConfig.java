package com.defiancecraft.modules.enchantgui.config;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class EnchantGUIConfig {

	public List<String> whitelistWorlds = Arrays.asList("world");
	
	// Enchanting table location
	public String tableWorld = "world";
	public double tableX = 0;
	public double tableY = 0;
	public double tableZ = 0;
	
	public String menuTitle = "Enchant";
	
	public boolean isWhitelistedWorld(String worldName) {
		for (String world : whitelistWorlds)
			if (world.equalsIgnoreCase(worldName)) return true;
			
		return false;
	}
	
	public Location getTableLocation() {
		return new Location(Bukkit.getWorld(tableWorld), tableX, tableY, tableZ);
	}
	
}
