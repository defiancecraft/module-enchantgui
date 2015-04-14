package com.defiancecraft.modules.enchantgui.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum ItemClass {

	TOOL('T'),
	SWORD('S'),
	ARMOR('A'),
	NONE;
	
	private static Map<Character, ItemClass> charMap;
	private Character symbol;
	
	ItemClass() {}
	ItemClass(char symbol) {
		this.symbol = symbol;
	}
	
	Character getSymbol() {
		return this.symbol;
	}
	
	// Initialize charMap in static section; can't do it in constructor
	// because enum values are initialized first.
	static {
		charMap = new HashMap<Character, ItemClass>();
		for (ItemClass ic : ItemClass.values())
			if (ic.getSymbol() != null)
				charMap.put(ic.getSymbol(), ic);
	}
	
	public static ItemClass getItemClass(Character symbol) {
		return charMap.containsKey(symbol) ? charMap.get(symbol) : null;
	}
	
	public static ItemClass getItemClass(ItemStack item) {
		return getItemClass(item.getType());
	}
	
	public static ItemClass getItemClass(Material mat) {
		switch (mat) {
		case LEATHER_HELMET:
		case LEATHER_CHESTPLATE:
		case LEATHER_LEGGINGS:
		case LEATHER_BOOTS:
		case IRON_HELMET:
		case IRON_CHESTPLATE:
		case IRON_LEGGINGS:
		case IRON_BOOTS:
		case GOLD_HELMET:
		case GOLD_CHESTPLATE:
		case GOLD_LEGGINGS:
		case GOLD_BOOTS:
		case DIAMOND_HELMET:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_LEGGINGS:
		case DIAMOND_BOOTS:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_LEGGINGS:
		case CHAINMAIL_BOOTS:
			return ItemClass.ARMOR;
		case WOOD_AXE:
		case WOOD_HOE:
		case WOOD_SPADE:
		case WOOD_PICKAXE:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_SPADE:
		case STONE_PICKAXE:
		case IRON_AXE:
		case IRON_HOE:
		case IRON_SPADE:
		case IRON_PICKAXE:
		case GOLD_AXE:
		case GOLD_HOE:
		case GOLD_SPADE:
		case GOLD_PICKAXE:
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_SPADE:
		case DIAMOND_PICKAXE:
			return ItemClass.TOOL;
		case WOOD_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case GOLD_SWORD:
		case DIAMOND_SWORD:
			return ItemClass.SWORD;
		default:
			return ItemClass.NONE;
		}
	}
	
}
