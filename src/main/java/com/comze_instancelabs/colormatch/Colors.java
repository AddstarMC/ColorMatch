package com.comze_instancelabs.colormatch;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;


/**
 * Created for use for the Add5tar MC Minecraft server
 * Created by benjamincharlton on 19/12/2018.
 */
public class Colors  {
    private static Map<DyeColor, Set<Material>> colours = new EnumMap<>(DyeColor.class);
    private static Map<Material, DyeColor> materialMap = new EnumMap<>(Material.class);
    
    static {
        Set<Material> red = EnumSet.of(
                Material.RED_CONCRETE,
                Material.RED_CONCRETE_POWDER,
                Material.RED_GLAZED_TERRACOTTA,
                Material.RED_STAINED_GLASS,
                Material.RED_STAINED_GLASS_PANE,
                Material.RED_TERRACOTTA,
                Material.RED_WOOL);
        colours.put(DyeColor.RED, red);
        Set<Material> pink = EnumSet.of(
                Material.PINK_CONCRETE,
                Material.PINK_CONCRETE_POWDER,
                Material.PINK_GLAZED_TERRACOTTA,
                Material.PINK_STAINED_GLASS,
                Material.PINK_STAINED_GLASS_PANE,
                Material.PINK_TERRACOTTA,
                Material.PINK_WOOL);
        colours.put(DyeColor.PINK, pink);
        Set<Material> yellow = EnumSet.of(Material.YELLOW_CONCRETE, Material.YELLOW_CONCRETE_POWDER, Material.YELLOW_GLAZED_TERRACOTTA, Material.YELLOW_STAINED_GLASS, Material.YELLOW_STAINED_GLASS_PANE, Material.YELLOW_TERRACOTTA, Material.YELLOW_WOOL);
        colours.put(DyeColor.YELLOW, yellow);
        Set<Material> orange = EnumSet.of(Material.ORANGE_CONCRETE, Material.ORANGE_CONCRETE_POWDER, Material.ORANGE_GLAZED_TERRACOTTA, Material.ORANGE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS_PANE, Material.ORANGE_TERRACOTTA, Material.ORANGE_WOOL);
        colours.put(DyeColor.ORANGE, orange);
        Set<Material> lime = EnumSet.of(Material.LIME_CONCRETE, Material.LIME_CONCRETE_POWDER, Material.LIME_GLAZED_TERRACOTTA, Material.LIME_STAINED_GLASS, Material.LIME_STAINED_GLASS_PANE, Material.LIME_TERRACOTTA, Material.LIME_WOOL);
        colours.put(DyeColor.LIME, lime);
        Set<Material> cyan = EnumSet.of(Material.CYAN_CONCRETE, Material.CYAN_CONCRETE_POWDER, Material.CYAN_GLAZED_TERRACOTTA, Material.CYAN_STAINED_GLASS, Material.CYAN_STAINED_GLASS_PANE, Material.CYAN_TERRACOTTA, Material.CYAN_WOOL);
        colours.put(DyeColor.CYAN, cyan);
        Set<Material> blue = EnumSet.of(Material.BLUE_CONCRETE, Material.BLUE_CONCRETE_POWDER, Material.BLUE_GLAZED_TERRACOTTA, Material.BLUE_STAINED_GLASS, Material.BLUE_STAINED_GLASS_PANE, Material.BLUE_TERRACOTTA, Material.BLUE_WOOL);
        colours.put(DyeColor.BLUE, blue);
        Set<Material> light_blue = EnumSet.of(Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_CONCRETE_POWDER, Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS_PANE, Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_BLUE_WOOL);
        colours.put(DyeColor.LIGHT_BLUE, light_blue);
        Set<Material> green = EnumSet.of(Material.GREEN_CONCRETE, Material.GREEN_CONCRETE_POWDER, Material.GREEN_GLAZED_TERRACOTTA, Material.GREEN_STAINED_GLASS, Material.GREEN_STAINED_GLASS_PANE, Material.GREEN_TERRACOTTA, Material.GREEN_WOOL);
        colours.put(DyeColor.GREEN, green);
        Set<Material> purple = EnumSet.of(Material.PURPLE_CONCRETE, Material.PURPLE_CONCRETE_POWDER, Material.PURPLE_GLAZED_TERRACOTTA, Material.PURPLE_STAINED_GLASS, Material.PURPLE_STAINED_GLASS_PANE, Material.PURPLE_TERRACOTTA, Material.PURPLE_WOOL);
        colours.put(DyeColor.PURPLE, purple);
        Set<Material> magenta = EnumSet.of(Material.MAGENTA_CONCRETE, Material.MAGENTA_CONCRETE_POWDER, Material.MAGENTA_GLAZED_TERRACOTTA, Material.MAGENTA_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS_PANE, Material.MAGENTA_TERRACOTTA, Material.MAGENTA_WOOL);
        colours.put(DyeColor.MAGENTA, magenta);
        Set<Material> light_gray = EnumSet.of(Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_CONCRETE_POWDER, Material.LIGHT_GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_STAINED_GLASS, Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.LIGHT_GRAY_TERRACOTTA, Material.LIGHT_GRAY_WOOL);
        colours.put(DyeColor.LIGHT_GRAY, light_gray);
        Set<Material> gray = EnumSet.of(Material.GRAY_CONCRETE, Material.GRAY_CONCRETE_POWDER, Material.GRAY_GLAZED_TERRACOTTA, Material.GRAY_STAINED_GLASS, Material.GRAY_STAINED_GLASS_PANE, Material.GRAY_TERRACOTTA, Material.GRAY_WOOL);
        colours.put(DyeColor.GRAY, gray);
        Set<Material> black = EnumSet.of(Material.BLACK_CONCRETE, Material.BLACK_CONCRETE_POWDER, Material.BLACK_GLAZED_TERRACOTTA, Material.BLACK_STAINED_GLASS, Material.BLACK_STAINED_GLASS_PANE, Material.BLACK_TERRACOTTA, Material.BLACK_WOOL);
        colours.put(DyeColor.BLACK, black);
        Set<Material> white = EnumSet.of(Material.WHITE_CONCRETE, Material.WHITE_CONCRETE_POWDER, Material.WHITE_GLAZED_TERRACOTTA, Material.WHITE_STAINED_GLASS, Material.WHITE_STAINED_GLASS_PANE, Material.WHITE_TERRACOTTA, Material.WHITE_WOOL);
        colours.put(DyeColor.WHITE, white);
        Set<Material> brown = EnumSet.of(Material.BROWN_CONCRETE, Material.BROWN_CONCRETE_POWDER, Material.BROWN_GLAZED_TERRACOTTA, Material.BROWN_STAINED_GLASS, Material.BROWN_STAINED_GLASS_PANE, Material.BROWN_TERRACOTTA, Material.BROWN_WOOL);
        colours.put(DyeColor.BROWN, brown);
    
        for(Map.Entry<DyeColor,Set<Material>> entry:colours.entrySet()) {
            for(Material mat:entry.getValue()){
                materialMap.put(mat,entry.getKey());
            }
        }
    }
    public static DyeColor getBlockColour(Block block){
        if(isColorable(block.getType())){
            return getColour(block.getType());
        }
        return null;
    }
    
    public static Material modifyColour(Material material, DyeColor color){
        if(isColorable(material)){
            String newMatName = color.name()+"_"+baseMaterialName(material);
            Material modified = Material.matchMaterial(newMatName);
            if(isColour(modified,color))return modified;
        }
        return null;
    }
    public static boolean isColour(Material mat, DyeColor color){
        Set<Material> matSet = colours.get(color);
        if(matSet != null)return matSet.contains(mat);
        return false;
    }

    public static boolean isColorable(Material material){
        return materialMap.containsKey(material);
    }
    
    private static String baseMaterialName(Material material){
        DyeColor color = getColour(material);
        String name = material.name().replace(color.name()+"_","");
        return name;
    }

    public static DyeColor getColour(Material material){
        return materialMap.get(material);
    }
}
