package net.minecraft.server.level;

import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.lighting.LightEngineGraph;

public abstract class ChunkMap extends LightEngineGraph {

    protected ChunkMap(int i, int j, int k) {
        super(i, j, k);
    }

    @Override
    protected boolean isSource(long i) {
        return i == ChunkCoordIntPair.INVALID_CHUNK_POS;
    }

    @Override
    protected void checkNeighborsAfterUpdate(long i, int j, boolean flag) {
        if (!flag || j < this.levelCount - 2) {
            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);
            int k = chunkcoordintpair.x;
            int l = chunkcoordintpair.z;

            for (int i1 = -1; i1 <= 1; ++i1) {
                for (int j1 = -1; j1 <= 1; ++j1) {
                    long k1 = ChunkCoordIntPair.asLong(k + i1, l + j1);

                    if (k1 != i) {
                        this.checkNeighbor(i, k1, j, flag);
                    }
                }
            }

        }
    }

    @Override
    protected int getComputedLevel(long i, long j, int k) {
        int l = k;
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i);
        int i1 = chunkcoordintpair.x;
        int j1 = chunkcoordintpair.z;

        for (int k1 = -1; k1 <= 1; ++k1) {
            for (int l1 = -1; l1 <= 1; ++l1) {
                long i2 = ChunkCoordIntPair.asLong(i1 + k1, j1 + l1);

                if (i2 == i) {
                    i2 = ChunkCoordIntPair.INVALID_CHUNK_POS;
                }

                if (i2 != j) {
                    int j2 = this.computeLevelFromNeighbor(i2, i, this.getLevel(i2));

                    if (l > j2) {
                        l = j2;
                    }

                    if (l == 0) {
                        return l;
                    }
                }
            }
        }

        return l;
    }

    @Override
    protected int computeLevelFromNeighbor(long i, long j, int k) {
        return i == ChunkCoordIntPair.INVALID_CHUNK_POS ? this.getLevelFromSource(j) : k + 1;
    }

    protected abstract int getLevelFromSource(long i);

    public void update(long i, int j, boolean flag) {
        this.checkEdge(ChunkCoordIntPair.INVALID_CHUNK_POS, i, j, flag);
    }
}
