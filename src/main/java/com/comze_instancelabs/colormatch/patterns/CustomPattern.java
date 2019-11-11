package com.comze_instancelabs.colormatch.patterns;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;

import au.com.mineauz.minigames.Minigames;
import com.comze_instancelabs.colormatch.Colors;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.material.Wool;

public class CustomPattern extends PatternBase {
	private static final String fileHeader = "CMPAT";
	
	private int width;
	private int height;
	
	private PatternPixel[] pixels;
	
	protected CustomPattern() {}
	
	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public PatternPixel getPixel(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height)
			return null;
		
		return pixels[x + y * width];
	}
	
	private boolean checkType(DataInputStream in) throws IOException {
		for (int i = 0; i < fileHeader.length(); ++i) {
			if (in.readChar() != fileHeader.charAt(i)) {
				return false;
			}
		}
		
		return true;
	}

	public boolean load(File file) {
		DataInputStream in = null;
		try	{
			FileInputStream stream = new FileInputStream(file);
			in = new DataInputStream(stream);
			
			if (!checkType(in)) {
				return false;
			}
			
			int version = in.readUnsignedByte();
			switch (version){
				case 1:
					return loadVersion1(in,file);
				case 2:
					return loadVersion2(in);
					default:
						return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch(IOException ignored) {}
			}
		}
	}
	private boolean loadVersion2(DataInputStream in) throws IOException{
		width = in.readUnsignedShort();
		height = in.readUnsignedShort();

		// Read the materials
		HashMap<Integer, Material> mats = new HashMap<>();
		int matCount = in.readShort();
		for(int i = 0; i < matCount; ++i) {
			int id = in.readUnsignedShort();
			String name = in.readUTF();
			Material mat = Material.matchMaterial(name);
			if(mat == null){
				throw new IOException("Unknown material: " + name);
			}
			mats.put(id, mat);
		}
		return readPixels(mats,in);
	}
	private boolean readPixels(HashMap<Integer, Material> mats, DataInputStream in) throws IOException{
		pixels = new PatternPixel[width * height];
		for (int i = 0; i < pixels.length; ++i) {
			Material material = mats.get(in.readUnsignedShort());
			if (material == null)
				throw new IOException("Read an unknown material id");

			int x = i % width;
			int y = i / width;
			pixels[i] = new PatternPixel(x, y, material);
		}
		return true;
	}
	private boolean loadVersion1(DataInputStream in, File file) throws IOException{
		width = in.readUnsignedShort();
		height = in.readUnsignedShort();

		// Read the materials
		HashMap<Integer, Material> mats = new HashMap<>();
		int matCount = in.readShort();
		for(int i = 0; i < matCount; ++i) {
			int id = in.readUnsignedShort();
			String name = in.readUTF();
			byte data = in.readByte();
			Material mat = null;
			try {
				mat = Material.valueOf(name);
			}catch (IllegalArgumentException e){
				Minigames.log(Level.WARNING,"Pattern has Legacy Materials - it may require remaking : " +file.getCanonicalPath());
        Minigames.log(Level.WARNING,"String:"+name+" data:"+data);
        Minigames.log(Level.WARNING,e.getMessage());
        ColorClay clay = ColorClay.getByID(data);
        if(clay != null){
          mat = clay.getMaterial();
        }
				if(mat == null){
          Wool wool;
				  try {
            wool = new Wool(DyeColor.getByWoolData(data));
          }catch (NullPointerException ignored) {
				    byte d = (byte) (new Random().nextInt(15)-1);
				    wool = new Wool(DyeColor.getByWoolData(d));
          }
          wool.getColor();
          mat = Colors.modifyColour(Material.WHITE_TERRACOTTA, wool.getColor());
				}
			}
			mats.put(id, mat);
		}
		return readPixels(mats,in);
	}
	
	public boolean save(File file) {
		HashMap<Material, Integer> ids = new HashMap<>();
		int nextId = 0;
		int[] pixelIds = new int[pixels.length];
		
		for (int i = 0; i < pixels.length; ++i) {
			PatternPixel pixel = pixels[i];
			int id;
			if (!ids.containsKey(pixel.material)) {
				id = nextId++;
				ids.put(pixel.material, id);
			} else {
				id = ids.get(pixel.material);
			}
			
			pixelIds[i] = id;
		}
		
		DataOutputStream out = null;
		try	{
			FileOutputStream stream = new FileOutputStream(file);
			out = new DataOutputStream(stream);
			
			out.writeChars(fileHeader);
			out.writeByte(2); // Version
			
			out.writeShort(width);
			out.writeShort(height);
			
			// Write material map
			out.writeShort(ids.size());
			for (Entry<Material, Integer> entry : ids.entrySet()) {
				out.writeShort(entry.getValue());
				out.writeUTF(entry.getKey().name());
			}
			
			// Write pixels
			for (int id : pixelIds) {
				out.writeShort(id);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch(IOException ignored) {}
			}
		}
	}
	
	public static CustomPattern createFrom(File file) {
		CustomPattern pattern = new CustomPattern();
		if (pattern.load(file))
			return pattern;
		return null;
	}
	
	public static CustomPattern createFrom(Location corner1, Location corner2) {
		Validate.isTrue(corner1.getBlockY() == corner2.getBlockY());
		
		int width = Math.abs(corner1.getBlockX() - corner2.getBlockX()) + 1;
		int height = Math.abs(corner1.getBlockZ() - corner2.getBlockZ()) + 1;
		int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
		int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
		
		CustomPattern pattern = new CustomPattern();
		pattern.width = width;
		pattern.height = height;
		
		pattern.pixels = new PatternPixel[width * height];
		for(int x = 0; x < width; ++x) {
			for(int z = 0; z < height; ++z) {
				Block block = corner1.getWorld().getBlockAt(minX+x, corner1.getBlockY(), minZ+z);
				pattern.pixels[x + z * width] = new PatternPixel(x, z, block.getType());
			}
		}
		
		return pattern;
	}
	enum ColorClay{

	  WHITE(Material.WHITE_TERRACOTTA,0),
    ORANGE(Material.ORANGE_TERRACOTTA,1),
	  BLACK(Material.BLACK_TERRACOTTA,15),
    RED(Material.RED_TERRACOTTA,14),
    BLUE(Material.BLUE_TERRACOTTA,11),
    LIGHT_BLUE(Material.LIGHT_BLUE_TERRACOTTA,3),
    GREEN(Material.GREEN_TERRACOTTA,13),
    LIME(Material.LIME_TERRACOTTA,5),
    PINK(Material.PINK_TERRACOTTA,6),
    MAGNETA(Material.MAGENTA_TERRACOTTA,2),
    LIGHT_GREY(Material.LIGHT_GRAY_TERRACOTTA,8),
    YELLOW(Material.YELLOW_TERRACOTTA,4),
    GRAY(Material.GRAY_TERRACOTTA,7),
    CYAN(Material.CYAN_TERRACOTTA,9),
    PURPLE(Material.PURPLE_TERRACOTTA,10);

    private Material material;
    private int id;
    private static HashMap<Integer, ColorClay> byID = new HashMap<>();

    ColorClay(Material material, int id) {
      this.material = material;
      this.id = id;
    }
    static {
      for(ColorClay cClay:values())
      byID.put(cClay.id,cClay);
    }

    public static ColorClay getByID(int id){
      return byID.get(id);
    }

    public int getId() {
      return id;
    }

    public Material getMaterial() {
      return material;
    }
  }
}
