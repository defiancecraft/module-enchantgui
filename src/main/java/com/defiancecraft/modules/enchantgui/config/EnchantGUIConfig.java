package com.defiancecraft.modules.enchantgui.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.defiancecraft.modules.enchantgui.util.ItemClass;
import com.defiancecraft.modules.enchantgui.util.MaterialParser;

public class EnchantGUIConfig {

	/*
	 * Generate an example configuration
	 */
	private static final Map<String, EnchantmentTypeConfig> DEFAULT_ENCHANTMENT_TYPES = new HashMap<String, EnchantmentTypeConfig>();
	static {
		
		Map<Integer, EnchantmentLevelConfig> sharpnessLevels = new HashMap<Integer, EnchantmentLevelConfig>();
		for (int i = 2; i <= 6; i += 2)
			sharpnessLevels.put(i, new EnchantmentLevelConfig("sharpness.level." + i, i * 2, Arrays.asList("Buy hero for", "sharp " + i)));
		
		EnchantmentTypeConfig sharpnessConfig = new EnchantmentTypeConfig();
		sharpnessConfig.friendlyName = "&9Sharpness {level} ({cost})";
		sharpnessConfig.lore = Arrays.asList("Enchant with sharpness!");
		sharpnessConfig.levels = sharpnessLevels;
		sharpnessConfig.row = 0;
		sharpnessConfig.item = "DIAMOND_SWORD";
		
		DEFAULT_ENCHANTMENT_TYPES.put("DAMAGE_ALL", sharpnessConfig);
		
	}
	
	// Whitelisted worlds for enchanting GUI
	public List<String> whitelistWorlds = Arrays.asList("world");
	
	// Menu Stuff
	public String menuTitle = "Enchant";
	public String menuSelectTitle = "Select Enchantment";
	public String menuApplyTitle = "Enchant Item!";
	
	public String menuApplyItem = "IRON_FENCE";
	public String menuApplyText = "Put item in the middle!";
	public List<String> menuApplyLore = Arrays.asList("&aOoO SpoOky!");
	
	public String menuTableText = "&9Cheap Enchantments";
	public String menuTableItem = "ENCHANTING_TABLE";
	public List<String> menuTableLore = Arrays.asList("&bSome text", "&bgoes here.");
	
	public String menuTokenText = "&6Buy Enchantments";
	public String menuTokenItem = "DOUBLE_PLANT:0";
	public List<String> menuTokenLore = Arrays.asList("&eYellow is", "&egreat.");
	
	// Enchanting table location
	public String tableWorld = "world";
	public double tableX = 0;
	public double tableY = 0;
	public double tableZ = 0;
	
	// Enchantment Types
	public String enchantmentPoorLore = "&4You do not have enough tokens.";
	public Map<String, EnchantmentTypeConfig> enchantmentTypes = DEFAULT_ENCHANTMENT_TYPES;
	
	// Various messages
	public String onlyOneItemMsg = "&4You may only enchant one item at a time!";
	public String sameLevelMsg = "&4You already have this level of enchantment.";
	
	// Random Enchantments
	public int randomCost1 = 5;
	public int randomCost2 = 10;
	public int randomCost3 = 15;
	public List<String> randomEnchantments = Arrays.asList("DAMAGE_ALL:5:100:TS", "PROTECTION_ENVIRONMENTAL:1:20:A");
	
	// Enchant command
	public String enchantCommandPermission = "enchantgui.enchant";
	public String enchantCommandMsg = "&aBuy HERO to get this command!";
	
	public boolean isWhitelistedWorld(String worldName) {
		for (String world : whitelistWorlds)
			if (world.equalsIgnoreCase(worldName)) return true;
		return false;
	}
	
	public Location getTableLocation() {
		return new Location(Bukkit.getWorld(tableWorld), tableX, tableY, tableZ);
	}
	
	public ItemStack getTableItem() {
		return MaterialParser.parseMaterialString(menuTableItem, Material.ENCHANTMENT_TABLE);
	}
	
	public ItemStack getTokenItem() {
		return MaterialParser.parseMaterialString(menuTableItem, Material.DOUBLE_PLANT);
	}
	
	public int getSelectMenuRows() {
		return enchantmentTypes.size();
	}
	
	public List<RandomEnchantment> getRandomEnchantments() {
		
		List<RandomEnchantment> enchantments = new ArrayList<RandomEnchantment>();
		
		for (String serialized : this.randomEnchantments) {
			
			if (serialized == null || serialized.isEmpty() || serialized.split(":").length < 3)
				continue;
			
			// Get all of the parts of the serialized string
			String typeStr    = serialized.split(":")[0];
			String levelStr   = serialized.split(":")[1];
			String chanceStr  = serialized.split(":")[2];
			String classesStr = serialized.split(":").length > 3 ? serialized.split(":")[3] : "TSA"; // Allow for any type if classes are omitted
			
			int level, chance;
			Enchantment type;
			
			try {
				
				level = Integer.parseInt(levelStr);
				chance = Integer.parseInt(chanceStr);
				type = Enchantment.getByName(typeStr);
				
				if (type == null) throw new IllegalArgumentException();
				
			} catch (IllegalArgumentException e) {
			
				// Continue if NumberFormatException or Enchantment is invalid
				continue;
				
			}
			
			List<ItemClass> classList = new ArrayList<ItemClass>();
			
			// Parse the characters in the classes string to their respective ItemClasses
			for (char c : classesStr.toUpperCase().toCharArray())
				if (ItemClass.getItemClass(c) != null)
					classList.add(ItemClass.getItemClass(c));
			
			// Add all if none were valid
			if (classList.size() == 0)
				classList.addAll(Arrays.asList(new ItemClass[]{ ItemClass.ARMOR, ItemClass.TOOL, ItemClass.SWORD }));
			
			enchantments.add(new RandomEnchantment(type, level, classList.toArray(new ItemClass[]{}), chance));
			
		}
		
		return enchantments;
		
	}
	
	public static class RandomEnchantment {
		
		public Enchantment type;
		public int level;
		public ItemClass[] classes;
		public int chance;
		
		RandomEnchantment(Enchantment type, int level, ItemClass[] classes, int chance) {
			this.type = type;
			this.level = level;
			this.classes = classes;
			this.chance = chance;
		}
		
	}
	
	public static class EnchantmentTypeConfig {
		
		public String friendlyName;
		public String item;
		public int row = 0;
		public List<String> lore = new ArrayList<String>();
		public Map<Integer, EnchantmentLevelConfig> levels = new HashMap<Integer, EnchantmentLevelConfig>();
		
	}
	
	public static class EnchantmentLevelConfig {
		
		public String permission = "";
		public double cost = 1;
		public List<String> lore = Arrays.asList("You don't have", "permission!");

		// Internal constructor for convenience
		private EnchantmentLevelConfig(String permission, int cost, List<String> lore) {
			this.permission = permission;
			this.cost = cost;
			this.lore = lore;
		}
		
		public EnchantmentLevelConfig() {}
		
	}
	
}
