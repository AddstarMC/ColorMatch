package com.comze_instancelabs.colormatch;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

import com.google.common.collect.Maps;

public class Utilities {
	public static DyeColor getBlockColour(Block block) {
		Material mat = block.getType();
		if (mat == Material.WOOL || mat == Material.STAINED_CLAY || mat == Material.STAINED_GLASS || mat == Material.STAINED_GLASS_PANE || mat == Material.CONCRETE) {
			Wool wool = new Wool(mat, block.getData());
			return wool.getColor();
		}
		
		return null;
	}
	
	public static ItemStack makeItem(Material material, DyeColor colour) {
		Wool wool = new Wool(colour);
		return new ItemStack(material, 1, wool.getData());
	}
	
	private static Map<DyeColor, ChatColor> dyeChatMap;
	static {
		dyeChatMap = Maps.newHashMap();
		dyeChatMap.put(DyeColor.BLACK, ChatColor.DARK_GRAY);
		dyeChatMap.put(DyeColor.BLUE, ChatColor.DARK_BLUE);
		dyeChatMap.put(DyeColor.BROWN, ChatColor.GOLD);
		dyeChatMap.put(DyeColor.CYAN, ChatColor.AQUA);
		dyeChatMap.put(DyeColor.GRAY, ChatColor.GRAY);
		dyeChatMap.put(DyeColor.GREEN, ChatColor.DARK_GREEN);
		dyeChatMap.put(DyeColor.LIGHT_BLUE, ChatColor.BLUE);
		dyeChatMap.put(DyeColor.LIME, ChatColor.GREEN);
		dyeChatMap.put(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE);
		dyeChatMap.put(DyeColor.ORANGE, ChatColor.GOLD);
		dyeChatMap.put(DyeColor.PINK, ChatColor.LIGHT_PURPLE);
		dyeChatMap.put(DyeColor.PURPLE, ChatColor.DARK_PURPLE);
		dyeChatMap.put(DyeColor.RED, ChatColor.DARK_RED);
		dyeChatMap.put(DyeColor.SILVER, ChatColor.GRAY);
		dyeChatMap.put(DyeColor.WHITE, ChatColor.WHITE);
		dyeChatMap.put(DyeColor.YELLOW, ChatColor.YELLOW);
	}
	
	public static ChatColor dyeToChat(DyeColor dclr) {
		if (dyeChatMap.containsKey(dclr))
			return dyeChatMap.get(dclr);
		return ChatColor.MAGIC;
	}
	
	public static String translate(String raw) {
		return ChatColor.translateAlternateColorCodes('&', raw);
	}
}
