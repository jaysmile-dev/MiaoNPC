package de.miao.miaonpc.npc.custom;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import de.miao.miaonpc.builder.ItemBuilder;
import de.miao.miaonpc.npc.NPC;
import de.miao.miaonpc.npc.NPCType;
import de.miao.miaonpc.util.MobUtil;
import de.miao.miaonpc.util.ParticleUtil;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.*;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftVillager;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.UUID;

public class Knight extends NPC {

  private Plugin plugin;

  public Knight(Level level, UUID uuid, Plugin plugin, int entityId) {
    super(NPCType.KNIGHT, level, uuid, entityId);
    this.plugin = plugin;
    addGoals();
  }

  public Knight(Level level, Plugin plugin) {
    super(NPCType.KNIGHT, level);
    this.plugin = plugin;
    this.setItemInHand(InteractionHand.MAIN_HAND,
      ItemStack.fromBukkitCopy(new ItemBuilder(Material.IRON_SWORD)
        .setDamage((short) 10)
        .setName("§7Knight´s Sword").build()));
  }


  @Override
  public void onLoad() {

    addGoals();
  }

  @Override
  public void onJoin() {

  }

  @Override
  public void onLeave() {

  }

  @Override
  public void onAttack(Entity entity) {

    ParticleUtil.createRotatingCircle(entity.getLocation(), Particle.REDSTONE, null, plugin);

  }

  @Override
  public void onInteract(PlayerInteractAtEntityEvent event) {

  }

  @Override
  public void tick() {
    super.tick();
    var mob = (Mob) Bukkit.getEntity(uuid);
    if (mob == null) return;
    MobUtil.preventArrowHitInRadius(4, mob);
  }

  @Override
  public void onDamage(Entity damager) {
    var villager = (Villager) Bukkit.getEntity(uuid);
    if (villager == null)
      return;
    if (damager instanceof LivingEntity) {


      if (damager instanceof Player player)
        if (player.getGameMode() == GameMode.CREATIVE && player.getGameMode() == GameMode.SPECTATOR) return;
      villager.setTarget((LivingEntity) damager);
      Bukkit.getMobGoals().removeAllGoals(villager);
      addGoals();
    }

    if (damager instanceof Projectile) {
      if (((Projectile) damager).getShooter() instanceof LivingEntity entity)
        villager.setTarget(entity);

    }

  }

  @Override
  public void setUnhappy() {

  }

  @Override
  public void playAmbientSound() {

  }

  @Override
  public void playHurtSound(@NotNull DamageSource damageSource) {
    playSound(SoundEvents.ILLUSIONER_HURT, 10, 10);
  }

  @Override
  public void playWorkSound() {

  }

  @Override
  public void playCelebrateSound() {

  }

  @Override
  public SoundEvent getNotifyTradeSound() {
    return null;
  }

