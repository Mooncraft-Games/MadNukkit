package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@ToString(exclude = {"tag"})
public class AvailableEntityIdentifiersPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.AVAILABLE_ENTITY_IDENTIFIERS_PACKET;

    private static final Map<Integer, byte[]> PROTOCOL_TAGS = new HashMap<>();

    public static void loadIdentifiers(int protocol) {
        try {
            InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream(String.format("versions/v%s/entity_identifiers.dat", protocol));
            if (inputStream == null) {
                throw new AssertionError(String.format("Could not find entity_identifiers.dat [v%s]", protocol));
            }
            //noinspection UnstableApiUsage
            PROTOCOL_TAGS.put(protocol, ByteStreams.toByteArray(inputStream));
        } catch (Exception e) {
            throw new AssertionError(String.format("Error whilst loading entity_identifiers.dat [v%s]", protocol), e);
        }
    }

    public byte[] tag;

    @Override
    public void setProtocolVersion(int protocol) {
        super.setProtocolVersion(protocol);
        tag = PROTOCOL_TAGS.get(protocol);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.tag = this.get();
    }

    @Override
    public void encode() {
        this.reset();
        this.put(this.tag);
    }
}
