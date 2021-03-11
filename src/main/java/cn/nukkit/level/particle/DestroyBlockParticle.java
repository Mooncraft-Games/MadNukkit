package cn.nukkit.level.particle;

import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.network.protocol.ProtocolInfo;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.level.particle in project Nukkit .
 */
public class DestroyBlockParticle extends Particle {

    protected final int data;

    protected final Block block;  // protocol support

    public DestroyBlockParticle(Vector3 pos, Block block) {
        super(pos.x, pos.y, pos.z);
        this.block = block;
        this.data = GlobalBlockPalette.getOrCreateRuntimeId(ProtocolInfo.CURRENT_PROTOCOL, block.getId(), block.getDamage());
    }

    @Override
    public DataPacket[] encode(int[] protocols) {
        DataPacket[] packets = new DataPacket[protocols.length];
        int index = 0;
        for (int protocol : protocols) {
            LevelEventPacket pk = new LevelEventPacket();
            pk.setProtocolVersion(protocol);
            pk.evid = LevelEventPacket.EVENT_PARTICLE_DESTROY;
            pk.x = (float) this.x;
            pk.y = (float) this.y;
            pk.z = (float) this.z;
            pk.data = GlobalBlockPalette.getOrCreateRuntimeId(protocol, this.block.getId(), this.block.getDamage());
            packets[index++] = pk;
        }

        return packets;
    }
}
