package de.miao.miaonpc.npc.custom;

import de.miao.miaonpc.npc.NPC;
import de.miao.miaonpc.npc.NPCType;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class Sauron extends NPC {


  public Sauron(Level level, UUID uuid, int entityId) {
    super(NPCType.SAURON, level, uuid, entityId);
  }

  public Sauron(Level level) {
    super(NPCType.SAURON, level);
  }

  @Override
  public void spawn(Location location) {
    this.setPos(location.getX(), location.getY(), location.getZ());
    this.setHealth(10000);
    this.setAggressive(true);
    this.setCanPickUpLoot(true);
    level.addFreshEntity(this);

  }

  @Override
  public void playAmbientSound() {

  }
}