  private static Pair<Integer, Behavior<net.minecraft.world.entity.LivingEntity>> getFullLookBehavior() {
    return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(new SetEntityLookTarget(net.minecraft.world.entity.EntityType.CAT, 8.0F), 8), Pair.of(new SetEntityLookTarget(net.minecraft.world.entity.EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(net.minecraft.world.entity.EntityType.PLAYER, 8.0F), 2), Pair.of(new SetEntityLookTarget(MobCategory.CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.AXOLOTLS, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.UNDERGROUND_WATER_CREATURE, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.WATER_AMBIENT, 8.0F), 1), Pair.of(new SetEntityLookTarget(MobCategory.MONSTER, 8.0F), 1), Pair.of(new DoNothing(30, 60), 2))));
  }

  private static Pair<Integer, Behavior<net.minecraft.world.entity.LivingEntity>> getMinimalLookBehavior() {
    return Pair.of(5, new RunOne<>(ImmutableList.of(Pair.of(new SetEntityLookTarget(net.minecraft.world.entity.EntityType.VILLAGER, 8.0F), 2), Pair.of(new SetEntityLookTarget(net.minecraft.world.entity.EntityType.PLAYER, 8.0F), 2), Pair.of(new DoNothing(30, 60), 8))));
  }

  private Brain<net.minecraft.world.entity.npc.Villager> getThisBrain() {
    var villager = (Villager) Bukkit.getEntity(uuid);
    var thisBrain = ((CraftVillager) villager).getHandle().getBrain();


    VillagerProfession villagerprofession = this.getVillagerData().getProfession();

    if (this.isBaby()) {
      thisBrain.setSchedule(Schedule.VILLAGER_BABY);
      thisBrain.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5F));
    } else {
      thisBrain.setSchedule(Schedule.VILLAGER_DEFAULT);
      thisBrain.addActivityWithConditions(Activity.WORK, VillagerGoalPackages.getWorkPackage(villagerprofession, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
    }

    thisBrain.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(villagerprofession, 0.5F));
    thisBrain.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(villagerprofession, 0.5F));
    thisBrain.addActivityWithConditions(Activity.MEET, VillagerGoalPackages.getMeetPackage(VillagerProfession.NONE, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
    thisBrain.addActivity(Activity.IDLE, ImmutableList.of(Pair.of(2, new RunOne<>(ImmutableList.of(Pair.of(new VillageBoundRandomStroll(0.5F), 1), Pair.of(new SetWalkTargetFromLookTarget(0.5F, 2), 1), Pair.of(new DoNothing(30, 60), 1)))), Pair.of(3, new SetLookAndInteract(net.minecraft.world.entity.EntityType.PLAYER, 4)), Pair.of(3, new GateBehavior<>(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), 1)))), getFullLookBehavior(), Pair.of(99, new UpdateActivityFromSchedule())));
    thisBrain.addActivity(Activity.PANIC, ImmutableList.of(Pair.of(0, new VillagerCalmDown()), Pair.of(3, new VillageBoundRandomStroll(0.5F, 2, 2)), getMinimalLookBehavior()));
    thisBrain.setCoreActivities(ImmutableSet.of(Activity.CORE));
    thisBrain.setDefaultActivity(Activity.IDLE);
    thisBrain.setActiveActivityIfPossible(Activity.IDLE);
    thisBrain.updateActivityFromSchedule(this.level.getDayTime(), this.level.getGameTime());
    return thisBrain;
  }

  @Override

  public void addGoals() {
    var villager = (Villager) Bukkit.getEntity(uuid);

    ((CraftVillager) villager).getHandle().getBrain().removeAllBehaviors();


    brain = getThisBrain();
    villager.registerAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
    villager.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(10);
    Bukkit.getMobGoals().addGoal(villager, 0, new RandomStrollGoal(this, 0.5F).asPaperVanillaGoal());
    Bukkit.getScheduler().runTask(plugin, task -> {
      Bukkit.getMobGoals().addGoal(villager, 1, new Goal<Mob>() {

        @Override
        public boolean shouldActivate() {
          if (villager.getTarget() == null) {
            return false;
          }
          if (villager.getTarget().isDead()) {
            return false;
          }
          return true;
        }

        @Override
        public void tick() {

          var target = villager.getTarget();
          if (target == null) {
            return;
          }
          if (target.isDead() || target.getHealth() <= 0) {
            villager.setTarget(null);
            return;
          }

          if (target instanceof Player player)
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
              villager.setTarget(null);
            }


          villager.lookAt(target);
          if (villager.getLocation().distanceSquared(target.getLocation()) < 20)
            villager.getPathfinder().moveTo(target);
          if (villager.getLocation().distanceSquared(target.getLocation()) < 3)
            villager.attack(target);

          if (target.isDead() || target.getHealth() <= 0) {
            villager.setTarget(null);
            return;
          }
          brain = getThisBrain();
        }

        @Override
        public @NotNull GoalKey<Mob> getKey() {
          return GoalKey.of(Mob.class, new NamespacedKey(plugin, "goal"));
        }

        @Override
        public @NotNull EnumSet<GoalType> getTypes() {
          return EnumSet.of(GoalType.TARGET, GoalType.MOVE);
        }

      });
    });

  }

  @Override
  public void spawn(Location location) {
    this.setPos(location.getX(), location.getY(), location.getZ());
    this.setCustomName(new TextComponent("§8Knight"));
    this.setCustomNameVisible(true);
    this.setHealth(50);
    this.setCanPickUpLoot(true);

    level.addFreshEntity(this);

  }
}
