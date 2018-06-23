package au.com.addstar.colormatch.materials;

import au.com.addstar.colormatch.ColorMatch;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created for the AddstarMC Project. Created by Narimm on 22/06/2018.
 */
public class MaterialGroupRegistry {
    static {
        createStandardGroup();
    }
    private static final List<String>groupNames = new ArrayList<>();
    private static final List<MaterialGroup> materialGroups = new ArrayList<>();

    public static List<MaterialGroup> getMaterialGroups() {
        return materialGroups;
    }

    public static List<String> getGroupNames() {
        List<String> result = new ArrayList<>();
        for (MaterialGroup group : materialGroups) {
            result.add(group.getGroupName());
        }
        return result;
    }

    public static MaterialGroup getDefaultGroup() {
        return materialGroups.iterator().next();
    }

    public static MaterialGroup getGroup(String name) {
        for (MaterialGroup group : materialGroups) {
            if (group.getGroupName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    private static void createStandardGroup() {
        Map<Material, String> materials = new HashMap<>();
        materials.put(Material.BLACK_WOOL, "Black");
        materials.put(Material.YELLOW_WOOL, "Yellow");
        materials.put(Material.RED_WOOL, "Red");
        materials.put(Material.BLUE_WOOL, "Blue");
        materials.put(Material.GREEN_WOOL, "Green");
        materials.put(Material.BROWN_WOOL, "Brown");
        materials.put(Material.CYAN_WOOL, "Cyan");
        materials.put(Material.ORANGE_WOOL, "Orange");
        materials.put(Material.PURPLE_WOOL, "Purple");
        MaterialGroup woolgroup = new MaterialGroup("Standard Wool Colours", materials);
        addGroup(woolgroup);
    }
    private static boolean hasGroup(MaterialGroup group){
        String name = group.getGroupName();
        for (String existing: groupNames){
            if (existing.equals(name))return true;
        }
        return false;
    }


    private static boolean addGroup(MaterialGroup group) {
        if(hasGroup(group))return false;
        return materialGroups.add(group);
    }

    public static void loadGroups(){
        File base = new File(ColorMatch.plugin.getDataFolder(), "materials");
        if (!base.exists()) {
            return;
        }
        for (File file : base.listFiles()) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            loadGroup(yamlConfiguration);
        }
    }

    private static void loadGroup(YamlConfiguration config){
        String groupName = config.getString("GroupName");
        Map<Material, String> materials = new HashMap<>();
        if(config.contains("Materials")) {
            ConfigurationSection section = config.getConfigurationSection("Materials");
            for( String key : section.getKeys(false)){
                Material mat =  Material.valueOf(section.getString(key));
                materials.put(mat,key);
            }
        }
        if(groupName != null && materials.size() > 0){
            MaterialGroup group = new MaterialGroup(groupName,materials);
            if(!addGroup(group)){
                ColorMatch.plugin.getLogger().warning(groupName + " materials could not be loaded into the registry");
                return;
            }
            ColorMatch.plugin.getLogger().info("[ColourMatch]" +groupName + " has been added as a Material selection.");

        }
    }

    public static void saveGroups() {
        File base = new File(ColorMatch.plugin.getDataFolder(), "materials");
        base.mkdir();
        for(MaterialGroup group : materialGroups){
            File file = new File(base,group.getGroupName()+".yml");
            YamlConfiguration configuration = new YamlConfiguration();
            configuration.set("GroupName",group.getGroupName());
            ConfigurationSection section = configuration.createSection("Materials");
            for(Map.Entry<Material, String> entry: group.getMaterials().entrySet())
            section.set(entry.getValue(),entry.getKey());
            try {
                configuration.save(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
