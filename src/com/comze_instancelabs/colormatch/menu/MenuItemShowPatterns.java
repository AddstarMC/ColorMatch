package com.comze_instancelabs.colormatch.menu;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.colormatch.GameBoard;
import com.comze_instancelabs.colormatch.Util.WeightedPatternMap.WeightedPattern;
import com.comze_instancelabs.colormatch.patterns.PatternRegistry;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemPage;

public class MenuItemShowPatterns extends MenuItem {
	private final int rows = 6;
	private final int itemsPerPage = 9 * (rows-1);
	
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
		Menu menu = createMenu(getContainer().getViewer());
		menu.displayMenu(getContainer().getViewer());
		
		return getItem();
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
	
	private Menu createMenu(MinigamePlayer viewer, int page) {
    	Menu menu = new Menu(rows, "Patterns", viewer);
    	
    	if (getPageStart(page) < game.getPatternMap().getPatterns().size()) {
    		for (int i = getPageStart(page); i < game.getPatternMap().getPatterns().size() && i < getPageEnd(page); ++i) {
    			WeightedPattern pattern = game.getPatternMap().getPatterns().get(i);
    			menu.addItem(new MenuItemCurrentPattern(Material.STAINED_CLAY, pattern));
    		}
    	}
    	
    	// Add controls
    	menu.addItem(new MenuItemBack(getContainer()), menu.getSize() - 9);
    	menu.addItem(new MenuItemAddPattern("Add Pattern", Material.STAINED_CLAY, game), menu.getSize() - 3);
		
		return menu;
    }
	
	private Menu createMenu(MinigamePlayer player) {
		Menu base = createMenu(player, 0);
		Menu last = base;
		for (int i = 1; i < getPageCount(); ++i) {
			Menu next = createMenu(player, i);
			last.setNextPage(next);
			next.setPreviousPage(last);
			
			// Add controls
			last.addItem(new MenuItemPage("Next Page", Material.EYE_OF_ENDER, next), 9 * (rows - 1) + 5);
			next.addItem(new MenuItemPage("Previous Page", Material.EYE_OF_ENDER, last), 9 * (rows - 1) + 3);
			last = next;
		}
		
		return base;
	}
	
	private class MenuItemCurrentPattern extends MenuItem {

		private WeightedPattern pattern;
		public MenuItemCurrentPattern(Material displayItem, WeightedPattern pattern) {
			super(pattern.getPatternName(), displayItem);
			setDescription(Arrays.asList(ChatColor.YELLOW + "Weight: " + pattern.getWeight(), ChatColor.GRAY.toString() + ChatColor.ITALIC + "Shift+Right click to remove."));
			this.pattern = pattern;
		}
		
		@Override
		public ItemStack onShiftRightClick() {
			game.getPatternMap().remove(pattern);
			Menu remake = createMenu(getContainer().getViewer());
			remake.displayMenu(getContainer().getViewer());
			return getItem();
		}
		
	}
}
