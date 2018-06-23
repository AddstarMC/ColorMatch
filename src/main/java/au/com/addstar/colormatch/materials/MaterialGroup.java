package au.com.addstar.colormatch.materials;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created for the AddstarMC Project. Created by Narimm on 22/06/2018.
 * Represents a group of materials that can be used to create a pattern
 */
public class MaterialGroup {
    private final String groupName;
    private final int materialCount;
    private final Map<Material, String> materials;

    public MaterialGroup(String groupName, Map<Material, String> materials) {
        this.groupName = groupName;
        this.materials = materials;
        this.materialCount = materials.size();
    }

    public String getGroupName() {
        return groupName;
    }

    public int getMaterialCount() {
        return materialCount;
    }

    public Map<Material, String> getMaterials() {
        return materials;
    }

    public Material getRandomMaterial() {
        List<Material> keys = new ArrayList<>(materials.keySet());
        return keys.get(new Random().nextInt(materialCount));
    }

    public String getMaterialName(Material material) {
        return materials.get(material);
    }
}
