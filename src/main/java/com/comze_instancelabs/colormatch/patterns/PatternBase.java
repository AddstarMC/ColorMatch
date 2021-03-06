package com.comze_instancelabs.colormatch.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.comze_instancelabs.colormatch.Colors;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import com.comze_instancelabs.colormatch.Main;

public abstract class PatternBase {
	private HashSet<PatternGroup> groups;
	
	public abstract int getWidth();
	public abstract int getHeight();
	
	/**
	 * Gets the pixel at the coords. Must return null if the coords are out of range
	 */
	public abstract PatternPixel getPixel(int x, int y);
	
	public final void processPattern() {
		HashSet<PatternPixel> visited = new HashSet<PatternPixel>();
		
		groups = new HashSet<PatternGroup>();
		
		for (int x = 0; x < getWidth(); ++x) {
			for (int y = 0; y < getHeight(); ++y) {
				PatternPixel pixel = getPixel(x, y);
				
				if (pixel.material == Material.AIR || visited.contains(pixel))
					continue;
				
				// Do a flood fill to find all matching blocks
				PatternGroup group = new PatternGroup();
				LinkedList<PatternPixel> floodQueue = new LinkedList<PatternPixel>();
				floodQueue.add(pixel);
				
				while(!floodQueue.isEmpty()) {
					PatternPixel floodPixel = floodQueue.pop();
					
					if (!visited.add(floodPixel))
						continue;
					
					group.getPixels().add(floodPixel);
					
					// Pixel to left
					PatternPixel nextPixel = getPixel(floodPixel.offsetX-1, floodPixel.offsetY);
					if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
						floodQueue.add(nextPixel);
					
					// Pixel to right
					nextPixel = getPixel(floodPixel.offsetX+1, floodPixel.offsetY);
					if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
						floodQueue.add(nextPixel);
					
					// Pixel to top
					nextPixel = getPixel(floodPixel.offsetX, floodPixel.offsetY-1);
					if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
						floodQueue.add(nextPixel);
					
					// Pixel to bottom
					nextPixel = getPixel(floodPixel.offsetX, floodPixel.offsetY+1);
					if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
						floodQueue.add(nextPixel);
				}
			
				groups.add(group);
			}
		}
	}
	
	private boolean isMatch(PatternPixel pixel, PatternPixel current) {
		if (pixel == null)
			return false;
		
		return current.material.equals(pixel.material);
	}
	
	public final void placeAt(Location location, Material material, List<Block> modified, Random random,DyeColor cuurentColour, boolean extendedColor) {
		if (groups == null)
			processPattern();
		
//      // Debug placing that shows the patterns actual materials with no randomization 		
//		for (int x = 0; x < getWidth(); ++x) {
//			for (int y = 0; y < getHeight(); ++y) {
//				PatternPixel pixel = getPixel(x, y);
//				Block block = location.getWorld().getBlockAt(pixel.getLocation(location));
//				block.setType(pixel.material.getItemType());
//				block.setData(pixel.material.getData());
//			}
//		}
		DyeColor[] colours;
		if(extendedColor){
			colours = Main.extendedColor;
		}else{
			colours = Main.colors;

		}
		int groupnum = groups.size();
		boolean safe = false;
		int i = 1;
		for (PatternGroup group : groups) {
			DyeColor colour = colours[random.nextInt(colours.length)];
			if (colour == cuurentColour) safe = true;
			if((i >= groupnum) && !safe){
				colour = cuurentColour;
			}
			Material mat = Colors.modifyColour(material,colour);
			BlockData data;
			if(mat==null){
				data = material.createBlockData();
			}else{
				data = mat.createBlockData();
			}
			group.placeAt(location, data, modified);
			i++;
		}
	}
	
	/**
	 * Represents a block in the pattern
	 */
	public static class PatternPixel {
		public final int offsetX;
		public final int offsetY;
		public final Material material;
		
		public PatternPixel(int offsetX, int offsetY, Material material) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.material = material;
		}
		
		public Location getLocation(Location origin) {
			return new Location(origin.getWorld(), origin.getBlockX() + offsetX, origin.getBlockY(), origin.getBlockZ() + offsetY);
		}
		
		@Override
		public int hashCode() {
			return 37 ^ offsetX | 17 ^ offsetY << 16;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PatternPixel))
				return false;
			
			PatternPixel other = (PatternPixel)obj;
			
			return other.offsetX == offsetX && other.offsetY == offsetY && other.material.equals(material);
		}
		
		@Override
		public String toString() {
			return String.format("%d,%d", offsetX, offsetY);
		}
	}
	
	/**
	 * Represents a group of pixels
	 */
	public static class PatternGroup {
		private final List<PatternPixel> pixels;
		
		public PatternGroup() {
			pixels = new ArrayList<PatternPixel>();
		}
		
		public List<PatternPixel> getPixels() {
			return pixels;
		}
		
		public void placeAt(Location origin, BlockData material, List<Block> modified) {
			for (PatternPixel pixel : pixels) {
				Location pixelLocation = pixel.getLocation(origin);
				Block block = pixelLocation.getBlock();
				block.setBlockData(material);
				modified.add(block);
			}
		}
	}
}
