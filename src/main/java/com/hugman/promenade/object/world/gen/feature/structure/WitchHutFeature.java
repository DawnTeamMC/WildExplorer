package com.hugman.promenade.object.world.gen.feature.structure;

import com.hugman.promenade.object.world.gen.feature.structure.generator.WitchHutGenerator;
import com.mojang.serialization.Codec;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WitchHutFeature extends StructureFeature<DefaultFeatureConfig> {
	public WitchHutFeature(Codec<DefaultFeatureConfig> configCodec) {
		super(configCodec);
	}

	@Override
	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(DynamicRegistryManager registryManager, ChunkGenerator chunkGenerator, StructureManager manager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig config, HeightLimitView world) {
			int x = chunkPos.getStartX();
			int z = chunkPos.getStartZ();
			int y = chunkGenerator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG, world);
			BlockPos pos = new BlockPos(x, y, z).down();
			BlockRotation blockRotation = BlockRotation.random(this.random);
			BlockMirror blockMirror = this.random.nextFloat() < 0.5F ? BlockMirror.NONE : BlockMirror.FRONT_BACK;
			WitchHutGenerator.addPieces(manager, pos, blockRotation, blockMirror, this);
		}
	}
}
