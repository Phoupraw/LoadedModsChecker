package phoupraw.mcmod.voxelcake.constant;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.block.entity.ManualBasinBlockEntity;
import phoupraw.mcmod.voxelcake.block.entity.VoxelCakeBlockEntity;

@ApiStatus.NonExtendable
public interface VCBlockEntityTypes {
    private static <T extends BlockEntity> BlockEntityType<T> of(Identifier id, BlockEntityType.BlockEntityFactory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, BlockEntityType.Builder.create(factory, blocks).build());
    }    BlockEntityType<VoxelCakeBlockEntity> VOXEL_CAKE = of(VCIDs.VOXEL_CAKE, VoxelCakeBlockEntity::new, VCBlocks.VOXEL_CAKE);
    BlockEntityType<ManualBasinBlockEntity> MANUAL_BASIN = of(VCIDs.MANUAL_BASIN, ManualBasinBlockEntity::new, VCBlocks.MANUAL_BASIN);

    
    //BlockEntityType<CupboardBlockEntity> CUPBOARD = of(UIDs.CUPBOARD, CupboardBlockEntity::new, UBlocks.CUPBOARD, UBlocks.CUPBOARD_CLOSET, UBlocks.CUPBOARD_TABLE);
    
}
