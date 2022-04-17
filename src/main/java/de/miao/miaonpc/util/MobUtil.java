package de.miao.miaonpc.util;

import org.bukkit.Particle;
import org.bukkit.entity.*;

public class MobUtil {
  private MobUtil() {
  }

  public static void preventArrowHitInRadius(double radius, Mob mob) {
    var mobLocation = mob.getEyeLocation();
    mobLocation.setY(mobLocation.getY() - 1);
    for (var entity : mob.getChunk().getEntities())
      if (entity.getType() == EntityType.SPECTRAL_ARROW || entity.getType() == EntityType.ARROW && (mobLocation.distance(entity.getLocation()) <= radius || mobLocation.distance(entity.getLocation()) == 0
        || mobLocation.distance(((Entity) ((Arrow) entity).getShooter()).getLocation()) <= radius)) {
        if (!((Arrow) entity).isInBlock()) {
          entity.remove();
          var location = mob.getEyeLocation();
          location.setY(location.getY() - 1);
          ParticleUtil.createSphere(location, Particle.REDSTONE, null, radius / 2);
        }
      }
  }


}
