package com.bespectacled.modernbeta.util;

import net.minecraft.util.Identifier;

public class GUIUtil {
    public static String createTranslatableBiomeStringFromId(String biomeId) {
        String[] strArr = biomeId.split(":");
        
        if (strArr.length != 2)
            throw new IllegalArgumentException("[Modern Beta] Biome Id string is malformed!");
        
        return "biome." + strArr[0] + "." + strArr[1];
    }
    
    public static String createTranslatableBiomeStringFromId(Identifier biomeId) {
        return "biome." + biomeId.getNamespace() + "." + biomeId.getPath();
                
    }
}