package com.bespectacled.modernbeta.world.biome.infdev;

import com.bespectacled.modernbeta.world.biome.ModernBetaBiomeColors;
import com.bespectacled.modernbeta.world.biome.ModernBetaBiomeFeatures;
import com.bespectacled.modernbeta.world.biome.ModernBetaBiomeMobs;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;

public class Infdev420 {
    public static final Biome BIOME = create();
    
    private static Biome create() {
        SpawnSettings.Builder spawnSettings = new SpawnSettings.Builder();
        ModernBetaBiomeMobs.addCommonMobs(spawnSettings);
        ModernBetaBiomeMobs.addSquid(spawnSettings);
        ModernBetaBiomeMobs.addWolves(spawnSettings);
        
        GenerationSettings.Builder genSettings = new GenerationSettings.Builder();
        ModernBetaBiomeFeatures.addInfdev420Features(genSettings);
        
        return (new Biome.Builder())
            .precipitation(Biome.Precipitation.RAIN)
            .category(Biome.Category.FOREST)
            .temperature(0.6F)
            .downfall(0.6F)
            .effects((new BiomeEffects.Builder())
                .grassColor(ModernBetaBiomeColors.OLD_GRASS_COLOR)
                .foliageColor(ModernBetaBiomeColors.OLD_FOLIAGE_COLOR)
                .skyColor(ModernBetaBiomeColors.INFDEV_420_SKY_COLOR)
                .fogColor(ModernBetaBiomeColors.INFDEV_420_FOG_COLOR)
                .waterColor(ModernBetaBiomeColors.OLD_WATER_COLOR)
                .waterFogColor(ModernBetaBiomeColors.OLD_WATER_FOG_COLOR)
                .build())
            .spawnSettings(spawnSettings.build())
            .generationSettings(genSettings.build())
            .build();
    }
}
