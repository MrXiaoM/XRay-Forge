package com.xray.keybinding;

import com.xray.Configuration;
import com.xray.gui.GuiSelectionScreen;
import com.xray.xray.AntiAntiXray;
import com.xray.xray.Controller;
import com.xray.XRay;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class InputEvent
{
	@SubscribeEvent
	public void onKeyInput( KeyInputEvent event )
    {
		if( (!FMLClientHandler.instance().isGUIOpen( GuiChat.class )) && (XRay.mc.currentScreen == null) && (XRay.mc.world != null) )
        {
			if( KeyBindings.keyBind_keys[ KeyBindings.keyIndex_toggleXray ].isPressed() )
			{
				Controller.toggleDrawOres();
			}
			else if( KeyBindings.keyBind_keys[ KeyBindings.keyIndex_showXrayMenu ].isPressed() )
			{
				XRay.mc.displayGuiScreen( new GuiSelectionScreen() );
			}
			else if (KeyBindings.keyBind_keys[KeyBindings.keyIndex_scan].isPressed()){
				assert Minecraft.getMinecraft().player != null;
				Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString("§6[ §a！ §6] §fRefreshing blocks..."), true);
				AntiAntiXray.revealNewBlocks(Configuration.radius_x, Configuration.radius_y, Configuration.radius_z, Configuration.delay);
			}
			else if (KeyBindings.keyBind_keys[KeyBindings.keyIndex_removeblock].isPressed()){
				Minecraft.getMinecraft().gameSettings.gammaSetting = 114514.0F;
				for (int cx = -2; cx <= 2; cx++) {
					for (int cy = 0; cy <= 2; cy++) {
						for (int cz = -2; cz <= 2; cz++) {
							assert Minecraft.getMinecraft().player != null;
							BlockPos b2r = Minecraft.getMinecraft().player.getPosition();
							//BlockState a = MinecraftClient.getInstance().player.world.getBlockState(b2r.add(cx,cy,cz));

							Block s = Block.getBlockFromItem(Minecraft.getMinecraft().player.inventory.getCurrentItem().getItem());
							IBlockState b = Blocks.AIR.getDefaultState();
							if(s!=null) b =	s.getDefaultState();

							Minecraft.getMinecraft().player.world.setBlockState(b2r.add(cx, cy, cz), b);
							//MinecraftClient.getInstance().player.world.removeBlock(b2r.add(cx, cy, cz), false);
						}
					}
				}
			} else if (KeyBindings.keyBind_keys[KeyBindings.keyIndex_freeze].isPressed()){
				Configuration.freeze = !Configuration.freeze;
				Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString("§6[ §a！ §6] §fFreeze now " + (Configuration.freeze?"opened" : "closed")), true);
			}
		}
	}
}
