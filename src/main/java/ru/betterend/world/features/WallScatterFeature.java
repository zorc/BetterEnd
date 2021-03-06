package ru.betterend.world.features;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import ru.betterend.util.BlocksHelper;
import ru.betterend.util.MHelper;

public abstract class WallScatterFeature extends DefaultFeature {
	private static final Direction[] DIR = BlocksHelper.makeHorizontal();
	private final int radius;
	
	public WallScatterFeature(int radius) {
		this.radius = radius;
	}
	
	public abstract boolean canGenerate(StructureWorldAccess world, Random random, BlockPos pos, Direction dir);
	
	public abstract void generate(StructureWorldAccess world, Random random, BlockPos pos, Direction dir);
	
	@Override
	public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos center, DefaultFeatureConfig featureConfig) {
		int maxY = world.getTopY(Heightmap.Type.WORLD_SURFACE, center.getX(), center.getZ());
		int minY = BlocksHelper.upRay(world, new BlockPos(center.getX(), 0, center.getZ()), maxY);
		if (maxY < 10 || maxY < minY) {
			return false;
		}
		int py = MHelper.randRange(minY, maxY, random);
		
		Mutable mut = new Mutable();
		for (int x = -radius; x <= radius; x++) {
			mut.setX(center.getX() + x);
			for (int y = -radius; y <= radius; y++) {
				mut.setY(py + y);
				for (int z = -radius; z <= radius; z++) {
					mut.setZ(center.getZ() + z);
					if (random.nextInt(4) == 0 && world.isAir(mut)) {
						shuffle(random);
						for (Direction dir: DIR) {
							if (canGenerate(world, random, mut, dir)) {
								generate(world, random, mut, dir);
								break;
							}
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private void shuffle(Random random) {
		for (int i = 0; i < 4; i++) {
			int j = random.nextInt(4);
			Direction d = DIR[i];
			DIR[i] = DIR[j];
			DIR[j] = d;
		}
	}
}
