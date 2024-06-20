package net.shadowbeast.arcanemysteries.util.Compass;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.shadowbeast.arcanemysteries.ArcaneMysteries;

import java.util.ArrayList;
import java.util.List;

public class CompassStructureUtil {
    public static List<ResourceLocation> getAvailableStructureList(Level level) {
        List<ResourceLocation> structureList = new ArrayList<>();
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        registry.keySet().forEach(location -> {
            if (!isBlacklisted(location) && !structureList.contains(location)) {
                structureList.add(location);
            }
        });

        return structureList;
    }

    public static boolean isBlacklisted(ResourceLocation structureLocation) {
        if (structureLocation == null) {
            ArcaneMysteries.LOGGER.error("Checking blacklist but fed location is null!");
            return false;
        }
        if (!CompassConfig.COMMON.structureBlacklist.get().isEmpty()) {
            if (CompassConfig.COMMON.structureBlacklist.get().contains(structureLocation.toString())) {
                return true;
            }
            List<? extends String> wildcardList = CompassConfig.COMMON.structureBlacklist.get().stream()
                    .filter(value -> value.contains(":") && value.contains("*")).toList();
            for (String wildcard : wildcardList) {
                String[] blacklistSplit = wildcard.split(":");
                if ((blacklistSplit[0].equals("*") && structureLocation.getPath().equals(blacklistSplit[1])) ||
                        blacklistSplit[1].equals("*") && structureLocation.getNamespace().equals(blacklistSplit[0])
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Pair<BlockPos, Holder<Structure>> findNearestMapStructure(ServerLevel serverLevel,
                                                                            HolderSet<Structure> structureHolderSet, BlockPos pos, int range, boolean findUnexplored) {
        ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
        Pair<BlockPos, Holder<Structure>> nearest = generator.findNearestMapStructure(serverLevel, structureHolderSet, pos, range, findUnexplored);
        if (nearest == null) return null;
        return nearest.getFirst().distManhattan(pos) <= CompassConfig.COMMON.compassRange.get() ? nearest : null;
    }
}
