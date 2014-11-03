package com.comze_instancelabs.colormatch.modes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.messaging.ReservedChannelException;

import com.comze_instancelabs.colormatch.Main;

public class ColorMatchClayMode {
	
	private static Main m;
	
	public ColorMatchClayMode(Main m){
		this.m = m;
	}
	
	
	public void setup(Location start, Main main, String name_) {
		reset(start, true);
	}
	
	public static void getAll(Location start) {
		m.ints.clear();

		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int z = start.getBlockZ() - 32;

		int current = 0;
		int count = 0;

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x_ = x + i * 4;
				int z_ = z + j * 4;

				Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_, y, z_));

				m.ints.add((int) b.getData());
			}
		}
	}

	public void reset(final Location start) {
		reset(start, false);
	}

	public void reset(final Location start, boolean resetfloor) {
		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int y_ = start.getBlockY() - 4;
		int z = start.getBlockZ() - 32;

		int current = 0;
		m.ints.clear();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x_ = x + i * 4;
				int z_ = z + j * 4;

				current = m.r.nextInt(m.colors.size());
				// ints.add(current);
				m.ints.add((int) m.colors.get(current).getData());

				for (int i_ = 0; i_ < 4; i_++) {
					for (int j_ = 0; j_ < 4; j_++) {
						Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y, z_ + j_));
						b.setType(Material.STAINED_CLAY);
						b.setData(m.colors.get(current).getData());
						if (resetfloor) {
							Block b_ = start.getWorld().getBlockAt(new Location(start.getWorld(), x_ + i_, y_, z_ + j_));
							b_.setType(Material.GLOWSTONE);
						}
					}
				}
			}
		}
	}

	public void removeAllExceptOne(Location start, String arena) {
		// final MassBlockUpdate mbu =
		// CraftMassBlockUpdate.createMassBlockUpdater(m, start.getWorld());

		// mbu.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.NEVER);

		int x = start.getBlockX() - 32;
		int y = start.getBlockY();
		int z = start.getBlockZ() - 32;
		Byte data = m.colors.get(m.a_currentw.get(arena)).getData();

		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				Block b = start.getWorld().getBlockAt(new Location(start.getWorld(), x + i, y, z + j));
				if (b.getData() != data) {
					b.setType(Material.AIR);
					// mbu.setBlock(x + i, y, z + j, 0);
				}
			}
		}

		// mbu.notifyClients();
	}
}
