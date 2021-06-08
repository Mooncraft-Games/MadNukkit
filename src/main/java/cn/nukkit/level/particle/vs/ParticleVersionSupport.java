package cn.nukkit.level.particle.vs;

import cn.nukkit.level.particle.vs.versions.ParticleSupport;
import cn.nukkit.level.particle.vs.versions.v419.V419ParticleSupport;
import cn.nukkit.level.particle.vs.versions.v428.V428ParticleSupport;
import cn.nukkit.network.protocol.ProtocolInfo;

import java.util.HashMap;
import java.util.Map;

public class ParticleVersionSupport {

    private static final Map<Integer, ParticleSupport> DATA = new HashMap<>();

    static {
        DATA.put(ProtocolInfo.VERSION_1_16_210_PROTOCOL, new V428ParticleSupport());
        // TODO: Check if other protocols modify particles. Otherwise we assign it to 1.16.100
        DATA.put(ProtocolInfo.VERSION_1_16_100_PROTOCOL, new V419ParticleSupport());
    }


    public static int getProtocolParticleId(int targetProtocol, int particleId) {
        if (DATA.containsKey(targetProtocol)) {
            return DATA.get(targetProtocol).getParticleId(particleId);
        } else {
            return particleId;
        }

    }

}
