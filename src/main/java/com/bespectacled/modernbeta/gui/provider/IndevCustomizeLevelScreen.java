package com.bespectacled.modernbeta.gui.provider;

import java.util.Arrays;
import java.util.function.BiConsumer;

import com.bespectacled.modernbeta.ModernBeta;
import com.bespectacled.modernbeta.api.AbstractScreenProvider;
import com.bespectacled.modernbeta.api.BiomeProviderType;
import com.bespectacled.modernbeta.biome.indev.IndevUtil;
import com.bespectacled.modernbeta.biome.indev.IndevUtil.IndevTheme;
import com.bespectacled.modernbeta.biome.indev.IndevUtil.IndevType;
import com.bespectacled.modernbeta.gui.ScreenButtonOption;
import com.bespectacled.modernbeta.gui.TextOption;
import com.bespectacled.modernbeta.util.GUIUtil;

import net.minecraft.client.gui.screen.CustomizeBuffetLevelScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.option.CyclingOption;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

public class IndevCustomizeLevelScreen extends AbstractScreenProvider {
    private IndevType levelType;
    private IndevTheme levelTheme;

    private int levelWidth;
    private int levelLength;
    private int levelHeight;
    
    private float caveRadius;
    
    public IndevCustomizeLevelScreen(
        CreateWorldScreen parent, 
        DynamicRegistryManager registryManager, 
        NbtCompound biomeProviderSettings, 
        NbtCompound chunkProviderSettings, 
        BiConsumer<NbtCompound, NbtCompound> consumer
    ) {
        super(parent, registryManager, biomeProviderSettings, chunkProviderSettings, consumer);
        
        this.levelType = this.chunkProviderSettings.contains("levelType") ? 
            IndevType.fromName(this.chunkProviderSettings.getString("levelType")) : 
            IndevType.fromName(ModernBeta.BETA_CONFIG.generationConfig.indevLevelType);
        
        this.levelTheme = this.chunkProviderSettings.contains("levelTheme") ? 
            IndevTheme.fromName(this.chunkProviderSettings.getString("levelTheme")) :                 
            IndevTheme.fromName(ModernBeta.BETA_CONFIG.generationConfig.indevLevelTheme);
        
        this.levelWidth = this.chunkProviderSettings.contains("levelWidth") ?
            this.chunkProviderSettings.getInt("levelWidth") :
            ModernBeta.BETA_CONFIG.generationConfig.indevLevelWidth;
        
        this.levelLength = this.chunkProviderSettings.contains("levelLength") ?
            this.chunkProviderSettings.getInt("levelLength") :
            ModernBeta.BETA_CONFIG.generationConfig.indevLevelLength;
        
        this.levelHeight = this.chunkProviderSettings.contains("levelHeight") ?
            this.chunkProviderSettings.getInt("levelHeight") :
            ModernBeta.BETA_CONFIG.generationConfig.indevLevelHeight;
        
        this.caveRadius = this.chunkProviderSettings.contains("caveRadius") ?
            this.chunkProviderSettings.getFloat("caveRadius") :
            ModernBeta.BETA_CONFIG.generationConfig.indevCaveRadius;
        
        
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Get Indev Theme list, sans legacy themes
        IndevTheme[] indevThemes = Arrays.stream(IndevTheme.values()).filter(theme -> IndevUtil.IndevTheme.getExclusions(theme)).toArray(IndevTheme[]::new);
        
        this.biomeOption = new ScreenButtonOption(
            "createWorld.customize.biomeType.biome",
            GUIUtil.createTranslatableBiomeStringFromId(this.singleBiome),
            biomeType -> this.biomeType == BiomeProviderType.SINGLE,
            buttonWidget -> this.client.openScreen(new CustomizeBuffetLevelScreen(
              this, 
              this.registryManager,
              (biome) -> {
                  this.singleBiome = this.registryManager.<Biome>get(Registry.BIOME_KEY).getId(biome).toString();
                  this.biomeProviderSettings.putString("singleBiome", this.singleBiome.toString());
              },
              this.registryManager.<Biome>get(Registry.BIOME_KEY).get(new Identifier(this.singleBiome))  
            ))
        );
        
        this.buttonList.addOptionEntry(
            CyclingOption.create(
                "createWorld.customize.indev.levelTheme", 
                indevThemes, 
                (value) -> new TranslatableText("createWorld.customize.indev.theme." + value.getName()), 
                (gameOptions) -> { return this.levelTheme; }, 
                (gameOptions, option, value) -> {
                    this.levelTheme = value;
                    this.chunkProviderSettings.putString("levelTheme", this.levelTheme.getName());
                    this.biomeProviderSettings.putString("singleBiome", this.levelTheme.getDefaultBiome().toString());
                    
                    this.client.openScreen(
                        this.worldProvider.createLevelScreen(
                            this.parent, 
                            this.registryManager, 
                            this.biomeProviderSettings, 
                            this.chunkProviderSettings, 
                            this.consumer
                    ));
                }
            ),
            this.biomeOption
        );
        
        this.updateButtonActive(this.biomeOption);
        
        this.buttonList.addSingleOptionEntry(
                CyclingOption.create(
                    "createWorld.customize.indev.levelType", 
                    IndevType.values(), 
                    (value) -> new TranslatableText("createWorld.customize.indev.type." + value.getName()), 
                    (gameOptions) -> { return this.levelType; }, 
                    (gameOptions, option, value) -> {
                        this.levelType = value;
                        this.chunkProviderSettings.putString("levelType", this.levelType.getName());
                    })
            );
        
        this.buttonList.addSingleOptionEntry(
            new DoubleOption(
                "createWorld.customize.indev.widthSlider", 
                128D, 1024D, 128f,
                (gameOptions) -> { return (double) this.levelWidth; }, // Getter
                (gameOptions, value) -> { // Setter
                    this.levelWidth = value.intValue();
                    this.chunkProviderSettings.putInt("levelWidth", this.levelWidth);
                },
                (gameOptions, doubleOptions) -> {
                    return new TranslatableText(
                        "options.generic_value", 
                        new Object[] { 
                            new TranslatableText("createWorld.customize.indev.levelWidth"), 
                            Text.of(String.valueOf(this.levelWidth)) 
                    });
                }
        ));
        
        this.buttonList.addSingleOptionEntry(
            new DoubleOption(
                "createWorld.customize.indev.lengthSlider", 
                128D, 1024D, 128f,
                (gameOptions) -> { return (double) this.levelLength; }, // Getter
                (gameOptions, value) -> { // Setter
                    this.levelLength = value.intValue();
                    this.chunkProviderSettings.putInt("levelLength", this.levelLength);
                },
                (gameOptions, doubleOptions) -> {
                    return new TranslatableText(
                        "options.generic_value", 
                        new Object[] { 
                            new TranslatableText("createWorld.customize.indev.levelLength"), 
                            Text.of(String.valueOf(this.levelLength)) 
                    });
                }
        ));
      
        this.buttonList.addSingleOptionEntry(
            new DoubleOption(
                "createWorld.customize.indev.heightSlider", 
                64D, 320D, 64F,
                (gameOptions) -> { return (double) this.levelHeight; }, // Getter
                (gameOptions, value) -> { // Setter
                    this.levelHeight = value.intValue();
                    this.chunkProviderSettings.putInt("levelHeight", this.levelHeight);
                },
                (gameOptions, doubleOptions) -> {
                    int seaLevel = this.levelHeight / 2;
                    String heightString = String.valueOf(this.levelHeight) + " (Sea Level: " + String.valueOf(seaLevel) + ")";
                    
                    return new TranslatableText(
                        "options.generic_value", 
                        new Object[] { 
                            new TranslatableText("createWorld.customize.indev.levelHeight"), 
                            Text.of(heightString) 
                    });
                }
        ));
        
        this.buttonList.addSingleOptionEntry(
            new DoubleOption(
                "createWorld.customize.indev.caveRadiusSlider", 
                1D, 3D, 0.1f,
                (gameOptions) -> { return (double) this.caveRadius; }, // Getter
                (gameOptions, value) -> { // Setter
                    this.caveRadius = value.floatValue();
                    this.chunkProviderSettings.putFloat("caveRadius", this.caveRadius);
                },
                (gameOptions, doubleOptions) -> {
                    return new TranslatableText(
                        "options.generic_value",  
                        new Object[] { 
                            new TranslatableText("createWorld.customize.indev.caveRadius"), 
                            Text.of(String.format("%.01f", this.caveRadius)) 
                    });
                }
        ));
        
        this.buttonList.addSingleOptionEntry(new TextOption("Note: Settings are not final and may change."));
    }

    @Override
    protected void updateButtonActive(ScreenButtonOption option) {
        option.setButtonActive(BiomeProviderType.SINGLE);
    }
}