package com.comze_instancelabs.colormatch.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.colormatch.GameBoard;
import com.comze_instancelabs.colormatch.Util.WeightedPatternMap.WeightedPattern;
import com.comze_instancelabs.colormatch.patterns.PatternBase;
import com.comze_instancelabs.colormatch.patterns.PatternRegistry;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemPage;

public class PatternSelectMenu {
	private final int rows = 6;
	private final int itemsPerPage = 9 * (rows-1);
	
	private Menu container;
	private GameBoard game;
	
	public PatternSelectMenu(Menu container, GameBoard game) {
		this.container = container;
		this.game = game;
	}
	
	private int getPatternCount() {
		return PatternRegistry.getPatterns().size();
	}
	
	private int getPageCount() {
		return (int)Math.ceil(getPatternCount() / (double)itemsPerPage);
	}
	
	private int getPageStart(int page) {
		return page * itemsPerPage;
	}
	
	private int getPageEnd(int page) {
		return getPageStart(page) + itemsPerPage;
	}
	
	private Menu createPatternAddMenu(MinigamePlayer viewer, int page) {
    	Menu menu = new Menu(rows, "Add Pattern", viewer);
    	
    	int index = 0;
    	for (String name : PatternRegistry.getPatterns()) {
    		if (index >= getPageStart(page) && index < getPageEnd(page)) {
    			menu.addItem(new MenuItemAddPatternObject(name, Material.STAINED_CLAY, name, PatternRegistry.getPattern(name)));
    		}
    		++index;
    	}
    	
    	// Add controls
    	menu.addItem(new MenuItemPage("Done", Material.REDSTONE_TORCH_ON, container), menu.getSize() - 9);
		
		return menu;
    }
	
	private Menu createPatternAddMenu(MinigamePlayer player) {
		Menu base = createPatternAddMenu(player, 0);
		Menu last = base;
		for (int i = 1; i < getPageCount(); ++i) {
			Menu next = createPatternAddMenu(player, i);
			last.setNextPage(next);
			next.setPreviousPage(last);
			
			// Add controls
			last.addItem(new MenuItemPage("Next Page", Material.EYE_OF_ENDER, next), 9 * (rows - 1) + 5);
			next.addItem(new MenuItemPage("Previous Page", Material.EYE_OF_ENDER, last), 9 * (rows - 1) + 3);
			last = next;
		}
		
		return base;
	}
	
	public void show() {
		Menu menu = createPatternAddMenu(container.getViewer());
		menu.displayMenu(container.getViewer());
	}
	
	private class MenuItemAddPatternObject extends MenuItem {

		private String patternName;
		private PatternBase pattern;
		
		public MenuItemAddPatternObject(String name, Material displayItem, String patternName, PatternBase pattern) {
			super(name, displayItem);
			this.patternName = patternName;
			this.pattern = pattern;
		}
		
		@Override
		public ItemStack onClick() {
			MinigamePlayer player = getContainer().getViewer();
			player.setNoClose(true);
			player.getPlayer().closeInventory();
			player.sendMessage("Enter the weight of this pattern. An integer 1 or more. The higher its value, the more likely it is to be chosen. The menu will automatically reopen in 20s if nothing is entered.");
			player.setManualEntry(this);
			getContainer().startReopenTimer(20);;
			
			return null;
		}
		
		@Override
		public void checkValidEntry(String entry) {
			try {
				int weight = Integer.parseInt(entry);
				if (weight < 1) {
					getContainer().getViewer().sendMessage(ChatColor.RED + "Invalid weight value. Must be an integer 1 or higher.");
				} else {
					WeightedPattern wPattern = new WeightedPattern(weight, patternName, pattern);
					game.getPatternMap().add(wPattern);
				}
			} catch(NumberFormatException e) {
				getContainer().getViewer().sendMessage(ChatColor.RED + "Invalid weight value. Must be an integer 1 or higher.");
			}
			
			// Open the parent menu again
			getContainer().cancelReopenTimer();
			container.displayMenu(getContainer().getViewer());
		}
	}
}
