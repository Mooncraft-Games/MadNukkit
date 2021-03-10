package cn.nukkit.utils;

import java.util.HashMap;
import java.util.Map;

public class ProtocolEntityDataModifier {

    private final int previousProtocol;
    private final Map<Integer, Integer> remapping = new HashMap<>();

    public ProtocolEntityDataModifier(int previousProtocol) {
        this.previousProtocol = previousProtocol;
    }

    public ProtocolEntityDataModifier remap(int idToChange, int newId) {
        remapping.put(idToChange, newId);
        return this;
    }

    public Map<Integer, Integer> getRemappings() {
        return new HashMap<>(this.remapping);
    }

    public int getPreviousProtocol() {
        return this.previousProtocol;
    }

}
