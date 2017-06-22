package com.comze_instancelabs.colormatch.patterns;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

/**
 * A pattern consisting of squares each 4x4 blocks
 */
public class SquaresPattern extends PatternBase {
	private int squareSize;
	
	private int width;
	private int height;
	
	private PatternPixel[] pixels;
	
	public SquaresPattern(int width, int height, int squareSize) {
		this.width = width;
		this.height = height;
		this.squareSize = squareSize;
		
		generatePattern();
	}
	
	private void generatePattern() {
		pixels = new PatternPixel[getWidth() * getHeight()];
		
		// The material doesnt matter as long as the squares are separated
		MaterialData even = new MaterialData(Material.STONE);
		MaterialData odd = new MaterialData(Material.DIRT);
		
		// Each square
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				MaterialData mat;
				if ((x + y) % 2 == 0)
					mat = even;
				else
					mat = odd;
				
				// Each pixel in square
				for (int px = 0; px < squareSize; ++px) {
					for (int py = 0; py < squareSize; ++py) {
						PatternPixel pixel = new PatternPixel(x * squareSize + px, y * squareSize + py, mat);
						pixels[pixel.offsetX + pixel.offsetY * getWidth()] = pixel;
					}
				}
			}
		}
	}
	
	@Override
	public int getWidth() {
		return width * squareSize;
	}

	@Override
	public int getHeight() {
		return height * squareSize;
	}

	@Override
	public PatternPixel getPixel(int x, int y) {
		if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
			return null;
		
		return pixels[x + y * getWidth()];
	}

}
