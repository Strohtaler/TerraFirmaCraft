/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.util.Lazy;

/**
 * Places gravel under subsurface material
 * Not extending config due to concerns with how possible that is in 1.16. This is our only use case for that, it works fine
 */
public class DeepSurfaceBuilder extends SurfaceBuilder<SurfaceBuilderConfig>
{
    private static final BlockState AIR = Blocks.AIR.defaultBlockState();
    private static final BlockState GRAVEL = Blocks.GRAVEL.defaultBlockState();

    public DeepSurfaceBuilder()
    {
        super(SurfaceBuilderConfig::deserialize);
    }

    @Override
    public void apply(Random random, IChunk chunkIn, Biome biomeIn, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, SurfaceBuilderConfig config)
    {
        // Lazy because this queries a noise layer
        Lazy<SurfaceBuilderConfig> underWaterConfig = Lazy.of(() -> TFCSurfaceBuilders.UNDERWATER.get().getUnderwaterConfig(x, z, seed));

        BlockState topState;
        BlockState underState = config.getUnderMaterial();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int surfaceDepth = -1;
        int maxSurfaceDepth = (int) (noise / 3.0D + 5.0D + random.nextDouble() * 0.25D);
        int localX = x & 15;
        int localZ = z & 15;
        boolean subsurface = false;

        for (int y = startHeight; y >= 0; --y)
        {
            pos.set(localX, y, localZ);
            BlockState stateAt = chunkIn.getBlockState(pos);
            if (stateAt.isAir(chunkIn, pos))
            {
                // Reached air, reset surface depth
                surfaceDepth = -1;
                subsurface = false;
            }
            else if (stateAt.getBlock() == defaultBlock.getBlock())
            {
                if (surfaceDepth == -1)
                {
                    // Reached surface. Place top state and switch to subsurface layers
                    surfaceDepth = maxSurfaceDepth;
                    subsurface = false;
                    if (maxSurfaceDepth <= 0)
                    {
                        topState = AIR;
                        underState = defaultBlock;
                    }
                    else if (y >= seaLevel)
                    {
                        topState = config.getTopMaterial();
                        underState = config.getUnderMaterial();
                    }
                    else
                    {
                        topState = underState = underWaterConfig.get().getUnderwaterMaterial();
                    }

                    chunkIn.setBlockState(pos, topState, false);
                }
                else if (surfaceDepth > 0)
                {
                    // Subsurface layers
                    surfaceDepth--;
                    chunkIn.setBlockState(pos, underState, false);
                    if (surfaceDepth == 0 && !subsurface && maxSurfaceDepth > 1)
                    {
                        subsurface = true;
                        surfaceDepth = maxSurfaceDepth + random.nextInt(3) - random.nextInt(3);
                        underState = GRAVEL;
                    }
                }
            }
        }
    }
}