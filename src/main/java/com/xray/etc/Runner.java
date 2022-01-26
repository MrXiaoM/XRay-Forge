package com.xray.etc;


import com.xray.Configuration;
import com.xray.xray.Controller;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import java.text.DecimalFormat;

public class Runner implements Runnable {
    boolean isRunning = true;
    public boolean done = false;
    long delay;
    int current;
    int max;
    int radX;
    int radY;
    int radZ;
    public Runner(int radX, int radY, int radZ, long delay) {
        this.max = (radX + radX + 1) * (radY + radY + 1) * (radZ + radZ + 1);
        this.radX = radX;
        this.radY = radY;
        this.radZ = radZ;
        this.delay = delay;
    }

    public double getProcess(){
        return (double)current / (double)max * 100.0D;
    }
    public String getProcessText(){
        return new DecimalFormat("0.00").format(getProcess());
    }

    @SuppressWarnings("BusyWait")
    @Override
    public void run() {
        NetHandlerPlayClient conn = Minecraft.getMinecraft().getConnection();
        if (conn == null) return;
        assert Minecraft.getMinecraft().player != null;
        BlockPos pos = Minecraft.getMinecraft().player.getPosition();

        for (int cx = -radX; cx <= radX; cx++) {
            for (int cy = -radY; cy <= radY; cy++) {
                for (int cz = -radZ; cz <= radZ; cz++) {
                    if (!isRunning) break;
                    current++;
                    BlockPos currblock = new BlockPos(pos.getX() + cx, pos.getY() + cy, pos.getZ() + cz);
                    // Block block = Minecraft.getMinecraft().player.world.getBlockState(currblock).getBlock();

                    CPacketPlayerDigging packet = new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.START_DESTROY_BLOCK,
                            currblock,
                            EnumFacing.UP
                    );
                    conn.sendPacket(packet);
                    packet = new CPacketPlayerDigging(
                            CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK,
                            currblock,
                            EnumFacing.UP
                    );
                    conn.sendPacket(packet);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        //e.printStackTrace();
                    }
                }
            }
        }
        Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString("§6[ §a！ §6] §fRefresh done."), true);
        Configuration.freeze = false;
        Controller.requestBlockFinder(true);
        done = true;
    }
}
