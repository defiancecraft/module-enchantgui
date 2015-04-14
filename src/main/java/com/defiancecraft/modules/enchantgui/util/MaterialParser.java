package com.defiancecraft.modules.enchantgui.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MaterialParser {

	public static ItemStack parseMaterialString(String mat) {
		return parseMaterialString(mat, (ItemStack)null);
	}
	
	public static ItemStack parseMaterialString(String mat, Material def) {
		return parseMaterialString(mat, new ItemStack(def, 1));
	}
	
	/**
	 * Parses a material string (takes the form 'material:durability', or 'material')
	 * into an ItemStack. If the string is invalid, `def` is returned.
	 * 
	 * @param str String to parse
	 * @param def Default value if string is unparsable
	 * @return Parsed string, or `def` on failure
	 */
	public static ItemStack parseMaterialString(String str, ItemStack def) {
		
		if (str == null || str.isEmpty() || str.split(":").length == 0)
			return def;
		
		String material = str.split(":")[0];
		String damage   = str.split(":").length > 1 ? str.split(":")[1] : "-1";
		
		if (Material.getMaterial(material) == null)
			return def;
		
		int durability = -1;
		try {
			durability = Integer.parseInt(damage);
		} catch (NumberFormatException e) {}
		
		ItemStack stack = new ItemStack(Material.getMaterial(material), 1);
		if (durability > -1)
			stack.setDurability((short)(durability & 0xFFFF)); 
		
		return stack;
		
		
	}
	
}
