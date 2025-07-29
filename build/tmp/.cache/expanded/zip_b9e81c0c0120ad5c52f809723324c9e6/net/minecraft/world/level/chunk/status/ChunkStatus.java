package net.minecraft.world.level.chunk.status;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class ChunkStatus {
    public static final int MAX_STRUCTURE_DISTANCE = 8;
    private static final EnumSet<Heightmap.Types> PRE_FEATURES = EnumSet.of(Heightmap.Types.OCEAN_FLOOR_WG, Heightmap.Types.WORLD_SURFACE_WG);
    public static final EnumSet<Heightmap.Types> POST_FEATURES = EnumSet.of(
        Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.WORLD_SURFACE, Heightmap.Types.MOTION_BLOCKING, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES
    );
    public static final ChunkStatus EMPTY = register(
        "empty", null, -1, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateEmpty, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus STRUCTURE_STARTS = register(
        "structure_starts", EMPTY, 0, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateStructureStarts, ChunkStatusTasks::loadStructureStarts
    );
    public static final ChunkStatus STRUCTURE_REFERENCES = register(
        "structure_references", STRUCTURE_STARTS, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateStructureReferences, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus BIOMES = register(
        "biomes", STRUCTURE_REFERENCES, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateBiomes, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus NOISE = register(
        "noise", BIOMES, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateNoise, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus SURFACE = register(
        "surface", NOISE, 8, false, PRE_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateSurface, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus CARVERS = register(
        "carvers", SURFACE, 8, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateCarvers, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus FEATURES = register(
        "features", CARVERS, 8, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateFeatures, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus INITIALIZE_LIGHT = register(
        "initialize_light", FEATURES, 0, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateInitializeLight, ChunkStatusTasks::loadInitializeLight
    );
    public static final ChunkStatus LIGHT = register(
        "light", INITIALIZE_LIGHT, 1, true, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateLight, ChunkStatusTasks::loadLight
    );
    public static final ChunkStatus SPAWN = register(
        "spawn", LIGHT, 1, false, POST_FEATURES, ChunkType.PROTOCHUNK, ChunkStatusTasks::generateSpawn, ChunkStatusTasks::loadPassThrough
    );
    public static final ChunkStatus FULL = register(
        "full", SPAWN, 0, false, POST_FEATURES, ChunkType.LEVELCHUNK, ChunkStatusTasks::generateFull, ChunkStatusTasks::loadFull
    );
    private static final List<ChunkStatus> STATUS_BY_RANGE = ImmutableList.of(
        FULL, INITIALIZE_LIGHT, CARVERS, BIOMES, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS, STRUCTURE_STARTS
    );
    private static final IntList RANGE_BY_STATUS = Util.make(new IntArrayList(getStatusList().size()), p_335012_ -> {
        int i = 0;

        for (int j = getStatusList().size() - 1; j >= 0; j--) {
            while (i + 1 < STATUS_BY_RANGE.size() && j <= STATUS_BY_RANGE.get(i + 1).getIndex()) {
                i++;
            }

            p_335012_.add(0, i);
        }
    });
    private final int index;
    private final ChunkStatus parent;
    private final ChunkStatus.GenerationTask generationTask;
    private final ChunkStatus.LoadingTask loadingTask;
    private final int range;
    private final boolean hasLoadDependencies;
    private final ChunkType chunkType;
    private final EnumSet<Heightmap.Types> heightmapsAfter;

    private static ChunkStatus register(
        String p_334704_,
        @Nullable ChunkStatus p_335238_,
        int p_331152_,
        boolean p_332303_,
        EnumSet<Heightmap.Types> p_335194_,
        ChunkType p_333808_,
        ChunkStatus.GenerationTask p_328792_,
        ChunkStatus.LoadingTask p_335536_
    ) {
        return Registry.register(
            BuiltInRegistries.CHUNK_STATUS, p_334704_, new ChunkStatus(p_335238_, p_331152_, p_332303_, p_335194_, p_333808_, p_328792_, p_335536_)
        );
    }

    public static List<ChunkStatus> getStatusList() {
        List<ChunkStatus> list = Lists.newArrayList();

        ChunkStatus chunkstatus;
        for (chunkstatus = FULL; chunkstatus.getParent() != chunkstatus; chunkstatus = chunkstatus.getParent()) {
            list.add(chunkstatus);
        }

        list.add(chunkstatus);
        Collections.reverse(list);
        return list;
    }

    public static ChunkStatus getStatusAroundFullChunk(int p_334095_) {
        if (p_334095_ >= STATUS_BY_RANGE.size()) {
            return EMPTY;
        } else {
            return p_334095_ < 0 ? FULL : STATUS_BY_RANGE.get(p_334095_);
        }
    }

    public static int maxDistance() {
        return STATUS_BY_RANGE.size();
    }

    public static int getDistance(ChunkStatus p_331292_) {
        return RANGE_BY_STATUS.getInt(p_331292_.getIndex());
    }

    public ChunkStatus(
        @Nullable ChunkStatus p_334696_,
        int p_328357_,
        boolean p_329678_,
        EnumSet<Heightmap.Types> p_329876_,
        ChunkType p_336141_,
        ChunkStatus.GenerationTask p_328064_,
        ChunkStatus.LoadingTask p_333773_
    ) {
        this.parent = p_334696_ == null ? this : p_334696_;
        this.generationTask = p_328064_;
        this.loadingTask = p_333773_;
        this.range = p_328357_;
        this.hasLoadDependencies = p_329678_;
        this.chunkType = p_336141_;
        this.heightmapsAfter = p_329876_;
        this.index = p_334696_ == null ? 0 : p_334696_.getIndex() + 1;
    }

    public int getIndex() {
        return this.index;
    }

    public ChunkStatus getParent() {
        return this.parent;
    }

    public CompletableFuture<ChunkAccess> generate(WorldGenContext p_333542_, Executor p_332959_, ToFullChunk p_332442_, List<ChunkAccess> p_328194_) {
        ChunkAccess chunkaccess = p_328194_.get(p_328194_.size() / 2);
        ProfiledDuration profiledduration = JvmProfiler.INSTANCE.onChunkGenerate(chunkaccess.getPos(), p_333542_.level().dimension(), this.toString());
        return this.generationTask.doWork(p_333542_, this, p_332959_, p_332442_, p_328194_, chunkaccess).thenApply(p_330327_ -> {
            if (p_330327_ instanceof ProtoChunk protochunk && !protochunk.getStatus().isOrAfter(this)) {
                protochunk.setStatus(this);
            }

            if (profiledduration != null) {
                profiledduration.finish();
            }

            return (ChunkAccess)p_330327_;
        });
    }

    public CompletableFuture<ChunkAccess> load(WorldGenContext p_336003_, ToFullChunk p_329647_, ChunkAccess p_335394_) {
        return this.loadingTask.doWork(p_336003_, this, p_329647_, p_335394_);
    }

    public int getRange() {
        return this.range;
    }

    public boolean hasLoadDependencies() {
        return this.hasLoadDependencies;
    }

    public ChunkType getChunkType() {
        return this.chunkType;
    }

    public static ChunkStatus byName(String p_329723_) {
        return BuiltInRegistries.CHUNK_STATUS.get(ResourceLocation.tryParse(p_329723_));
    }

    public EnumSet<Heightmap.Types> heightmapsAfter() {
        return this.heightmapsAfter;
    }

    public boolean isOrAfter(ChunkStatus p_334516_) {
        return this.getIndex() >= p_334516_.getIndex();
    }

    @Override
    public String toString() {
        return BuiltInRegistries.CHUNK_STATUS.getKey(this).toString();
    }

    @FunctionalInterface
    protected interface GenerationTask {
        CompletableFuture<ChunkAccess> doWork(
            WorldGenContext p_335125_, ChunkStatus p_330585_, Executor p_330238_, ToFullChunk p_330981_, List<ChunkAccess> p_331880_, ChunkAccess p_336215_
        );
    }

    @FunctionalInterface
    protected interface LoadingTask {
        CompletableFuture<ChunkAccess> doWork(WorldGenContext p_330561_, ChunkStatus p_332376_, ToFullChunk p_330749_, ChunkAccess p_332821_);
    }
}