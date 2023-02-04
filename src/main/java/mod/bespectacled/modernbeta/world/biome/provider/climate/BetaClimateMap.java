package mod.bespectacled.modernbeta.world.biome.provider.climate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mod.bespectacled.modernbeta.ModernBeta;
import mod.bespectacled.modernbeta.settings.ModernBetaSettingsBiome;
import mod.bespectacled.modernbeta.world.biome.provider.climate.BetaClimateMapping.ClimateType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

public class BetaClimateMap {
    private final Map<String, BetaClimateMapping> climateMap;
    private final BetaClimateMapping[] climateTable;
    
    public BetaClimateMap(ModernBetaSettingsBiome settings) {
        this.climateMap = new LinkedHashMap<>();
        this.climateTable = new BetaClimateMapping[4096];
        
        for (String key : ModernBeta.BIOME_CONFIG.betaClimates.keySet()) {
            this.climateMap.put(key, BetaClimateMapping.fromCompound(settings.betaClimates.get(key).toCompound()));
        }
        
        this.generateBiomeLookup();
    }
    
    public Map<String, BetaClimateMapping> getMap() {
        return new LinkedHashMap<>(this.climateMap);
    }
    
    public RegistryKey<Biome> getBiome(double temp, double rain, ClimateType type) {
        int t = (int) (temp * 63D);
        int r = (int) (rain * 63D);

        return this.climateTable[t + r * 64].biomeByClimateType(type);
    }
    
    public List<RegistryKey<Biome>> getBiomeKeys() {
        List<RegistryKey<Biome>> biomeKeys = new ArrayList<>();
        biomeKeys.addAll(this.climateMap.values().stream().map(p -> p.biome()).toList());
        biomeKeys.addAll(this.climateMap.values().stream().map(p -> p.oceanBiome()).toList());
        biomeKeys.addAll(this.climateMap.values().stream().map(p -> p.deepOceanBiome()).toList());
        
        return biomeKeys;
    }
    
    private void generateBiomeLookup() {
        for (int t = 0; t < 64; t++) {
            for (int r = 0; r < 64; r++) {
                this.climateTable[t + r * 64] = this.getBiome((float) t / 63F, (float) r / 63F);
            }
        }
    }
    
    private BetaClimateMapping getBiome(float temp, float rain) {
        rain *= temp;

        if (temp < 0.1F) {
            return this.climateMap.get("ice_desert");
        }

        if (rain < 0.2F) {
            if (temp < 0.5F) {
                return this.climateMap.get("tundra");
            }
            if (temp < 0.95F) {
                return this.climateMap.get("savanna");
            } else {
                return this.climateMap.get("desert");
            }
        }

        if (rain > 0.5F && temp < 0.7F) {
            return this.climateMap.get("swampland");
        }

        if (temp < 0.5F) {
            return this.climateMap.get("taiga");
        }

        if (temp < 0.97F) {
            if (rain < 0.35F) {
                return this.climateMap.get("shrubland");
            } else {
                return this.climateMap.get("forest");
            }
        }

        if (rain < 0.45F) {
            return this.climateMap.get("plains");
        }

        if (rain < 0.9F) {
            return this.climateMap.get("seasonal_forest");
        } else {
            return this.climateMap.get("rainforest");
        }
    }
}
