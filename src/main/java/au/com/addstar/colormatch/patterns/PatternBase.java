package au.com.addstar.colormatch.patterns;

import au.com.addstar.colormatch.materials.MaterialGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public abstract class PatternBase {
    private HashSet<PatternGroup> groups;

    public abstract int getWidth();

    public abstract int getHeight();

    /**
     * Gets the pixel at the coords. Must return null if the coords are out of range
     */
    protected abstract PatternPixel getPixel(int x, int y);

    private void processPattern() {
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

                while (!floodQueue.isEmpty()) {
                    PatternPixel floodPixel = floodQueue.pop();

                    if (!visited.add(floodPixel))
                        continue;

                    group.getPixels().add(floodPixel);

                    // Pixel to left
                    PatternPixel nextPixel = getPixel(floodPixel.offsetX - 1, floodPixel.offsetY);
                    if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
                        floodQueue.add(nextPixel);

                    // Pixel to right
                    nextPixel = getPixel(floodPixel.offsetX + 1, floodPixel.offsetY);
                    if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
                        floodQueue.add(nextPixel);

                    // Pixel to top
                    nextPixel = getPixel(floodPixel.offsetX, floodPixel.offsetY - 1);
                    if (isMatch(nextPixel, floodPixel) && !visited.contains(nextPixel))
                        floodQueue.add(nextPixel);

                    // Pixel to bottom
                    nextPixel = getPixel(floodPixel.offsetX, floodPixel.offsetY + 1);
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

    public final void placeAt(Location location, MaterialGroup matgroup, List<Block> modified,
                              Random
                                      random, Material currentMaterial) {
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
        int groupnum = groups.size();
        boolean safe = false;
        int i = 1;
        for (PatternGroup group : groups) {
            Material material = matgroup.getRandomMaterial();
            group.placeAt(location, material, modified);
            i++;
        }
    }

    /**
     * Represents a block in the pattern
     */
    public static class PatternPixel {
        final int offsetX;
        final int offsetY;
        final Material material;

        PatternPixel(int offsetX, int offsetY, Material material) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.material = material;
        }

        Location getLocation(Location origin) {
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

            PatternPixel other = (PatternPixel) obj;

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
    static class PatternGroup {
        private final List<PatternPixel> pixels;

        PatternGroup() {
            pixels = new ArrayList<>();
        }

        List<PatternPixel> getPixels() {
            return pixels;
        }

        void placeAt(Location origin, Material material, List<Block> modified) {
            for (PatternPixel pixel : pixels) {
                Location pixelLocation = pixel.getLocation(origin);

                Block block = pixelLocation.getBlock();
                block.setType(material);
                modified.add(block);
            }
        }
    }
}
