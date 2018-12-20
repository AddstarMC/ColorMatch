package com.comze_instancelabs.colormatch;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created for the AddstarMC Project. Created by Narimm on 20/12/2018.
 */
public class ColorsTest {
    
    private static Colors colors;
    @Before
    public void Setup(){
        colors = new Colors();
    }
    
    @Test
    public void getColour() {
        assertEquals(DyeColor.BLACK,Colors.getColour(Material.BLACK_STAINED_GLASS));
    }
    
    @Test
    public void modifyColour() {
        assertEquals(Material.WHITE_CONCRETE,Colors.modifyColour(Material.CYAN_CONCRETE, DyeColor.WHITE));
        assertEquals(Material.RED_GLAZED_TERRACOTTA,Colors.modifyColour(Material.WHITE_GLAZED_TERRACOTTA,DyeColor.RED));
        assertNull(Colors.modifyColour(Material.CYAN_BED,DyeColor.RED));
    }
}