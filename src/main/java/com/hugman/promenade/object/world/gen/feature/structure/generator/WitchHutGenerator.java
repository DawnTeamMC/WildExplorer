package com.hugman.promenade.object.world.gen.feature.structure.generator;

import com.google.common.collect.ImmutableList;
import com.hugman.dawn.api.util.DefaultBlockTemplates;
import com.hugman.promenade.Promenade;
import com.hugman.promenade.init.PromenadeBlocks;
import com.hugman.promenade.init.data.PromenadeLootTables;
import com.hugman.promenade.init.data.PromenadeTags;
import com.hugman.promenade.init.world.PromenadeStructurePieces;
import net.minecraft.block.Blocks;
import net.minecraft.block.LanternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RandomBlockMatchRuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;

import java.util.Random;

public class WitchHutGenerator {
	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, BlockMirror mirror, StructurePiecesHolder piecesHolder) {
		piecesHolder.addPiece(new MainPiece(manager, Promenade.MOD_DATA.id("witch_hut"), pos, rotation, mirror));
	}

	public static class MainPiece extends SimpleStructurePiece {
		public MainPiece(ServerWorld world, NbtCompound nbt) {
			super(PromenadeStructurePieces.WITCH_HUT_PIECE, nbt, world, (identifier) -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot")), BlockMirror.valueOf(nbt.getString("Mirror"))));
		}

		public MainPiece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, BlockMirror mirror) {
			super(PromenadeStructurePieces.WITCH_HUT_PIECE, 0, manager, identifier, identifier.toString(), createPlacementData(rotation, mirror), pos);
		}

		private static StructurePlacementData createPlacementData(BlockRotation blockRotation, BlockMirror blockMirror) {
			Random random = new Random();
			StructurePlacementData placementData = (new StructurePlacementData())
					.setRotation(blockRotation)
					.setMirror(blockMirror)
					.addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS)
					.addProcessor(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new RandomBlockMatchRuleTest(Blocks.COBBLESTONE, 0.4F), AlwaysTrueRuleTest.INSTANCE, Blocks.MOSSY_COBBLESTONE.getDefaultState()))))
					.addProcessor(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new TagMatchRuleTest(PromenadeTags.Blocks.POTTED_MUSHROOMS), AlwaysTrueRuleTest.INSTANCE, PromenadeTags.Blocks.POTTED_MUSHROOMS.getRandom(random).getDefaultState()))));
			if(random.nextBoolean()) {
				placementData.addProcessor(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new BlockMatchRuleTest(PromenadeBlocks.POLISHED_CARBONITE.getBlock(DefaultBlockTemplates.CUBE)), AlwaysTrueRuleTest.INSTANCE, Blocks.POLISHED_ANDESITE.getDefaultState()))));
				placementData.addProcessor(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new BlockMatchRuleTest(PromenadeBlocks.CARBONITE.getBlock(DefaultBlockTemplates.WALL)), AlwaysTrueRuleTest.INSTANCE, Blocks.ANDESITE_WALL.getDefaultState()))));
			}
			if(random.nextFloat() < 0.2f) {
				placementData.addProcessor(new RuleStructureProcessor(ImmutableList.of(new StructureProcessorRule(new BlockMatchRuleTest(Blocks.LANTERN), AlwaysTrueRuleTest.INSTANCE, Blocks.SOUL_LANTERN.getDefaultState().with(LanternBlock.HANGING, true)))));
			}
			return placementData;
		}

		@Override
		protected void writeNbt(ServerWorld world, NbtCompound nbt) {
			super.writeNbt(world, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
			nbt.putString("Mirror", this.placementData.getMirror().name());
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess serverWorldAccess, Random random, BlockBox boundingBox) {
			if(metadata.startsWith("Barrel")) {
				BlockEntity blockEntity = serverWorldAccess.getBlockEntity(pos.down());
				if(blockEntity instanceof LootableContainerBlockEntity) {
					((LootableContainerBlockEntity) blockEntity).setLootTable(PromenadeLootTables.WITCH_HUT_CHEST, random.nextLong());
				}
			}
			else if(metadata.startsWith("Witch")) {
				WitchEntity witchEntity = EntityType.WITCH.create(serverWorldAccess.toServerWorld());
				if(witchEntity != null) {
					witchEntity.setPersistent();
					witchEntity.refreshPositionAndAngles(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
					witchEntity.initialize(serverWorldAccess, serverWorldAccess.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
				}
				serverWorldAccess.spawnEntityAndPassengers(witchEntity);
			}
		}
	}
}
