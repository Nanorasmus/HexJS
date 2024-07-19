package me.nanorasmus.nanodev.hex_js.kubejs.types;

import at.petrak.hexcasting.api.spell.SpellList;
import at.petrak.hexcasting.api.spell.iota.*;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import java.util.ArrayList;
import java.util.List;

public class IotaList implements IotaJS {
    private SpellList internalStack;

    // -- Basic stuff --

    public IotaList (List<Iota> stack) {
        internalStack = new ListIota(stack).getList();
    }
    public IotaList (ListIota list) {
        internalStack = list.getList();
    }

    @Override
    public ListIota toIota() {
        return new ListIota(internalStack);
    }

    public ArrayList<Iota> toIotaList() {
        ArrayList<Iota> stack = new ArrayList<>();
        for (int i = 0; i < internalStack.size(); i++) {
            stack.add(internalStack.getAt(i));
        }
        return stack;
    }

    /**
     * @return The length of the list
     */
    public int getLength() {
        return internalStack.size();
    }

    public IotaList copy() {
        ArrayList<Iota> iotaList = new ArrayList<>();
        for (int i = 0; i < internalStack.size(); i++) {
            Iota iota = internalStack.getAt(i);
            if (iota.getType() == HexIotaTypes.LIST) {
                iotaList.add(new IotaList((ListIota) iota).copy().toIota());
            }
            if (iota.getType() == HexIotaTypes.DOUBLE) {
                iotaList.add(new IotaDouble((DoubleIota) iota).copy().toIota());
            }
            if (iota.getType() == HexIotaTypes.BOOLEAN) {
                iotaList.add(new IotaBoolean((BooleanIota) iota).copy().toIota());
            }
            if (iota.getType() == HexIotaTypes.VEC3) {
                iotaList.add(new IotaVector((Vec3Iota) iota).copy().toIota());
            }
        }
        return new IotaList(iotaList);
    }

    /**
     * Adds an Iota to the end of the list
     * @param iota The iota to add
     * @return A copy of itself with the specified iota at end
     */
    public IotaList addElement(Iota iota) {
        List<Iota> iotaList = toIotaList();
        iotaList.add(iota);
        return new IotaList(iotaList);
    }

    // -- List element handling --

    /**
     * @param idx The index of the desired iota
     * @return The iota at the given index, or null if there is none
     */
    public IotaJS getListAtIndex (int idx) {
        if (idx >= internalStack.size()) {
            return null;
        }
        Iota iota = internalStack.getAt(idx);
        if (iota.getType() == HexIotaTypes.LIST) {
            return new IotaList((ListIota) iota);
        } if (iota.getType() == HexIotaTypes.DOUBLE) {
            return new IotaDouble((DoubleIota) iota);
        } if (iota.getType() == HexIotaTypes.BOOLEAN) {
            return new IotaBoolean((BooleanIota) iota);
        } if (iota.getType() == HexIotaTypes.VEC3) {
            return new IotaVector((Vec3Iota) iota);
        } else {
            return null;
        }
    }

    /**
     * Sets the element at the given index to a iota and returns itself, or null if the index was out of bounds.
     * @param idx The index to override
     * @param iota the iota to override with
     * @return itself or null if the index was out of bounds
     */
    public IotaList setElementAt(int idx, IotaJS iota) {
        if (idx >= internalStack.size()) {
            return null;
        }

        internalStack = internalStack.modifyAt(idx, it -> new SpellList.LPair(iota.toIota(), it.getCdr()));

        return this;
    }
}
