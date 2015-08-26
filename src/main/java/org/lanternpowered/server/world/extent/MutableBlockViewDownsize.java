package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

public class MutableBlockViewDownsize extends AbstractBlockViewDownsize<MutableBlockVolume> implements MutableBlockVolume {

    public MutableBlockViewDownsize(MutableBlockVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        this.setBlockType(position.getX(), position.getY(), position.getZ(), type);
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        this.setBlock(x, y, z, type.getDefaultState());
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        this.setBlock(position.getX(), position.getY(), position.getZ(), block);
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        this.checkRange(x, y, z);
        this.volume.setBlock(x, y, z, block);
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        this.checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new MutableBlockViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }

    @Override
    public MutableBlockVolume getRelativeBlockView() {
        return this.getBlockView(DiscreteTransform3.fromTranslation(this.min.negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }
}