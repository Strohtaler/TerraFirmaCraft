package net.dries007.tfc.mixin.world.gen.settings;

import java.util.Properties;

import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;

import net.dries007.tfc.world.TFCChunkGenerator;
import net.dries007.tfc.world.biome.TFCBiomeProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionGeneratorSettings.class)
public class DimensionGeneratorSettingsMixin
{
    /**
     * This is done in order to catch the tfc world type upon parsing of the server properties
     * We do this so we can add the tfc preset to servers
     * Also ignores the bonus chest setting and sets it to false
     */
    @Inject(method = "create", at = @At(value = "RETURN"), cancellable = true)
    private static void inject$create(DynamicRegistries registries, Properties properties, CallbackInfoReturnable<DimensionGeneratorSettings> cir)
    {
        Object levelType = properties.get("level-type");
        if (levelType != null && "tfc".equalsIgnoreCase(levelType.toString()))
        {
            DimensionGeneratorSettings old = cir.getReturnValue();
            TFCBiomeProvider biomeProvider = new TFCBiomeProvider(old.seed(), new TFCBiomeProvider.LayerSettings(), registries.registryOrThrow(Registry.BIOME_REGISTRY));
            ChunkGenerator chunkGenerator = new TFCChunkGenerator(biomeProvider, () -> registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY).get(DimensionSettings.OVERWORLD), false, old.seed());
            cir.setReturnValue(new DimensionGeneratorSettings(old.seed(), old.generateFeatures(), false, DimensionGeneratorSettings.withOverworld(registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY), old.dimensions(), chunkGenerator)));
        }
    }
}