package phoupraw.mcmod.voxelcake.block;

import net.minecraft.block.Block;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;

public class MyBlock extends Block {
    public static final double EPSILON = 1 / 256.0;
    public static Box boxOf(double x, double y, double z, double a, double b, double c) {
        if (a < EPSILON) {
            x -= EPSILON;
            a = EPSILON * 2;
        }
        if (b < EPSILON) {
            y -= EPSILON;
            b = EPSILON * 2;
        }
        if (c < EPSILON) {
            z -= EPSILON;
            c = EPSILON * 2;
        }
        return new Box(x, y, z, (x + a), (y + b), (z + c));
    }
    public static VoxelShape of(double x, double y, double z, double a, double b, double c) {
        if (a < EPSILON) {
            x -= EPSILON;
            a = EPSILON * 2;
        }
        if (b < EPSILON) {
            y -= EPSILON;
            b = EPSILON * 2;
        }
        if (c < EPSILON) {
            z -= EPSILON;
            c = EPSILON * 2;
        }
        return createCuboidShape(x, y, z, x + a, y + b, z + c);
    }
    public MyBlock(Settings settings) {
        super(settings);
    }
}
