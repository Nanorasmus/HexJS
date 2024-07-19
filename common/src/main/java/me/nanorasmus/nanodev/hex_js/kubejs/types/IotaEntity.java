package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.iota.BooleanIota;
import at.petrak.hexcasting.api.spell.iota.EntityIota;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.entity.Entity;

public class IotaEntity implements IotaJS {
    private Entity entity;

    // -- Basic stuff --

    public IotaEntity(Entity entity) {
        this.entity = entity;
    }
    @HideFromJS
    public IotaEntity(EntityIota iota) {
        entity = iota.getEntity();
    }


    @Override
    @HideFromJS
    public EntityIota toIota() {
        return new EntityIota(entity);
    }

    @Override
    public IotaEntity copy() {
        return new IotaEntity(entity);
    }
}
