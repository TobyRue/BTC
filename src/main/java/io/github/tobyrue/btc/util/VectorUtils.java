package io.github.tobyrue.btc.util;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.Vector;

public class VectorUtils {

    public static double[] getPitchAndYaw(Vec3d source, Vec3d target) {
        double dx = target.x - source.x;
        double dy = target.y - source.y;
        double dz = target.z - source.z;

        double yaw = Math.toDegrees(Math.atan2(dx, dz));
        double pitch = Math.toDegrees(Math.atan2(-dy, Math.sqrt(dx*dx + dz*dz)));

        return new double[]{yaw, pitch};
    }
}