package com.comze_instancelabs.colormatch.patterns.logic;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import au.com.mineauz.minigames.MinigamePlayer;

import com.comze_instancelabs.colormatch.GameBoard;
import com.comze_instancelabs.colormatch.Utilities;
import com.comze_instancelabs.colormatch.patterns.PatternBase;

public class PreRoundState extends State<GameBoard> {
	
	@Override
	public void onStart(StateEngine<GameBoard> engine, GameBoard game) {
		DyeColor colour = game.getRandomColour();
		game.setColour(colour);
		
		PatternBase pattern = game.getPatternMap().getRandom(game.getRandom());
		game.setCurrentPattern(pattern);
		
		game.generate();
		
		// Let players know what colour it is now
		ItemStack hintItem = Utilities.makeItem(game.getMaterial(), colour);
		ItemMeta meta = hintItem.getItemMeta();
		meta.setDisplayName(Utilities.dyeToChat(colour).toString() + ChatColor.BOLD + colour.name());
		hintItem.setItemMeta(meta);
		
		for(MinigamePlayer player : game.getMinigame().getPlayers()) {
			player.getPlayer().getInventory().clear();
			for (int i = 0; i < 9; ++i) {
				player.getPlayer().getInventory().setItem(i, hintItem);
			}
			
			player.updateInventory();
		}
		
		game.updateSigns(colour.name(), colour);
		
		engine.setState(new RoundState());
	}
}
