package com.comze_instancelabs.colormatch.menu;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.colormatch.GameBoard;

import au.com.mineauz.minigames.menu.MenuItem;

public class MenuItemAddPattern extends MenuItem {

	private GameBoard game;
	public MenuItemAddPattern(String name, List<String> description, Material displayItem, GameBoard game) {
		super(name, description, displayItem);
		this.game = game;
	}
	
	public MenuItemAddPattern(String name, Material displayItem, GameBoard game) {
		super(name, displayItem);
		this.game = game;
	}
	
	@Override
	public ItemStack onClick() {
		
		PatternSelectMenu menu = new PatternSelectMenu(getContainer(), game);
		menu.show();
		
		return getItem();
	}
}
