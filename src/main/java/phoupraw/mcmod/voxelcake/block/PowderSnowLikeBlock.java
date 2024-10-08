package phoupraw.mcmod.voxelcake.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PowderSnowLikeBlock extends MyBlock implements FluidDrainable {
    /**
     @see PowderSnowBlock#HORIZONTAL_MOVEMENT_MULTIPLIER
     */
    public static final double HORIZONTAL_MOVEMENT_MULTIPLIER = 0.9;
    /**
     @see PowderSnowBlock#VERTICAL_MOVEMENT_MULTIPLIER
     */
    public static final double VERTICAL_MOVEMENT_MULTIPLIER = 1.5;
    /**
     @see PowderSnowBlock#field_31219
     */
    public static final float FALLING_DISTANCE = 2.5f;
    /**
     @see PowderSnowBlock#field_36189
     */
    public static final float LANDED_DISTANCE = 4;
    /**
     @see PowderSnowBlock#SMALL_FALL_SOUND_MAX_DISTANCE
     */
    public static final float SMALL_FALL_SOUND_MAX_DISTANCE = 7;
    /**
     @see PowderSnowBlock#FALLING_SHAPE
     */
    public static final VoxelShape FALLING_SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, HORIZONTAL_MOVEMENT_MULTIPLIER, 1);
    public static PowderSnowLikeBlock of(Settings settings) {
        return new PowderSnowLikeBlock(settings.strength(1).dropsNothing().sounds(BlockSoundGroup.POWDER_SNOW).dynamicBounds().solidBlock(Blocks::never));
    }
    public PowderSnowLikeBlock(Settings settings) {
        super(settings);
    }
    /**
     @see PowderSnowBlock#onLandedUpon
     */
    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!(fallDistance < LANDED_DISTANCE) && entity instanceof LivingEntity living) {
            LivingEntity.FallSounds fallSounds = living.getFallSounds();
            SoundEvent soundEvent = fallDistance < SMALL_FALL_SOUND_MAX_DISTANCE ? fallSounds.small() : fallSounds.big();
            entity.playSound(soundEvent, 1, 1);
        }
    }
    /**
     @see PowderSnowBlock#isSideInvisible
     */
    @Override
    protected boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return super.isSideInvisible(state, stateFrom, direction);
    }
    /**
     @see PowderSnowBlock#getCullingShape
     */
    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }
    /**
     @see PowderSnowBlock#getCollisionShape
     */
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityShapeContext) {
            Entity entity = entityShapeContext.getEntity();
            if (entity != null) {
                if (entity.fallDistance > FALLING_DISTANCE) {
                    return FALLING_SHAPE;
                }
                if (entity instanceof FallingBlockEntity) {
                    return super.getCollisionShape(state, world, pos, context);
                }
            }
        }
        return VoxelShapes.empty();
    }
    /**
     @see PowderSnowBlock#getCameraCollisionShape
     */
    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }
    /**
     @see PowderSnowBlock#onEntityCollision
     */
    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity.getBlockStateAtPos().isOf(this)) {
            entity.slowMovement(state, new Vec3d(HORIZONTAL_MOVEMENT_MULTIPLIER, VERTICAL_MOVEMENT_MULTIPLIER, HORIZONTAL_MOVEMENT_MULTIPLIER));
        }
    }
    @Override
    public ItemStack tryDrainFluid(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
        if (!world.isClient()) {
            world.syncWorldEvent(WorldEvents.BLOCK_BROKEN, pos, Block.getRawIdFromState(state));
        }
        return asItem().getDefaultStack();
    }
    @Override
    public Optional<SoundEvent> getBucketFillSound() {
        return Optional.of(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW);
    }
    //@Override
    //protected FluidState getFluidState(BlockState state) {
    //    return JamFluid.MAP.getOrDefault(this, Fluids.EMPTY).getDefaultState();
    //}
}
