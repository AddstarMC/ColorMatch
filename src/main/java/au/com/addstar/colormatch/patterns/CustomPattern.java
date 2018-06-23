package au.com.addstar.colormatch.patterns;

import au.com.addstar.colormatch.ColorMatch;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class CustomPattern extends PatternBase {
    private static final String oldfileHeader = "CMPAT";
    private static final String fileHeader = "CMPAT2";

    private int width;
    private int height;

    private PatternPixel[] pixels;

    private CustomPattern() {
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
        for (int x = 0; x < width; ++x) {
            for (int z = 0; z < height; ++z) {
                Block block = corner1.getWorld().getBlockAt(minX + x, corner1.getBlockY(), minZ + z);
                pattern.pixels[x + z * width] = new PatternPixel(x, z, block.getState().getType());
            }
        }

        return pattern;
    }

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

    private boolean checkType(DataInputStream in, String header) throws IOException {
        for (int i = 0; i < header.length(); ++i) {
            if (in.readChar() != header.charAt(i)) {
                return false;
            }
        }

        return true;
    }

    private boolean load(File file) {
        try (FileInputStream stream = new FileInputStream(file);
             DataInputStream in = new DataInputStream(stream)) {
            if (checkType(in, oldfileHeader)) {
                return loadOld(file, in);
            }
            if (!checkType(in, fileHeader)) {
                return false;
            }

            int version = in.readUnsignedByte();
            if (version != 1) {
                return false;
            }

            width = in.readUnsignedShort();
            height = in.readUnsignedShort();

            // Read the materials
            HashMap<Integer, Material> mats = new HashMap<>();
            int matCount = in.readShort();
            for (int i = 0; i < matCount; ++i) {
                int id = in.readUnsignedShort();
                String name = in.readUTF();
                mats.put(id, Material.valueOf(name));
            }
            // Read pixels
            pixels = new PatternPixel[width * height];
            for (int i = 0; i < pixels.length; ++i) {
                Material mdata = mats.get(in.readUnsignedShort());
                if (mdata == null)
                    throw new IOException("Read an unknown material id");
                int x = i % width;
                int y = i / width;
                pixels[i] = new PatternPixel(x, y, mdata);
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean loadOld(File file, DataInputStream in) {
        try {
            if (!checkType(in, oldfileHeader)) {
                return false;
            }

            int version = in.readUnsignedByte();
            if (version != 1) {
                return false;
            }

            width = in.readUnsignedShort();
            height = in.readUnsignedShort();

            // Read the materials
            HashMap<Integer, MaterialData> mats = new HashMap<>();
            int matCount = in.readShort();
            for (int i = 0; i < matCount; ++i) {
                int id = in.readUnsignedShort();
                String name = in.readUTF();
                byte data = in.readByte();

                mats.put(id, new MaterialData(Material.valueOf(name), data));
            }

            // Read pixels
            pixels = new PatternPixel[width * height];
            for (int i = 0; i < pixels.length; ++i) {
                MaterialData mdata = mats.get(in.readUnsignedShort());
                if (mdata == null)
                    throw new IOException("Read an unknown material id");

                int x = i % width;
                int y = i / width;
                pixels[i] = new PatternPixel(x, y, mdata.getItemType());
            }

            if (file.delete()) {
                ColorMatch.plugin.getLogger().info(file.getName() + " has been removed and will be re-saved in the new format");
            }
            if (save(file)) {
                ColorMatch.plugin.getLogger().info(file.getName() + " has been saved in the new format");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
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
        try {
            FileOutputStream stream = new FileOutputStream(file);
            out = new DataOutputStream(stream);

            out.writeChars(fileHeader);
            out.writeByte(1); // Version

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
                } catch (IOException ignored) {
                }
            }
        }
    }
}
