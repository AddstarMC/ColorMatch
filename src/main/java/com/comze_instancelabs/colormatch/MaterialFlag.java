package com.comze_instancelabs.colormatch;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import au.com.mineauz.minigames.config.Flag;
import au.com.mineauz.minigames.menu.Callback;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemList;

public class MaterialFlag extends Flag<Material> {
	private static final Material[] materials = new Material[] {Material.WOOL, Material.STAINED_CLAY, Material.STAINED_GLASS, Material.STAINED_GLASS_PANE, Material.CONCRETE};
	private static final String[] materialNames = new String[] {"wool", "clay", "glass", "glass_pane", "concrete" };
	
	public MaterialFlag(Material def, String name) {
		setName(name);
		setDefaultFlag(def);
	}
	
	public MaterialFlag() {}
	
	@Override
	public void saveValue(String path, FileConfiguration config) {
		for (int i = 0; i < materials.length; ++i) {
			if (materials[i] == getFlag()) {
				config.set(path + "." + getName(), materialNames[i]);
			}
		}
	}

	@Override
	public void loadValue(String path, FileConfiguration config) {
		String name = config.getString(path + "." + getName());
		for (int i = 0; i < materials.length; ++i) {
			if (name.equalsIgnoreCase(materialNames[i])) {
				setFlag(materials[i]);
			}
		}
	}
	
	private static String getName(Material material) {
		for (int i = 0; i < materials.length; ++i) {
			if (materials[i] == material) {
				return materialNames[i];
			}
		}
		return null;
	}
	
	private static Material getMaterial(String name) {
		for (int i = 0; i < materials.length; ++i) {
			if (name.equalsIgnoreCase(materialNames[i])) {
				return materials[i];
			}
		}
		return null;
	}
	
	private Callback<String> menuCallback;
	
	private Callback<String> getMenuCallback() {
		if (menuCallback == null) {
			menuCallback = new Callback<String>() {
				@Override
				public void setValue(String name) {
					setFlag(getMaterial(name));
				}
				
				@Override
				public String getValue() {
					Material mat = getFlag();
					if (mat == null)
						mat = getDefaultFlag();
					return getName(mat);
				}
			};
		}
		
		return menuCallback;
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem) {
		return new MenuItemMaterial(name, getMenuCallback(), displayItem);
	}

	@Override
	public MenuItem getMenuItem(String name, Material displayItem, List<String> description) {
		return new MenuItemMaterial(name, description, getMenuCallback(), displayItem);
	}
	
	public static class MenuItemMaterial extends MenuItemList {
		public MenuItemMaterial(String name, List<String> description, Callback<String> callback, Material displayItem) {
			super(name, description, displayItem, callback, Arrays.asList(materialNames));
		}
		
		public MenuItemMaterial(String name, Callback<String> callback, Material displayItem) {
			super(name, displayItem, callback, Arrays.asList(materialNames));
		}
	}
}
