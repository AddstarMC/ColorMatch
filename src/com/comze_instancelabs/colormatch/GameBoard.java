package com.comze_instancelabs.colormatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.comze_instancelabs.colormatch.patterns.PatternBase;

public class GameBoard {
	private Random random;
	
	private Location spawn;
	
	private Material material;
	private PatternBase currentPattern;
	private DyeColor currentColour;
	
	private List<Block> activeBlocks;
	
	public GameBoard() {
		random = new Random();
		activeBlocks = new ArrayList<Block>();
	}
	
	public void clear() {
		for (Block block : activeBlocks) {
			block.setType(Material.AIR);
		}
		
		activeBlocks.clear();
	}
	
	public void clearExcept(DyeColor colour) {
		for (Block block : activeBlocks) {
			if (Utilities.getBlockColour(block) != colour)
				block.setType(Material.AIR);
		}
	}
	
	public Location getBoardOrigin() {
		return new Location(spawn.getWorld(), spawn.getBlockX() - currentPattern.getWidth() / 2, spawn.getBlockY(), spawn.getBlockZ() - currentPattern.getHeight() / 2);
	}
	
	public void generate() {
		clear();
		currentPattern.placeAt(getBoardOrigin(), material, activeBlocks, random);
	}
	
	public void setColour(DyeColor colour) {
		currentColour = colour;
	}
	
	public DyeColor getColour() {
		return currentColour;
	}
	
	public void setPattern(PatternBase pattern) {
		currentPattern = pattern;
	}
	
	public PatternBase getPattern() {
		return currentPattern;
	}
	
	public void setSpawn(Location location) {
		spawn = location;
	}
	
	public Location getSpawn() {
		return spawn;
	}
	
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public Material getMaterial() {
		return material;
	}
}
