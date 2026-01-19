package io.github.tobyrue.btc.util;
import org.joml.Vector3f;

public class AABB {
    public final Vector3f min, max;

    public AABB(Vector3f min, Vector3f max) {
        this.min = new Vector3f(min);
        this.max = new Vector3f(max);
    }

    public static Vector3f getCenter(AABB a) {
        return new Vector3f(a.min).add(a.max).mul(0.5f);
    }

    public static Vector3f getSize(AABB a) {
        return new Vector3f(a.max).sub(a.min);
    }

    public static Vector3f getOverlap(AABB a, AABB b) {
        Vector3f minMax = new Vector3f(
                Math.min(a.max.x, b.max.x),
                Math.min(a.max.y, b.max.y),
                Math.min(a.max.z, b.max.z)
        );
        Vector3f maxMin = new Vector3f(
                Math.max(a.min.x, b.min.x),
                Math.max(a.min.y, b.min.y),
                Math.max(a.min.z, b.min.z)
        );
        return minMax.sub(maxMin);
    }

    public static Vector3f getMTV(AABB a, AABB b) {
        Vector3f overlap = getOverlap(a, b);
        Vector3f ca = getCenter(a);
        Vector3f cb = getCenter(b);

        Vector3f lessThan = new Vector3f(
                ca.x < cb.x ? 1.0f : 0.0f,
                ca.y < cb.y ? 1.0f : 0.0f,
                ca.z < cb.z ? 1.0f : 0.0f
        );

        Vector3f translation = new Vector3f(
                (1.0f - 2.0f * lessThan.x) * overlap.x,
                (1.0f - 2.0f * lessThan.y) * overlap.y,
                (1.0f - 2.0f * lessThan.z) * overlap.z
        );

        // Find minimum axis, prefer y
        if (overlap.y <= overlap.x && overlap.y <= overlap.z) {
            return new Vector3f(0.0f, translation.y, 0.0f);
        } else if (overlap.x <= overlap.z) {
            return new Vector3f(translation.x, 0.0f, 0.0f);
        } else {
            return new Vector3f(0.0f, 0.0f, translation.z);
        }
    }
}