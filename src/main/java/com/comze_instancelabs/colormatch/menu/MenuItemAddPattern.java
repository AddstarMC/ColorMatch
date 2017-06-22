package com.comze_instancelabs.colormatch.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.menu.MenuItem;

public class MenuItemAddPattern extends MenuItem {

	private PatternListMenu menu;
	
	public MenuItemAddPattern(String name, Material displayItem, PatternListMenu menu) {
		super(name, displayItem);
		this.menu = menu;
	}
	
	@Override
	public ItemStack onClick() {
		
		PatternSelectMenu selectMenu = new PatternSelectMenu(menu);
		selectMenu.show();
		
		return getItem();
	}
}
