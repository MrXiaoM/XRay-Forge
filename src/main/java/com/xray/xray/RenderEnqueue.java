package com.xray.xray;

import com.xray.Configuration;
import com.xray.XRay;
import com.xray.reference.block.BlockData;
import com.xray.reference.block.BlockInfo;
import com.xray.utils.WorldRegion;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import java.util.*;

public class RenderEnqueue implements Runnable
{
	private final WorldRegion box;

	public RenderEnqueue(WorldRegion region )
	{
		box = region;
	}

	@Override
	public void run() // Our thread code for finding ores near the player.
	{
		//blockFinder();
		if(!Configuration.freeze) blockFinder2();
	}
	private void blockFinder2() {
		HashMap<String, BlockData> blocks = Controller.getBlockStore().getStore();

		if ( blocks.isEmpty() ) {
			if( !Render.ores.isEmpty() )
				Render.ores.clear();
			return; // no need to scan the region if there's nothing to find
		}

		final World world = XRay.mc.world;
		final List<BlockInfo> renderQueue = new ArrayList<>();

		BlockPos pos = XRay.mc.player.getPosition();

		int radX = Configuration.radius_x;
		int radY = Configuration.radius_y;
		int radZ = Configuration.radius_z;
		IBlockState currentState;
		IBlockState defaultState;
		BlockData blockData;

		for (int cx = -radX; cx <= radX; cx++) {
			for (int cy = -radY; cy <= radY; cy++) {
				for (int cz = -radZ; cz <= radZ; cz++) {
					BlockPos currblock = new BlockPos(pos.getX() + cx, pos.getY() + cy, pos.getZ() + cz);
					currentState = Minecraft.getMinecraft().player.world.getBlockState(currblock);

					// Reject blacklisted blocks
					if( Controller.blackList.contains(currentState.getBlock()) )
						continue;

					defaultState = currentState.getBlock().getDefaultState();

					boolean defaultExists = blocks.containsKey(defaultState.toString());
					boolean currentExists = blocks.containsKey(currentState.toString());
					if( !defaultExists && !currentExists )
						continue;

					 blockData = blocks.get(currentExists ? currentState.toString() : defaultState.toString());
					if( blockData == null || !blockData.isDrawing() ) // fail safe
						continue;

					// Calculate distance from player to block. Fade out futher away blocks
					double alpha = !Configuration.shouldFade ? 255 : Math.max(0, ((Controller.getRadius() - XRay.mc.player.getDistance(currblock.getX(), currblock.getY(), currblock.getZ())) / Controller.getRadius() ) * 255);

					// Push the block to the render queue
					renderQueue.add(new BlockInfo(currblock.getX(), currblock.getY(), currblock.getZ(), blockData.getColor().getColor(), alpha));
					//System.out.println(block.getLocalizedName() + " (" + currblock.getX() + ", " + currblock.getY() + ", " + currblock.getZ() + ")");
					/*
					if(AntiAntiXray.isOre(block)) {
						renderQueue.add(new BlockInfo(currblock.getX(), currblock.getY(), currblock.getZ(),
							AntiAntiXray.getOreColor(block),1));
					}*/
				}
			}
		}

		renderQueue.sort((t, t1) -> Double.compare(t1.distanceSq(pos), t.distanceSq(pos)));
		Render.ores.clear();
		Render.ores.addAll( renderQueue ); // Add all our found blocks to the Render.ores list. To be use by Render when drawing.
	}
	/**
	 * Use Controller.requestBlockFinder() to trigger a scan.
	 */
	private void blockFinder() {
        HashMap<String, BlockData> blocks = Controller.getBlockStore().getStore();

		if ( blocks.isEmpty() ) {
		    if( !Render.ores.isEmpty() )
		        Render.ores.clear();
            return; // no need to scan the region if there's nothing to find
        }

		final World world = XRay.mc.world;
		final List<BlockInfo> renderQueue = new ArrayList<>();

		int lowBoundX, highBoundX, lowBoundY, highBoundY, lowBoundZ, highBoundZ;

		// Used for cleaning up the searching process
		IBlockState currentState;
		IBlockState defaultState;
		BlockData blockData;

		// Loop on chunks (x, z)
		for ( int chunkX = box.minChunkX; chunkX <= box.maxChunkX; chunkX++ )
		{
			// Pre-compute the extend bounds on X
			int x = chunkX << 4; // lowest x coord of the chunk in block/world coordinates
			lowBoundX = (x < box.minX) ? box.minX - x : 0; // lower bound for x within the extend
			highBoundX = (x + 15 > box.maxX) ? box.maxX - x : 15;// and higher bound. Basically, we clamp it to fit the radius.

			for ( int chunkZ = box.minChunkZ; chunkZ <= box.maxChunkZ; chunkZ++ )
			{
				// Time to getStore the chunk (16x256x16) and split it into 16 vertical extends (16x16x16)
				Chunk chunk = world.getChunkFromChunkCoords( chunkX, chunkZ );
				if (!chunk.isLoaded()) {
					continue; // We won't find anything interesting in unloaded chunks
				}
				ExtendedBlockStorage[] extendsList = chunk.getBlockStorageArray();

				// Pre-compute the extend bounds on Z
				int z = chunkZ << 4;
				lowBoundZ = (z < box.minZ) ? box.minZ - z : 0;
				highBoundZ = (z + 15 > box.maxZ) ? box.maxZ - z : 15;

				// Loop on the extends around the player's layer (6 down, 2 up)
				for ( int curExtend = box.minChunkY; curExtend <= box.maxChunkY; curExtend++ )
				{
					ExtendedBlockStorage ebs = extendsList[curExtend];
					if (ebs == null) // happens quite often!
						continue;

					// Pre-compute the extend bounds on Y
					int y = curExtend << 4;
					lowBoundY = (y < box.minY) ? box.minY - y : 0;
					highBoundY = (y + 15 > box.maxY) ? box.maxY - y : 15;

					// Now that we have an extend, let's check all its blocks
					for ( int i = lowBoundX; i <= highBoundX; i++ ) {
						for ( int j = lowBoundY; j <= highBoundY; j++ ) {
							for ( int k = lowBoundZ; k <= highBoundZ; k++ ) {
								currentState = ebs.get(i, j, k);

								// Reject blacklisted blocks
								if( Controller.blackList.contains(currentState.getBlock()) )
									continue;

								defaultState = currentState.getBlock().getDefaultState();

								boolean defaultExists = blocks.containsKey(defaultState.toString());
								boolean currentExists = blocks.containsKey(currentState.toString());
								if( !defaultExists && !currentExists )
									continue;

								blockData = blocks.get(currentExists ? currentState.toString() : defaultState.toString());
								if( blockData == null || !blockData.isDrawing() ) // fail safe
									continue;

								// Calculate distance from player to block. Fade out futher away blocks
								double alpha = !Configuration.shouldFade ? 255 : Math.max(0, ((Controller.getRadius() - XRay.mc.player.getDistance(x + i, y + j, z + k)) / Controller.getRadius() ) * 255);

								// Push the block to the render queue
								renderQueue.add(new BlockInfo(x + i, y + j, z + k, blockData.getColor().getColor(), alpha));
							}
						}
					}
				}
			}
		}
		final BlockPos playerPos = XRay.mc.player.getPosition();
		renderQueue.sort((t, t1) -> Double.compare(t1.distanceSq(playerPos), t.distanceSq(playerPos)));
		Render.ores.clear();
		Render.ores.addAll( renderQueue ); // Add all our found blocks to the Render.ores list. To be use by Render when drawing.
	}

