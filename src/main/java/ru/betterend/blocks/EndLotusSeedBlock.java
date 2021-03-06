package ru.betterend.blocks;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import ru.betterend.blocks.BlockProperties.TripleShape;
import ru.betterend.blocks.basis.UnderwaterPlantWithAgeBlock;
import ru.betterend.registry.EndBlocks;
import ru.betterend.util.BlocksHelper;

public class EndLotusSeedBlock extends UnderwaterPlantWithAgeBlock {
	@Override
	public void grow(StructureWorldAccess world, Random random, BlockPos pos) {
		if (canGrow(world, pos)) {
			BlockState startLeaf = EndBlocks.END_LOTUS_STEM.getDefaultState().with(EndLotusStemBlock.LEAF, true);
			BlockState roots = EndBlocks.END_LOTUS_STEM.getDefaultState().with(EndLotusStemBlock.SHAPE, TripleShape.BOTTOM).with(EndLotusStemBlock.WATERLOGGED, true);
			BlockState stem = EndBlocks.END_LOTUS_STEM.getDefaultState();
			BlockState flower = EndBlocks.END_LOTUS_FLOWER.getDefaultState();
			
			BlocksHelper.setWithoutUpdate(world, pos, roots);
			Mutable bpos = new Mutable().set(pos);
			bpos.setY(bpos.getY() + 1);
			while (world.getFluidState(bpos).isStill()) {
				BlocksHelper.setWithoutUpdate(world, bpos, stem.with(EndLotusStemBlock.WATERLOGGED, true));
				bpos.setY(bpos.getY() + 1);
			}
			
			int height = random.nextBoolean() ? 0 : random.nextBoolean() ? 1 : random.nextBoolean() ? 1 : -1;
			TripleShape shape = (height == 0) ? TripleShape.TOP : TripleShape.MIDDLE;
			Direction dir = BlocksHelper.randomHorizontal(random);
			BlockPos leafCenter = bpos.toImmutable().offset(dir);
			if (hasLeaf(world, leafCenter)) {
				generateLeaf(world, leafCenter);
				BlocksHelper.setWithoutUpdate(world, bpos, startLeaf.with(EndLotusStemBlock.SHAPE, shape).with(EndLotusStemBlock.FACING, dir));
			}
			else {
				BlocksHelper.setWithoutUpdate(world, bpos, stem.with(EndLotusStemBlock.SHAPE, shape));
			}
			
			bpos.setY(bpos.getY() + 1);
			for (int i = 1; i <= height; i++) {
				if (!world.isAir(bpos)) {
					bpos.setY(bpos.getY() - 1);
					BlocksHelper.setWithoutUpdate(world, bpos, flower);
					bpos.setY(bpos.getY() - 1);
					stem = world.getBlockState(bpos);
					BlocksHelper.setWithoutUpdate(world, bpos, stem.with(EndLotusStemBlock.SHAPE, TripleShape.TOP));
					return;
				}
				BlocksHelper.setWithoutUpdate(world, bpos, stem);
				bpos.setY(bpos.getY() + 1);
			}
			
			if (!world.isAir(bpos) || height < 0) {
				bpos.setY(bpos.getY() - 1);
			}
			
			BlocksHelper.setWithoutUpdate(world, bpos, flower);
			bpos.setY(bpos.getY() - 1);
			stem = world.getBlockState(bpos);
			if (!stem.isOf(EndBlocks.END_LOTUS_STEM)) {
				stem = EndBlocks.END_LOTUS_STEM.getDefaultState();
				if (!world.getBlockState(bpos.north()).getFluidState().isEmpty()) {
					stem = stem.with(EndLotusStemBlock.WATERLOGGED, true);
				}
			}
			
			if (world.getBlockState(bpos.offset(dir)).isOf(EndBlocks.END_LOTUS_LEAF)) {
				stem = stem.with(EndLotusStemBlock.LEAF, true).with(EndLotusStemBlock.FACING, dir);
			}
			
			BlocksHelper.setWithoutUpdate(world, bpos, stem.with(EndLotusStemBlock.SHAPE, TripleShape.TOP));
		}
	}
	
	private boolean canGrow(StructureWorldAccess world, BlockPos pos) {
		Mutable bpos = new Mutable();
		bpos.set(pos);
		while (world.getBlockState(bpos).getFluidState().getFluid().equals(Fluids.WATER.getStill())) {
			bpos.setY(bpos.getY() + 1);
		}
		return world.isAir(bpos) && world.isAir(bpos.up());
	}
	
	private void generateLeaf(StructureWorldAccess world, BlockPos pos) {
		Mutable p = new Mutable();
		BlockState leaf = EndBlocks.END_LOTUS_LEAF.getDefaultState();
		BlocksHelper.setWithoutUpdate(world, pos, leaf.with(EndLotusLeafBlock.SHAPE, TripleShape.BOTTOM));
		for (Direction move: BlocksHelper.HORIZONTAL) {
			BlocksHelper.setWithoutUpdate(world, p.set(pos).move(move), leaf.with(EndLotusLeafBlock.HORIZONTAL_FACING, move).with(EndLotusLeafBlock.SHAPE, TripleShape.MIDDLE));
		}
		for (int i = 0; i < 4; i ++) {
			Direction d1 = BlocksHelper.HORIZONTAL[i];
			Direction d2 = BlocksHelper.HORIZONTAL[(i + 1) & 3];
			BlocksHelper.setWithoutUpdate(world, p.set(pos).move(d1).move(d2), leaf.with(EndLotusLeafBlock.HORIZONTAL_FACING, d1).with(EndLotusLeafBlock.SHAPE, TripleShape.TOP));
		}
	}
	
	private boolean hasLeaf(StructureWorldAccess world, BlockPos pos) {
		Mutable p = new Mutable();
		p.setY(pos.getY());
		int count = 0;
		for (int x = -1; x < 2; x ++) {
			p.setX(pos.getX() + x);
			for (int z = -1; z < 2; z ++) {
				p.setZ(pos.getZ() + z);
				if (world.isAir(p) && !world.getFluidState(p.down()).isEmpty())
					count ++;
			}
		}
		return count == 9;
	}
}
