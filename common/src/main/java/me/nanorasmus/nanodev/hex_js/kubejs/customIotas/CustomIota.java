package me.nanorasmus.nanodev.hex_js.kubejs.customIotas;

import at.petrak.hexcasting.api.spell.iota.Iota;
import at.petrak.hexcasting.api.spell.iota.IotaType;
import dev.latvian.mods.rhino.util.HideFromJS;
import me.nanorasmus.nanodev.hex_js.HexJS;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static me.nanorasmus.nanodev.hex_js.kubejs.customIotas.CustomIotaRegistry.customIotas;

public class CustomIota extends Iota {
    public final String type;

    @HideFromJS
    private CustomIotaEntry entry;

    public HashMap<String, Object> data = new HashMap<>();
    public NbtCompound persistentData = new NbtCompound();
    public Object[] initParams;

    @HideFromJS
    public CustomIota(@NotNull NbtCompound content) {
        super(TYPE, content);

        type = content.getString("hex_js:type");
        this.persistentData = content;

        // Check for registration
        if (customIotas.containsKey(type)) {
            entry = customIotas.get(type);
        } else {
            HexJS.LOGGER.severe("Tried initializing an iota whose type had not been registered!");
            entry = new CustomIotaEntry();
        }
    }

    public CustomIota(@NotNull String type) {
        super(TYPE, new NbtCompound());

        this.type = type;

        // Check for registration
        if (customIotas.containsKey(type)) {
            entry = customIotas.get(type);
        } else {
            HexJS.LOGGER.severe("Tried initializing an iota whose type had not been registered!");
            entry = new CustomIotaEntry();
        }

        // Assign type
        persistentData.putString("hex_js:type", type); // Might not apply, I'm honestly not sure

        // Run constructor
        initParams = new List[]{};
        entry.init.accept(this);
    }

    public CustomIota(@NotNull String type, @NotNull Object ...params) {
        super(TYPE, new NbtCompound());

        this.type = type;

        // Check for registration
        if (customIotas.containsKey(type)) {
            entry = customIotas.get(type);
        } else {
            HexJS.LOGGER.severe("Tried initializing an iota whose type had not been registered!");
            entry = new CustomIotaEntry();
        }

        // Assign type
        persistentData.putString("hex_js:type", type); // Might not apply, I'm honestly not sure

        // Run constructor
        initParams = params;
        entry.init.accept(this);
    }

    @Override
    public boolean isTruthy() {
       boolean output;

       output = entry.isTruthy.apply(this);

       return output;
    }

    @Override
    protected boolean toleratesOther(Iota that) {
        boolean output;

        output = entry.toleratesOther.invoke(this, that);

        return output;
    }

    @Override
    public @NotNull NbtElement serialize() {
        return entry.serialize.apply(this);
    }

    public static IotaType<CustomIota> TYPE = new IotaType<>() {
        @Nullable
        @Override
        public CustomIota deserialize(NbtElement tag, ServerWorld world) throws IllegalArgumentException {
            NbtCompound compound = (NbtCompound) tag;
            CustomIota deserializedIota = new CustomIota(compound);
            return deserializedIota.entry.deserialize.invoke(deserializedIota, world);
        }

        @Override
        public Text display(NbtElement tag) {
            String outputString;
            if (tag instanceof NbtCompound ctag) {
                if (ctag.contains("display")) {
                    // Get the desired display
                    outputString = ctag.getString("display");
                } else {
                    // Get the name of the iota type
                    outputString = ctag.getString("hex_js:type");
                }
            } else {
                // Serialized iota is not proper
                outputString = "SERIALIZATION_ERROR";
            }
            return Text.of(outputString);
        }

        @Override
        public int color() {
            return 0xff_ffffff;
        }
    };
}
