package brstudio.godzin.shadowindicator.event;

import brstudio.godzin.shadowindicator.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class Events {
    private static Entity lastEntityLookedAt = null;
    private static final double MAX_DISTANCE = ModConfig.getMaxDistance();
    private static final boolean DISPLAY_ENTITY_NAME = ModConfig.displayEntityName();

    private static final Map<EntityLivingBase, Float> damageMap = new HashMap<>();
    private static final Map<EntityLivingBase, Integer> damageTimerMap = new HashMap<>();
    private static final int DISPLAY_DURATION = 100; // Duração em ticks (5 segundos)

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.world.isRemote) {
            return;
        }

        EntityPlayer player = event.player;
        double maxDistance = MAX_DISTANCE;
        Vec3d startPos = player.getPositionEyes(1.0F);
        Vec3d lookVec = player.getLook(1.0F);
        Vec3d endPos = startPos.add(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance);

        List<Entity> entitiesInRange = player.world.getEntitiesWithinAABB(Entity.class, player.getEntityBoundingBox().expand(lookVec.x * maxDistance, lookVec.y * maxDistance, lookVec.z * maxDistance));

        Entity entityLookedAt = null;
        double closestDistance = maxDistance;

        for (Entity entity : entitiesInRange) {
            if (entity != player) {
                double distanceToEntity = startPos.distanceTo(entity.getPositionVector());

                if (distanceToEntity < closestDistance) {
                    closestDistance = distanceToEntity;
                    entityLookedAt = entity;
                }
            }
        }

        lastEntityLookedAt = entityLookedAt;

        for (EntityLivingBase entity : damageTimerMap.keySet()) {
            int timeLeft = damageTimerMap.get(entity) - 1;
            if (timeLeft <= 0) {
                damageTimerMap.remove(entity);
                damageMap.remove(entity);
            } else {
                damageTimerMap.put(entity, timeLeft);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityLivingBase entity = event.getEntityLiving();
            float damage = event.getAmount();

            damageMap.put(entity, damageMap.getOrDefault(entity, 0f) + damage);
            damageTimerMap.put(entity, DISPLAY_DURATION);
        }
    }

    private static int getHealthBarColor(float healthRatio) {
        int red = (int) (255 * (1 - healthRatio));
        int green = (int) (255 * healthRatio);
        return new Color(red, green, 0).getRGB();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if (!player.equals(mc.player)) {
            return;
        }

        EntityLivingBase entityToRender = null;

        if (mc.pointedEntity instanceof EntityLivingBase && !mc.pointedEntity.equals(player)) {
            entityToRender = (EntityLivingBase) mc.pointedEntity;
        } else {
            return;
        }

        if (entityToRender != null) {
            int margin = 10;
            int textColor = 0xFFFFFF;

            String entityName = DISPLAY_ENTITY_NAME ? entityToRender.getName() : "";
            String healthText = "";

            float currentHealth = entityToRender.getHealth();
            float maxHealth = entityToRender.getMaxHealth();
            healthText = String.format("❤ %.1f / %.1f", currentHealth, maxHealth);

            int textHeight = mc.fontRenderer.FONT_HEIGHT;
            int boxWidth = Math.max(mc.fontRenderer.getStringWidth(entityName), mc.fontRenderer.getStringWidth(healthText)) + 20;
            int boxHeight = textHeight * (DISPLAY_ENTITY_NAME ? 2 : 1) + 30;

            if (DISPLAY_ENTITY_NAME) {
                mc.fontRenderer.drawStringWithShadow(entityName, margin + 10, margin + 5, textColor);
            }

            mc.fontRenderer.drawStringWithShadow(healthText, margin + 10, margin + (DISPLAY_ENTITY_NAME ? 20 : 5), textColor);

            if (maxHealth > 0) {
                int healthBarWidth = boxWidth - 20;
                int healthBarHeight = 5;

                mc.ingameGUI.drawRect(margin + 10, margin + boxHeight - 20, margin + 10 + healthBarWidth, margin + boxHeight - 20 + healthBarHeight, 0xFFAAAAAA);

                float healthRatio = currentHealth / maxHealth;
                int currentHealthBarWidth = (int) (healthBarWidth * healthRatio);
                if (currentHealthBarWidth > 0) {
                    int healthBarColor = getHealthBarColor(healthRatio);
                    mc.ingameGUI.drawRect(margin + 10, margin + boxHeight - 20, margin + 10 + currentHealthBarWidth, margin + boxHeight - 20 + healthBarHeight, healthBarColor);
                }

                // Mostrar dano acumulado, se houver
                if (damageMap.containsKey(entityToRender)) {
                    float accumulatedDamage = damageMap.get(entityToRender);
                    String damageText = String.format("⚔ %.1f", accumulatedDamage);

                    mc.fontRenderer.drawStringWithShadow(damageText, margin + boxWidth + 10, margin + (DISPLAY_ENTITY_NAME ? 20 : 5), 0xFF5555);
                }
            }
        }
    }
}