	/**
	 * Single-block version of blockFinder. Can safely be called directly
	 * for quick block check.
	 * @param pos the BlockPos to check
	 * @param state the current state of the block
	 * @param add true if the block was added to world, false if it was removed
	 */
	public static void checkBlock( BlockPos pos, IBlockState state, boolean add )
	{
		if ( !Controller.drawOres() || Controller.getBlockStore().getStore().isEmpty() )
		    return; // just pass

		String defaultState = state.getBlock().getDefaultState().toString();

		// Let's see if the block to check is an ore we monitor
		if ( Controller.getBlockStore().getStore().containsKey(defaultState) ) // it's a block we are monitoring
		{
		    if( !add )
		    {
                Render.ores.remove( new BlockInfo(pos, null, 0.0) );
                return;
            }

		    BlockData data = null;
            if( Controller.getBlockStore().getStore().containsKey(defaultState) )
                data = Controller.getBlockStore().getStore().get(defaultState);

            if( data == null )
            	return;

			double alpha = !Configuration.shouldFade ? 255 : Math.max(0, ((Controller.getRadius() - XRay.mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ())) / Controller.getRadius() ) * 255);

            // the block was added to the world, let's add it to the drawing buffer
            Render.ores.add( new BlockInfo(pos, data.getColor().getColor(), alpha) );
		}
	}
}
