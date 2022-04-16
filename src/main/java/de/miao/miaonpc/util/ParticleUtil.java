package de.miao.miaonpc.util;

import de.miao.miaonpc.MiaoMain;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Objects;

public class ParticleUtil {

  private static final Particle.DustOptions defaultOptions = new Particle.DustOptions(Color.fromRGB(250, 10, 30), 1);

  private ParticleUtil() {
  }

  public static void createSphere(Location location, Particle particle, @Nullable Particle.DustOptions options, double radius) {
    for (double i = 0; i <= Math.PI * radius; i += Math.PI / 10) {
      double r = Math.sin(i) * radius;
      double y = Math.cos(i) * radius;
      for (double a = 0; a < Math.PI * radius; a += Math.PI / 10) {
        double x = Math.cos(a) * r;
        double z = Math.sin(a) * r;
        location.add(x, y, z);
        location.getWorld().spawnParticle(particle, location, 2, Objects.requireNonNullElse(options,
          defaultOptions));
        location.subtract(x, y, z);
      }
    }
  }

  public static void createCircle(Location location, Particle particle, @Nullable Particle.DustOptions options, double radius) {
    for (int i = 0; i < 365; i++) {
      var x = radius * Math.cos(i);
      var y = radius * Math.sin(i);

      location.getWorld().spawnParticle(particle, location, 1, Objects.requireNonNullElseGet(options, () -> defaultOptions));
    }
  }

  public static void createHelix(Location location, Particle particle, @Nullable Particle.DustOptions options, double radius, double height) {
    //TODO: code for creating a helix
  }

  public static void createRotatingCircle(Location location, Particle particle, @Nullable Particle.DustOptions options, Plugin plugin) {
    new BukkitRunnable() {

      double t = 0;
      double r = 2;

      double xangle = Math.toRadians(90); // note that here we do have to convert to radians.
      double xAxisCos = Math.cos(xangle); // getting the cos value for the pitch.
      double xAxisSin = Math.sin(xangle); // getting the sin value for the pitch.

      public void run() {
        t = t + Math.PI / 32;
        double x = r * Math.cos(t);
        double y = 0;
        double z = r * Math.sin(t);
        var v = new Vector(x, 0, z);
        v = ParticleUtil.rotateAroundAxisY(v, xAxisCos, xAxisSin);
        location.add(v.getX(), v.getY(), v.getZ());

        location.getWorld().spawnParticle(particle, location, 1, Objects.requireNonNullElseGet(options, () -> defaultOptions));

        location.subtract(v.getX(), v.getY(), v.getZ());
        if (t > Math.PI * 4) {
          this.cancel();
        }
      }

    }.runTaskTimer(plugin, 0, 1);
  }

  private static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
    double y = v.getY() * cos - v.getZ() * sin;
    double z = v.getY() * sin + v.getZ() * cos;
    return v.setY(y).setZ(z);
  }

  private static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
    double x = v.getX() * cos + v.getZ() * sin;
    double z = v.getX() * -sin + v.getZ() * cos;
    return v.setX(x).setZ(z);
  }

  private static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
    double x = v.getX() * cos - v.getY() * sin;
    double y = v.getX() * sin + v.getY() * cos;
    return v.setX(x).setY(y);
  }
}
