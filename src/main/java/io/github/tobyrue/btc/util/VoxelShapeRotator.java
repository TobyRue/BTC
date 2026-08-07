package io.github.tobyrue.btc.util;

import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.EnumMap;
import java.util.Map;

/**
 * Rotates a VoxelShape the same way a blockstate JSON variant rotates
 * its model via the "x" then "y" transform fields.
 *
 * Pass in the shape built for the UNROTATED orientation (x=0, y=0) --
 * i.e. whatever your base model looks like with no "x"/"y" entries,
 * which in your JSON is facing=up.
 */
public final class VoxelShapeRotator {

    private VoxelShapeRotator() {}

    /**
     * @param shape    shape in its base (x=0, y=0) orientation
     * @param xDegrees rotation around the x-axis in degrees (multiple of 90), applied first
     * @param yDegrees rotation around the y-axis in degrees (multiple of 90), applied second
     */
    public static VoxelShape rotateShape(VoxelShape shape, int xDegrees, int yDegrees) {
        int nx = ((xDegrees % 360) + 360) % 360;
        int ny = ((yDegrees % 360) + 360) % 360;
        if (nx == 0 && ny == 0) {
            return shape;
        }

        double xRad = Math.toRadians(-nx);
        double yRad = Math.toRadians(-ny);

        double cosX = round(Math.cos(xRad));
        double sinX = round(Math.sin(xRad));
        double cosY = round(Math.cos(yRad));
        double sinY = round(Math.sin(yRad));

        VoxelShape[] result = {VoxelShapes.empty()};

        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
            boolean first = true;
            double newMinX = 0, newMinY = 0, newMinZ = 0;
            double newMaxX = 0, newMaxY = 0, newMaxZ = 0;

            for (double bx : new double[]{minX, maxX}) {
                for (double by : new double[]{minY, maxY}) {
                    for (double bz : new double[]{minZ, maxZ}) {
                        double x = bx - 0.5;
                        double y = by - 0.5;
                        double z = bz - 0.5;

                        double y1 = y * cosX - z * sinX;
                        double z1 = y * sinX + z * cosX;
                        y = y1;
                        z = z1;

                        double x1 = x * cosY + z * sinY;
                        double z2 = -x * sinY + z * cosY;
                        x = x1;
                        z = z2;

                        x += 0.5;
                        y += 0.5;
                        z += 0.5;

                        if (first) {
                            newMinX = newMaxX = x;
                            newMinY = newMaxY = y;
                            newMinZ = newMaxZ = z;
                            first = false;
                        } else {
                            newMinX = Math.min(newMinX, x);
                            newMaxX = Math.max(newMaxX, x);
                            newMinY = Math.min(newMinY, y);
                            newMaxY = Math.max(newMaxY, y);
                            newMinZ = Math.min(newMinZ, z);
                            newMaxZ = Math.max(newMaxZ, z);
                        }
                    }
                }
            }

            result[0] = VoxelShapes.union(
                    result[0],
                    VoxelShapes.cuboid(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ)
            );
        });

        return result[0];
    }

    private static double round(double d) {
        return Math.round(d * 1_000_000d) / 1_000_000d;
    }

    /**
     * Builds a Direction -> VoxelShape map from a base (facing=up) shape,
     * using the same x/y values as your blockstate json:
     *
     * down:  x=180
     * up:    (none)
     * north: x=90
     * south: x=90, y=180
     * east:  x=90, y=90
     * west:  x=90, y=270
     */
    public static Map<Direction, VoxelShape> makeShapeMap(VoxelShape baseUpShape) {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);
        map.put(Direction.UP, baseUpShape);
        map.put(Direction.DOWN, rotateShape(baseUpShape, 180, 0));
        map.put(Direction.NORTH, rotateShape(baseUpShape, 90, 0));
        map.put(Direction.SOUTH, rotateShape(baseUpShape, 90, 180));
        map.put(Direction.EAST, rotateShape(baseUpShape, 90, 90));
        map.put(Direction.WEST, rotateShape(baseUpShape, 90, 270));
        return map;
    }
}