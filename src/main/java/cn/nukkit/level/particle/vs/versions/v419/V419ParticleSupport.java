package cn.nukkit.level.particle.vs.versions.v419;

import cn.nukkit.level.particle.Particle;
import cn.nukkit.level.particle.vs.versions.ParticleSupport;

public class V419ParticleSupport extends ParticleSupport {

    public V419ParticleSupport() {
        // TODO: We need to put all of the particle ids in the modifications here since 419 is the lowest version we support.

        // Unsupported particles: Introduced in 1.16.220
        this.modifications.put(Particle.TYPE_STALACTITE_DRIP_LAVA, -1);
        this.modifications.put(Particle.TYPE_STALACTITE_DRIP_WATER, -1);

    }

}
