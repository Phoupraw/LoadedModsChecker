package phoupraw.mcmod.voxelcake.constant;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.voxelcake.fluid.JamFluid;

/**
 <ul>
 <li>{@link phoupraw.mcmod.voxelcake.datagen.ChineseGen}</li>
 </ul>
 */
@ApiStatus.NonExtendable
public interface VCFluids {
    FlowableFluid CREAM = r(VCIDs.CREAM,new JamFluid(VCItems.BUCKETED_CREAM,VCBlocks.CREAM));
    FlowableFluid SWEET_BERRY_JAM = r(VCIDs.SWEET_BERRY_JAM,new JamFluid(VCItems.BUCKETED_SWEET_BERRY_JAM,VCBlocks.SWEET_BERRY_JAM));
    private static <T extends Fluid> T r(Identifier id,T fluid) {
        return Registry.register(Registries.FLUID,id,fluid);
    }
}
