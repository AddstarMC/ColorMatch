package com.comze_instancelabs.colormatch.menu;

import java.util.Arrays;

import au.com.mineauz.minigames.MinigameMessageType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.colormatch.Util.WeightedPatternMap.WeightedPattern;
import com.comze_instancelabs.colormatch.patterns.PatternBase;
import com.comze_instancelabs.colormatch.patterns.PatternRegistry;

import au.com.mineauz.minigames.MinigamePlayer;
import au.com.mineauz.minigames.menu.InteractionInterface;
import au.com.mineauz.minigames.menu.Menu;
import au.com.mineauz.minigames.menu.MenuItem;
import au.com.mineauz.minigames.menu.MenuItemCustom;
import au.com.mineauz.minigames.menu.MenuItemPage;

public class PatternSelectMenu {
	private final int rows = 6;
	private final int itemsPerPage = 9 * (rows-1);
	
	private PatternListMenu previous;
	
	public PatternSelectMenu(PatternListMenu menu) {
		previous = menu;
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
	
	private Menu createPatternAddMenu(final MinigamePlayer viewer, int page) {
    	Menu menu = new Menu(rows, "Add Pattern", viewer);
    	
    	int index = 0;
    	for (String name : PatternRegistry.getPatterns()) {
    		if (index >= getPageStart(page) && index < getPageEnd(page)) {
    			menu.addItem(new MenuItemAddPatternObject(name, Material.BRICK, name, PatternRegistry.getPattern(name)));
    		}
    		++index;
    	}
    	
    	// Add controls
    	MenuItemCustom done = new MenuItemCustom("Back", Material.REDSTONE_TORCH_ON);
    	done.setClick(new InteractionInterface() {
			@Override
			public Object interact(Object object) {
				previous.show(viewer);
				return null;
			}
		});
    	menu.addItem(done, menu.getSize() - 9);
		
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
		Menu menu = createPatternAddMenu(previous.getPrevious().getViewer());
		menu.displayMenu(previous.getPrevious().getViewer());
	}
	
	private class MenuItemAddPatternObject extends MenuItem {

		private String patternName;
		private PatternBase pattern;
		
		public MenuItemAddPatternObject(String name, Material displayItem, String patternName, PatternBase pattern) {
			super(name, displayItem);
			this.patternName = patternName;
			this.pattern = pattern;
			setDescription(Arrays.asList(ChatColor.YELLOW + "Size: " + ChatColor.WHITE + pattern.getWidth() + "x" + pattern.getHeight(), ChatColor.GRAY.toString() + ChatColor.ITALIC + "Left click to enter weight and add."));
		}
		
		@Override
		public ItemStack onClick() {
			MinigamePlayer player = getContainer().getViewer();
			player.setNoClose(true);
			player.getPlayer().closeInventory();
			player.sendInfoMessage("Enter the weight of this pattern, an integer 1 or more. The higher its value, the more likely it is to be chosen. The menu will automatically reopen in 20s if nothing is entered.");
			player.setManualEntry(this);
			getContainer().startReopenTimer(20);;
			
			return null;
		}
		
		@Override
		public void checkValidEntry(String entry) {
			try {
				int weight = Integer.parseInt(entry);
				if (weight < 1) {
					getContainer().getViewer().sendMessage( "Invalid weight value. Must be an integer 1 or higher.", MinigameMessageType.ERROR);
				} else {
					WeightedPattern wPattern = new WeightedPattern(weight, patternName, pattern);
					previous.getGame().getPatternMap().add(wPattern);
				}
			} catch(NumberFormatException e) {
				getContainer().getViewer().sendMessage( "Invalid weight value. Must be an integer 1 or higher.", MinigameMessageType.ERROR);
			}
			
			// Open the parent menu again
			getContainer().cancelReopenTimer();
			previous.show(getContainer().getViewer());
		}
	}
}
