package au.com.addstar.colormatch;

import au.com.addstar.colormatch.materials.MaterialGroup;
import au.com.addstar.colormatch.materials.MaterialGroupRegistry;
import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemList;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

class MaterialGroupFlag extends Flag<MaterialGroup> {


    private Callback<String> menuCallback;

    public MaterialGroupFlag(MaterialGroup def, String name) {
        setName(name);
        setDefaultFlag(def);
    }

    public MaterialGroupFlag() {
    }

    private static String getName(MaterialGroup group) {
        return group.getGroupName();
    }

    private static Material getMaterial(String name) {
        MaterialGroup group = MaterialGroupRegistry.getGroup(name);
        if (group != null) {
            return group.getMaterials().keySet().iterator().next();
        }
        return null;
    }

    @Override
    public void saveValue(String path, FileConfiguration config) {
        config.set(path + "." + getName(), getFlag().getGroupName());
    }

    @Override
    public void loadValue(String path, FileConfiguration config) {
        String name = config.getString(path + "." + getName());
        if(MaterialGroupRegistry.getGroup(name) != null)
            setFlag(MaterialGroupRegistry.getGroup(name));
        else
            setFlag(MaterialGroupRegistry.getDefaultGroup());
    }

    private Callback<String> getMenuCallback() {
        if (menuCallback == null) {
            menuCallback = new Callback<String>() {
                @Override
                public String getValue() {
                    MaterialGroup mat = getFlag();
                    if (mat == null)
                        mat = getDefaultFlag();
                    return getName(mat);
                }

                @Override
                public void setValue(String name) {
                    setFlag(MaterialGroupRegistry.getGroup(name));
                }
            };
        }

        return menuCallback;
    }

    private Material getDisplayItem(Material def){
        Material material = getFlag().getMaterials().keySet().iterator().next();
        if(material != null)return material;
        return def;
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem) {
        return new MenuItemMaterial(name, getMenuCallback(), displayItem);
    }

    @Override
    public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
        return new MenuItemMaterial(name, description, getMenuCallback(), getDisplayItem(displayItem));
    }

    static class MenuItemMaterial extends MenuItemList {
        MenuItemMaterial(String name, List<String> description, Callback<String> callback, Material displayItem) {
            super(name, description, displayItem, callback, MaterialGroupRegistry.getGroupNames());
        }

        MenuItemMaterial(String name, Callback<String> callback, Material displayItem) {
            super(name, displayItem, callback, MaterialGroupRegistry.getGroupNames());
        }
    }
}
