package com.greencat.antimony.common.function;

import com.greencat.Antimony;
import com.greencat.antimony.common.mixins.EntityPlayerSPAccessor;
import com.greencat.antimony.core.FunctionManager.FunctionManager;
import com.greencat.antimony.core.event.CustomEventHandler;
import com.greencat.antimony.utils.Utils;
import me.greencat.lwebus.core.annotation.EventModule;
import me.greencat.lwebus.core.reflectionless.ReflectionlessEventHandler;
import me.greencat.lwebus.core.type.Event;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

import static org.lwjgl.input.Keyboard.KEY_UP;

public class SynthesizerAura implements ReflectionlessEventHandler {
    static Boolean hasCharm = false;
    static Boolean hasAtom = false;
    double range = 2.9;
    public static long latest;
    public static long lastTrigger = 0L;
    public static EntityLivingBase entityTarget;
    static EntityLivingBase LastClickedEntity;
    public SynthesizerAura(){
        MinecraftForge.EVENT_BUS.register(this);
        CustomEventHandler.EVENT_BUS.register(this);
    }
    @EventModule
    public void onEnable(CustomEventHandler.FunctionEnableEvent event){
        if(event.function.getName().equals("SynthesizerAura")){
            if(Minecraft.getMinecraft().theWorld != null) {
                if (onInit()) {
                    event.setCanceled(true);
                }
            }
        }
    }
    @EventModule
    public void onSwitch(CustomEventHandler.FunctionSwitchEvent event){
        if(event.function.getName().equals("SynthesizerAura")){
            if(Minecraft.getMinecraft().theWorld != null) {
                if (event.status) {
                    if (onInit()) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
    public void MotionChangePreEvent(CustomEventHandler.MotionChangeEvent.Pre event){
        if(Minecraft.getMinecraft().theWorld != null) {
            if (FunctionManager.getStatus("SynthesizerAura")) {
                if (Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
                    if (StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().toLowerCase()).contains("charminizer") || StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().toLowerCase()).contains("atominizer")) {
                        entityTarget = getTarget();
                        if (entityTarget != null) {
                            float[] angles = Utils.getServerAngles(entityTarget);
                            event.yaw = ((EntityPlayerSPAccessor) Minecraft.getMinecraft().thePlayer).getLastReportedYaw() - MathHelper.wrapAngleTo180_float((float) Math.max(-180, Math.min((double) (((EntityPlayerSPAccessor) Minecraft.getMinecraft().thePlayer).getLastReportedYaw() - angles[0]), 180)));
                            event.pitch = ((EntityPlayerSPAccessor) Minecraft.getMinecraft().thePlayer).getLastReportedPitch() - MathHelper.wrapAngleTo180_float((float) Math.max(-90, Math.min((double) (((EntityPlayerSPAccessor) Minecraft.getMinecraft().thePlayer).getLastReportedPitch() - angles[1]), 90)));
                        }
                    } else {
                        entityTarget = null;
                    }
                }
            }
        }
    }
    public void MotionChangePostEvent(CustomEventHandler.MotionChangeEvent event) {
        if(System.currentTimeMillis() - lastTrigger < 250){
            return;
        }
        lastTrigger = System.currentTimeMillis();
        if(Minecraft.getMinecraft().theWorld != null) {
            if (FunctionManager.getStatus("SynthesizerAura")) {
                if (entityTarget != null) {
                    //utils.devLog("target:" + entityTarget);
                    //Minecraft.getMinecraft().playerController.interactWithEntitySendPacket(Minecraft.getMinecraft().thePlayer, entityTarget);
                    //Minecraft.getMinecraft().playerController.sendUseItem(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().thePlayer.getHeldItem());
                    //KeyBinding.setKeyBindState(Minecraft.getMinecraft().gameSettings.keyBindUseItem.getKeyCode(),true);
                    C02PacketUseEntity c02 = new C02PacketUseEntity(entityTarget, C02PacketUseEntity.Action.INTERACT);
                    Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(c02);
                    LastClickedEntity = entityTarget;
                }
            }
        }
    }
    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent event){
        if(Minecraft.getMinecraft().theWorld != null) {
            if (FunctionManager.getStatus("SynthesizerAura")) {
                if(Minecraft.getMinecraft().thePlayer.getHeldItem() != null) {
                    if (StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().toLowerCase()).contains("charminizer") || StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().toLowerCase()).contains("atominizer")) {
                        if (Minecraft.getMinecraft().gameSettings.keyBindAttack.isPressed()) {
                            try {
                                if (System.currentTimeMillis() - latest >= 0) {
                                    latest = System.currentTimeMillis();
                                    for (int i = 0; i < 8; ++i) {
                                        ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
                                        if (stack != null && (StringUtils.stripControlCodes(stack.getDisplayName().toLowerCase()).contains("aspect of the end") || StringUtils.stripControlCodes(stack.getDisplayName().toLowerCase()).contains("aspect of the void"))) {
                                            int currentSlot = Minecraft.getMinecraft().thePlayer.inventory.currentItem;
                                            Minecraft.getMinecraft().thePlayer.inventory.currentItem = i;
                                            Minecraft.getMinecraft().playerController.sendUseItem(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().theWorld, stack);
                                            Minecraft.getMinecraft().thePlayer.inventory.currentItem = currentSlot;
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
    private EntityLivingBase getTarget(){
        if ((!(Minecraft.getMinecraft().currentScreen instanceof GuiContainer) && Minecraft.getMinecraft().theWorld != null)) {
            double x = Minecraft.getMinecraft().thePlayer.posX;
            double y = Minecraft.getMinecraft().thePlayer.posY;
            double z = Minecraft.getMinecraft().thePlayer.posZ;
            List<EntityArmorStand> entities = Minecraft.getMinecraft().theWorld.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(x - (range*2 / 2d), y - (range*2 / 2d), z - (range*2 / 2d), x + (range*2 / 2d), y + (range*2 / 2d), z + (range*2 / 2d)), null);
            for(EntityArmorStand entity : entities){
                if(EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCustomNameTag()).contains("Exe") || EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCustomNameTag()).contains("Wai") || EnumChatFormatting.getTextWithoutFormattingCodes(entity.getCustomNameTag()).contains("Zee")){
                    if((LastClickedEntity == null) || (LastClickedEntity != entity && StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().toLowerCase()).contains("charminizer")) || StringUtils.stripControlCodes(Minecraft.getMinecraft().thePlayer.getHeldItem().getDisplayName().toLowerCase()).contains("atominizer")){
                        return entity;
                    }
                }
            }
        }
        return null;
    }
    private boolean onInit(){
        checkItem();
        if(!hasCharm && !hasAtom){
            Utils.print("无法在物品栏找到Charminizer或Atominizer");
            return true;
        }
       Utils.print("手持Charminizer或Atominizer左键来使用Aspect of the End/Void传送功能");
        entityTarget = null;
        LastClickedEntity = null;
        return false;
    }
    private void checkItem(){
        boolean charm = false;
        boolean atom = false;
        for (int i = 0; i < 8; ++i) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
            if (stack != null && StringUtils.stripControlCodes(stack.getDisplayName().toLowerCase()).contains("charminizer")) {
                charm = true;
                break;
            }
        }
        hasCharm = charm;
        for (int i = 0; i < 8; ++i) {
            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.mainInventory[i];
            if (stack != null && StringUtils.stripControlCodes(stack.getDisplayName().toLowerCase()).contains("atominizer")) {
                atom = true;
                break;
            }
        }
        hasAtom = atom;
    }

    @Override
    public void invoke(Event event) {
        if(event instanceof CustomEventHandler.MotionChangeEvent){
            MotionChangePostEvent((CustomEventHandler.MotionChangeEvent) event);
        }
        if(event instanceof CustomEventHandler.MotionChangeEvent.Pre){
            MotionChangePreEvent((CustomEventHandler.MotionChangeEvent.Pre) event);
        }
    }
}
