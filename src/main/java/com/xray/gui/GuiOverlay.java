package com.xray.gui;

import com.xray.xray.AntiAntiXray;
import com.xray.xray.Controller;
import com.xray.Configuration;
import com.xray.XRay;
import com.xray.reference.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiOverlay {
    private static final ResourceLocation circle = new ResourceLocation(Reference.PREFIX_GUI + "circle.png");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void RenderGameOverlayEvent(RenderGameOverlayEvent.Post event) {
        int width = event.getResolution().getScaledWidth();
        for (int i = 0; i < AntiAntiXray.jobs.size(); i++) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(width - 160, i * 32, 0);
            Minecraft.getMinecraft().getTextureManager().bindTexture(IToast.TEXTURE_TOASTS);
            GlStateManager.color(1.0F, 1.0F, 1.0F);
            drawTexturedModalRect(0, 0, 0, 96, 160, 32);
            XRay.mc.fontRenderer.drawStringWithShadow(I18n.format("xray.toast.title"),
                    6, 6, 0x000000);
            XRay.mc.fontRenderer.drawStringWithShadow(AntiAntiXray.jobs.get(i).refresher.getProcessText(),
                    6, 18, 0x000000);
            GlStateManager.popMatrix();
        }
        // Draw Indicator
        if(!Controller.drawOres() || !Configuration.showOverlay)
            return;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color(0, 255, 0, 30);
        XRay.mc.renderEngine.bindTexture(circle);
        Gui.drawModalRectWithCustomSizedTexture(5, 5, 0f, 0f, 5, 5, 5, 5);
        GlStateManager.disableBlend();

        XRay.mc.fontRenderer.drawStringWithShadow(I18n.format("xray.overlay"), 15, 4, java.awt.Color.getHSBColor(0f, 0f, 1f).getRGB() + (30 << 24));
        if(Configuration.freeze) {
            GlStateManager.enableBlend();
            GlStateManager.color(0, 255, 0, 30);
            XRay.mc.renderEngine.bindTexture(circle);
            Gui.drawModalRectWithCustomSizedTexture(5, 17, 0f, 0f, 5, 5, 5, 5);
            GlStateManager.disableBlend();
            XRay.mc.fontRenderer.drawStringWithShadow(I18n.format("xray.freeze"), 15, 16, java.awt.Color.getHSBColor(0f, 0f, 1f).getRGB() + (30 << 24));
        }
        GlStateManager.popMatrix();
    }
    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height)
    {
        double zLevel = 0;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(x, (y + height), zLevel).tex(((float)(textureX) * 0.00390625F), ((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos(x + width, (y + height), zLevel).tex(((float)(textureX + width) * 0.00390625F), ((float)(textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((x + width), (y), zLevel).tex(((float)(textureX + width) * 0.00390625F), ((float)(textureY) * 0.00390625F)).endVertex();
        bufferbuilder.pos((x), (y), zLevel).tex(((float)(textureX) * 0.00390625F), ((float)(textureY) * 0.00390625F)).endVertex();
        tessellator.draw();
    }
}
