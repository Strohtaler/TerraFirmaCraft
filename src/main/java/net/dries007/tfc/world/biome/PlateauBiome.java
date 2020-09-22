/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.biome;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;

import net.dries007.tfc.world.surfacebuilder.TFCSurfaceBuilders;

import net.minecraft.world.biome.Biome.Category;

public class PlateauBiome extends FlatBiome
{
    protected PlateauBiome(BiomeTemperature temperature, BiomeRainfall rainfall)
    {
        super(new Biome.Builder().biomeCategory(Category.EXTREME_HILLS), 20, 30, temperature, rainfall);

        biomeFeatures.enqueue(() -> {
            TFCDefaultBiomeFeatures.addCarvers(this);
            setSurfaceBuilder(TFCSurfaceBuilders.PLATEAU.get(), SurfaceBuilder.CONFIG_OCEAN_SAND);
        });
    }
}