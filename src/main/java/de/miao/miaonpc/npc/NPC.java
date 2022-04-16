package de.miao.miaonpc.npc;

import com.mojang.authlib.properties.Property;
import de.miao.miaonpc.util.NPCUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public abstract class NPC extends Villager {

  private final NPCType type;

  /**
   * constructor used for getting a npc
   */
  protected NPC(NPCType type, Level level, UUID uuid, int entityId) {
    super(EntityType.VILLAGER, level);
    this.stopTrading();
    this.type = type;
    this.uuid = uuid;
    this.setId(entityId);
  }

  /**
   * constructor used for creating a npc
   */
  protected NPC(NPCType type, Level level) {
    super(EntityType.VILLAGER, level);
    this.type = type;
  }



  public Property getSkin(FileConfiguration config) {
    return NPCUtil.getNPCSkin(type, config);
  }

  public abstract void addGoals();

  /**
   * Called when the NPC is loaded for the player
   */

  public abstract void onLoad();

  /**
   * Called while the PlayerJoinEvent
   */
  public abstract void onJoin();

  /**
   * Called while the PlayerQuitEvent
   */
  public abstract void onLeave();
  /**
   * Called when the NPC attacks an entity
   *
   * @param attackedEntity the entity being attacked by the npc
   */
  public abstract void onAttack(Entity attackedEntity);

  /**
   *
   * Called when a player interacts with the npc
   * @param event
   */

  public abstract void onInteract(PlayerInteractAtEntityEvent event);
  /**
   * Called when the NPC is getting hurt
   */
  public abstract void onDamage(Entity damager);

  /**
   * Spawning the npc as an entity
   */
  public abstract void spawn(Location location);

  public NPCType getNPCType() {
    return type;
  }
}
