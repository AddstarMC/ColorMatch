package com.comze_instancelabs.colormatch.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.colormatch.GameBoard;
import au.com.mineauz.minigames.menu.MenuItem;

public class MenuItemShowPatterns extends MenuItem {
	private GameBoard game;
	
	public MenuItemShowPatterns(String name, List<String> description, Material displayItem, GameBoard game) {
		super(name, description, displayItem);
		this.game = game;
	}

	public MenuItemShowPatterns(String name, Material displayItem, GameBoard game) {
		super(name, displayItem);
		this.game = game;
	}
	
	@Override
	public ItemStack onClick() {
		PatternListMenu menu = new PatternListMenu(game, getContainer());
		menu.show(getContainer().getViewer());
		
		return getItem();
	}
}
