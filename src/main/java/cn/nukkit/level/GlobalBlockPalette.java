package cn.nukkit.level;

import cn.nukkit.Server;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import com.google.common.io.ByteStreams;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class GlobalBlockPalette {

    private static final Map<Integer, Int2IntMap> legacyToRuntimeIdProtocols = new HashMap<>();
    private static final Map<Integer, Int2IntMap> runtimeIdToLegacyProtocols = new HashMap<>();
    private static final Map<Integer, AtomicInteger> runtimeIdAllocatorProtocols = new HashMap<>();

    public static void loadPalette(int protocol) {
        legacyToRuntimeIdProtocols.put(protocol, new Int2IntOpenHashMap());
        runtimeIdToLegacyProtocols.put(protocol, new Int2IntOpenHashMap());
        runtimeIdAllocatorProtocols.put(protocol, new AtomicInteger(0));

        Int2IntMap legacyToRuntimeId = legacyToRuntimeIdProtocols.get(protocol);
        Int2IntMap runtimeIdToLegacy = runtimeIdToLegacyProtocols.get(protocol);
        AtomicInteger runtimeIdAllocator = runtimeIdAllocatorProtocols.get(protocol);

        legacyToRuntimeId.defaultReturnValue(-1);
        runtimeIdToLegacy.defaultReturnValue(-1);

        ListTag<CompoundTag> tag;
        try (InputStream stream = Server.class.getClassLoader().getResourceAsStream(String.format("versions/v%s/runtime_block_states.dat", protocol))) {
            if (stream == null) {
                throw new AssertionError(String.format("Unable to locate block state nbt [v%s]", protocol));
            }
            //noinspection unchecked
            tag = (ListTag<CompoundTag>) NBTIO.readTag(new ByteArrayInputStream(ByteStreams.toByteArray(stream)), ByteOrder.LITTLE_ENDIAN, false);
        } catch (IOException e) {
            throw new AssertionError(String.format("Unable to load block palette [v%s]", protocol), e);
        }

        for (CompoundTag state : tag.getAll()) {
            int runtimeId = runtimeIdAllocator.getAndIncrement();
            if (!state.contains("LegacyStates")) continue;

            List<CompoundTag> legacyStates = state.getList("LegacyStates", CompoundTag.class).getAll();

            // Resolve to first legacy id
            CompoundTag firstState = legacyStates.get(0);
            runtimeIdToLegacy.put(runtimeId, firstState.getInt("id") << 6 | firstState.getShort("val"));

            for (CompoundTag legacyState : legacyStates) {
                int legacyId = legacyState.getInt("id") << 6 | legacyState.getShort("val");
                legacyToRuntimeId.put(legacyId, runtimeId);
            }
        }
    }

    public static int getOrCreateRuntimeId(int protocol, int id, int meta) {
        Int2IntMap legacyToRuntimeId = legacyToRuntimeIdProtocols.get(protocol);
        Int2IntMap runtimeIdToLegacy = runtimeIdToLegacyProtocols.get(protocol);
        AtomicInteger runtimeIdAllocator = runtimeIdAllocatorProtocols.get(protocol);
        int legacyId = id << 6 | meta;
        int runtimeId = legacyToRuntimeId.get(legacyId);
        if (runtimeId == -1) {
            runtimeId = legacyToRuntimeId.get(id << 6);
            if (runtimeId == -1) {
                log.info("Creating new runtime ID for unknown block {}", id);
                runtimeId = runtimeIdAllocator.getAndIncrement();
                legacyToRuntimeId.put(id << 6, runtimeId);
                runtimeIdToLegacy.put(runtimeId, id << 6);
            }
        }
        return runtimeId;
    }

    public static int getOrCreateRuntimeId(int protocol, int legacyId) throws NoSuchElementException {
        return getOrCreateRuntimeId(protocol, legacyId >> 4, legacyId & 0xf);
    }
}
