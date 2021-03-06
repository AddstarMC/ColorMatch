package com.comze_instancelabs.colormatch.menu;

import java.util.Arrays;

import au.com.mineauz.minigames.objects.MinigamePlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemBack;
import au.com.mineauz.minigames.menu.MenuItemPage;

import com.comze_instancelabs.colormatch.GameBoard;
import com.comze_instancelabs.colormatch.Util.WeightedPatternMap.WeightedPattern;

public class PatternListMenu {
	private final int rows = 6;
	private final int itemsPerPage = 9 * (rows-1);
	
	private GameBoard game;
	private Menu container;
	
	public PatternListMenu(GameBoard game, Menu container) {
		this.game = game;
		this.container = container;
	}
	
	private int getPatternCount() {
		return game.getPatternMap().getPatterns().size();
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
    			menu.addItem(new MenuItemCurrentPattern(Material.PAPER, pattern));
    		}
    	}
    	
    	// Add controls
    	menu.addItem(new MenuItemBack(container), menu.getSize() - 9);
    	menu.addItem(new MenuItemAddPattern("Add Pattern", Material.WHITE_TERRACOTTA, this), menu.getSize() - 1);
		
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
			last.addItem(new MenuItemPage("Next Page", Material.ENDER_EYE, next), 9 * (rows - 1) + 5);
			next.addItem(new MenuItemPage("Previous Page", Material.ENDER_EYE, last), 9 * (rows - 1) + 3);
			last = next;
		}
		
		return base;
	}
	
	public void show(MinigamePlayer player) {
		Menu menu = createMenu(player);
		menu.displayMenu(player);
	}
	
	public Menu getPrevious() {
		return container;
	}
	
	public GameBoard getGame() {
		return game;
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
			show(getContainer().getViewer());
			return getItem();
		}
	}
}
