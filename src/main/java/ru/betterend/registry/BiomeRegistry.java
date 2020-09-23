package ru.betterend.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import ru.betterend.world.biome.BiomeFoggyMushroomland;
import ru.betterend.world.biome.EndBiome;
import ru.betterend.world.generator.BiomePicker;
import ru.betterend.world.generator.BiomeType;

public class BiomeRegistry {
	private static final Map<EndBiome, RegistryKey<Biome>> KEYS = Maps.newHashMap();
	private static final HashMap<Biome, EndBiome> MUTABLE = Maps.newHashMap();
	public static final BiomePicker LAND_BIOMES = new BiomePicker();
	public static final BiomePicker VOID_BIOMES = new BiomePicker();
	
	public static final EndBiome END = registerBiome(BiomeKeys.THE_END, BiomeType.LAND, true);
	public static final EndBiome END_BARRENS = registerBiome(BiomeKeys.END_BARRENS, BiomeType.VOID, true);
	public static final EndBiome END_HIGHLANDS = registerBiome(BiomeKeys.END_HIGHLANDS, BiomeType.LAND, false);
	public static final EndBiome END_MIDLANDS = registerBiome(BiomeKeys.END_MIDLANDS, BiomeType.LAND, false);
	public static final EndBiome SMALL_END_ISLANDS = registerBiome(BiomeKeys.SMALL_END_ISLANDS, BiomeType.VOID, true);
	public static final EndBiome FOGGY_MUSHROOMLAND = registerBiome(new BiomeFoggyMushroomland(), BiomeType.LAND);
	
	public static void register() {}
	
	public static void mutateRegistry(Registry<Biome> biomeRegistry) {
		BiomeRegistry.MUTABLE.clear();
		for (EndBiome biome : BiomeRegistry.LAND_BIOMES.getBiomes())
			BiomeRegistry.MUTABLE.put(biomeRegistry.getOrThrow(BiomeRegistry.getBiomeKey(biome)), biome);
		for (EndBiome biome : BiomeRegistry.VOID_BIOMES.getBiomes())
			BiomeRegistry.MUTABLE.put(biomeRegistry.getOrThrow(BiomeRegistry.getBiomeKey(biome)), biome);
	}
	
	public static EndBiome registerBiome(RegistryKey<Biome> key, BiomeType type, boolean addToGen) {
		EndBiome endBiome = new EndBiome(BuiltinRegistries.BIOME.get(key));
		if (addToGen) addToPicker(endBiome, type);
		makeLink(endBiome);
		return endBiome;
	}
	
	public static EndBiome registerBiome(Biome biome, BiomeType type) {
		EndBiome endBiome = new EndBiome(biome);
		addToPicker(endBiome, type);
		makeLink(endBiome);
		return endBiome;
	}
	
	public static EndBiome registerBiome(EndBiome biome, BiomeType type) {
		registerBiomeDirect(biome);
		addToPicker(biome, type);
		return biome;
	}
	
	private static void addToPicker(EndBiome biome, BiomeType type) {
		if (type == BiomeType.LAND)
			LAND_BIOMES.addBiome(biome);
		else
			VOID_BIOMES.addBiome(biome);
	}

	private static void registerBiomeDirect(EndBiome biome) {
		Registry.register(BuiltinRegistries.BIOME, biome.getID(), biome.getBiome());
		makeLink(biome);
	}
	
	private static void makeLink(EndBiome biome) {
		Optional<RegistryKey<Biome>> optional = BuiltinRegistries.BIOME.getKey(biome.getBiome());
		RegistryKey<Biome> key = optional.isPresent() ? optional.get() : RegistryKey.of(Registry.BIOME_KEY, biome.getID());
		KEYS.put(biome, key);
	}

	public static RegistryKey<Biome> getBiomeKey(EndBiome biome) {
		return KEYS.get(biome);
	}
	
	public static EndBiome getFromBiome(Biome biome) {
		return MUTABLE.getOrDefault(biome, END);
	}
}