package cn.nukkit.network.protocol;

import cn.nukkit.Nukkit;
import com.google.common.io.ByteStreams;
import lombok.ToString;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@ToString(exclude = "tag")
public class BiomeDefinitionListPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BIOME_DEFINITION_LIST_PACKET;

    private static final Map<Integer, byte[]> PROTOCOL_TAGS = new HashMap<>();

    public static void loadDefinitions(int protocol) {
        try {
            InputStream inputStream = Nukkit.class.getClassLoader().getResourceAsStream(String.format("versions/v%s/biome_definitions.dat", protocol));
            if (inputStream == null) {
                throw new AssertionError(String.format("Could not find biome_definitions.dat [v%s]", protocol));
            }
            //noinspection UnstableApiUsage
            PROTOCOL_TAGS.put(protocol, ByteStreams.toByteArray(inputStream));
        } catch (Exception e) {
            throw new AssertionError(String.format("Error whilst loading biome_definitions.dat [v%s]", protocol), e);
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
    }

    @Override
    public void encode() {
        this.reset();
        this.put(tag);
    }
}
