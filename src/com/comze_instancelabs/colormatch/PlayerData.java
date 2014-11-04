package com.comze_instancelabs.colormatch;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {
	private ItemStack[] items;
	private ItemStack[] armour;
	private GameMode gamemode;
	private float xp;
	private int xpLevel;
	
	public PlayerData(Player player) {
		items = player.getInventory().getContents();
		armour = player.getInventory().getArmorContents();
		gamemode = player.getGameMode();
		xp = player.getExp();
		xpLevel = player.getLevel();
	}
	
	public void apply(Player player) {
		player.getInventory().setContents(items);
		player.getInventory().setArmorContents(armour);
		player.setGameMode(gamemode);
		player.setLevel(xpLevel);
		player.setExp(xp);
	}
}
