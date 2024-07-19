package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.Vec3Iota;
import net.minecraft.util.math.Vec3d;

public class IotaVector implements IotaJS {
    private Vec3d value;

    // -- Basic stuff --

    public IotaVector (Vec3d value) {
        this.value = value;
    }
    public IotaVector (Vec3Iota iota) {
        this.value = iota.getVec3();
    }
    public IotaVector (double x, double y, double z) {
        this.value = new Vec3d(x, y, z);
    }

    @Override
    public Vec3Iota toIota() {
        return new Vec3Iota(value);
    }

    @Override
    public IotaVector copy() {
        return new IotaVector(value.x, value.y, value.z);
    }

    public double getX() {
        return value.x;
    }
    public double getY() {
        return value.y;
    }
    public double getZ() {
        return value.z;
    }
}
