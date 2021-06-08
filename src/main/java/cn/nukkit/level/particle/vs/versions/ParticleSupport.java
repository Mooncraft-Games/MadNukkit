package cn.nukkit.level.particle.vs.versions;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a version that changes particle ids
 */
public abstract class ParticleSupport {

    /**
     * Represents all of the modifications that need to be made to convert a particle id from a higher protocol
     * to this protocol.
     * If the particle is not supported in this version it will return -1
     */
    protected final Map<Integer, Integer> modifications = new HashMap<>();

    public int getParticleId(int particleId) {
        return this.modifications.getOrDefault(particleId, particleId);
    }

}
