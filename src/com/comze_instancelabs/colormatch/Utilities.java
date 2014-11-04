package com.comze_instancelabs.colormatch;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.Wool;

public class Utilities {
	@SuppressWarnings("deprecation")
	public static DyeColor getBlockColour(Block block) {
		Material mat = block.getType();
		if (mat == Material.WOOL || mat == Material.STAINED_CLAY || mat == Material.STAINED_GLASS || mat == Material.STAINED_GLASS_PANE) {
			Wool wool = new Wool(mat, block.getData());
			return wool.getColor();
		}
		
		return null;
	}
}
