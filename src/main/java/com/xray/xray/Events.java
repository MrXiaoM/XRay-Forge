package com.xray.xray;

import com.xray.Configuration;
import com.xray.XRay;
import com.xray.etc.RefreshingJob;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

public class Events
{
	public BlockPos old;
	public int movedblocks;

	@SubscribeEvent
	public void pickupItem( BlockEvent.BreakEvent event )
	{
		RenderEnqueue.checkBlock( event.getPos(), event.getState(), false);
	}

	@SubscribeEvent
	public void placeItem( BlockEvent.PlaceEvent event )
	{
		RenderEnqueue.checkBlock( event.getPos(), event.getState(), true);
	}

	@SubscribeEvent
	public void chunkLoad( ChunkEvent.Load event )
	{
		Controller.requestBlockFinder( true );
	}

	@SubscribeEvent
	public void tickEnd( TickEvent.ClientTickEvent event )
	{
		if ( event.phase == TickEvent.Phase.END )
		{
			Controller.requestBlockFinder( false );
		}

		List<RefreshingJob> nl = new ArrayList<>();
		AntiAntiXray.jobs.forEach(refreshingJob -> {
			if (!refreshingJob.refresher.done) {
				nl.add(refreshingJob);
			}
		});
		AntiAntiXray.jobs = nl;
		if (Configuration.auto) {
			try {
				assert Minecraft.getMinecraft().player != null;
				BlockPos pos = Minecraft.getMinecraft().player.getPosition();

				if (pos != old) {
					movedblocks++;

					if (movedblocks > Configuration.movethreshhold && AntiAntiXray.jobs.size() == 0) {
						AntiAntiXray.revealNewBlocks(Configuration.radius_x, Configuration.radius_y, Configuration.radius_z, Configuration.delay);
						XRay.logger.info("Scanning new pos: " + pos);
						movedblocks = 0;
					}
				}
				old = pos;

			} catch (NullPointerException e) {
				XRay.logger.info("Null Error");
			}
		}
	}

	@SubscribeEvent
	public void onWorldRenderLast( RenderWorldLastEvent event ) // Called when drawing the world.
	{
		if ( Controller.drawOres() )
		{
			float f = event.getPartialTicks();

			// this is a world pos of the player
			Render.drawOres(
				(float)XRay.mc.player.prevPosX + ( (float)XRay.mc.player.posX - (float)XRay.mc.player.prevPosX ) * f,
				(float)XRay.mc.player.prevPosY + ( (float)XRay.mc.player.posY - (float)XRay.mc.player.prevPosY ) * f,
				(float)XRay.mc.player.prevPosZ + ( (float)XRay.mc.player.posZ - (float)XRay.mc.player.prevPosZ ) * f
			);
		}
	}
}
