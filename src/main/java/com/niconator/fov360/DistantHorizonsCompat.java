package com.niconator.fov360;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import net.neoforged.fml.ModList;

public final class DistantHorizonsCompat {
    private static final String DISTANT_HORIZONS_MODID = "distanthorizons";
    private static final String DH_API_CLASS = "com.seibel.distanthorizons.api.DhApi";
    private static final String DH_CULLING_FRUSTUM_INTERFACE =
        "com.seibel.distanthorizons.api.interfaces.override.rendering.IDhApiCullingFrustum";

    private DistantHorizonsCompat() {
    }

    public static void registerIfLoaded() {
        if (!ModList.get().isLoaded(DISTANT_HORIZONS_MODID)) {
            return;
        }

        try {
            Class<?> dhApiClass = Class.forName(DH_API_CLASS);
            Field overridesField = dhApiClass.getField("overrides");
            Object overrides = overridesField.get(null);
            Class<?> cullingFrustumInterface = Class.forName(DH_CULLING_FRUSTUM_INTERFACE);

            Object cullingFrustum = Proxy.newProxyInstance(
                cullingFrustumInterface.getClassLoader(),
                new Class<?>[] { cullingFrustumInterface },
                new CullingFrustumHandler()
            );

            Method bind = overrides.getClass().getMethod(
                "bind",
                Class.class,
                Class.forName("com.seibel.distanthorizons.api.interfaces.override.IDhApiOverrideable")
            );
            bind.invoke(overrides, cullingFrustumInterface, cullingFrustum);

            Fov360Mod.LOGGER.info("Registered Distant Horizons culling margin compatibility.");
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            Fov360Mod.LOGGER.warn("Unable to register Distant Horizons culling compatibility.", exception);
        }
    }

    private static final class CullingFrustumHandler implements InvocationHandler {
        private final Plane[] planes = new Plane[] {
            new Plane(),
            new Plane(),
            new Plane(),
            new Plane(),
            new Plane(),
            new Plane()
        };

        private int worldMinBlockY;
        private int worldMaxBlockY;

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws ReflectiveOperationException {
            return switch (method.getName()) {
                case "update" -> {
                    update((Integer) args[0], (Integer) args[1], args[2]);
                    yield null;
                }
                case "intersects" -> intersects((Integer) args[0], (Integer) args[1], (Integer) args[2]);
                case "getPriority" -> 10;
                case "finishDelayedSetup" -> null;
                case "getDelayedSetupComplete" -> true;
                case "toString" -> "FOV360 Distant Horizons culling frustum";
                case "hashCode" -> System.identityHashCode(proxy);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException("Unsupported DH API method: " + method);
            };
        }

        private void update(int worldMinBlockY, int worldMaxBlockY, Object worldViewProjection)
            throws ReflectiveOperationException {
            this.worldMinBlockY = worldMinBlockY;
            this.worldMaxBlockY = worldMaxBlockY;

            float m00 = getFloat(worldViewProjection, "m00");
            float m01 = getFloat(worldViewProjection, "m01");
            float m02 = getFloat(worldViewProjection, "m02");
            float m03 = getFloat(worldViewProjection, "m03");
            float m10 = getFloat(worldViewProjection, "m10");
            float m11 = getFloat(worldViewProjection, "m11");
            float m12 = getFloat(worldViewProjection, "m12");
            float m13 = getFloat(worldViewProjection, "m13");
            float m20 = getFloat(worldViewProjection, "m20");
            float m21 = getFloat(worldViewProjection, "m21");
            float m22 = getFloat(worldViewProjection, "m22");
            float m23 = getFloat(worldViewProjection, "m23");
            float m30 = getFloat(worldViewProjection, "m30");
            float m31 = getFloat(worldViewProjection, "m31");
            float m32 = getFloat(worldViewProjection, "m32");
            float m33 = getFloat(worldViewProjection, "m33");

            this.planes[0].set(m30 + m00, m31 + m01, m32 + m02, m33 + m03);
            this.planes[1].set(m30 - m00, m31 - m01, m32 - m02, m33 - m03);
            this.planes[2].set(m30 + m10, m31 + m11, m32 + m12, m33 + m13);
            this.planes[3].set(m30 - m10, m31 - m11, m32 - m12, m33 - m13);
            this.planes[4].set(m30 + m20, m31 + m21, m32 + m22, m33 + m23);
            this.planes[5].set(m30 - m20, m31 - m21, m32 - m22, m33 - m23);
        }

        private boolean intersects(int lodBlockPosMinX, int lodBlockPosMinZ, int lodBlockWidth) {
            double margin = Fov360Config.getDistantHorizonsCullingMarginBlocks();
            double minX = lodBlockPosMinX - margin;
            double minY = this.worldMinBlockY - margin;
            double minZ = lodBlockPosMinZ - margin;
            double maxX = lodBlockPosMinX + lodBlockWidth + margin;
            double maxY = this.worldMaxBlockY + margin;
            double maxZ = lodBlockPosMinZ + lodBlockWidth + margin;

            for (Plane plane : this.planes) {
                double x = plane.x >= 0.0F ? maxX : minX;
                double y = plane.y >= 0.0F ? maxY : minY;
                double z = plane.z >= 0.0F ? maxZ : minZ;
                if (plane.distanceTo(x, y, z) < 0.0D) {
                    return false;
                }
            }

            return true;
        }

        private static float getFloat(Object target, String fieldName) throws ReflectiveOperationException {
            return target.getClass().getField(fieldName).getFloat(target);
        }
    }

    private static final class Plane {
        private float x;
        private float y;
        private float z;
        private float w;

        private void set(float x, float y, float z, float w) {
            float length = (float) Math.sqrt(x * x + y * y + z * z);
            if (length == 0.0F) {
                this.x = x;
                this.y = y;
                this.z = z;
                this.w = w;
                return;
            }

            this.x = x / length;
            this.y = y / length;
            this.z = z / length;
            this.w = w / length;
        }

        private double distanceTo(double x, double y, double z) {
            return this.x * x + this.y * y + this.z * z + this.w;
        }
    }
}
