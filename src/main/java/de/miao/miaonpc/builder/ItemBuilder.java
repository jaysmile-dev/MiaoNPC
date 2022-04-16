package de.miao.miaonpc.builder;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemBuilder {

  private Material material;
  private String name;
  private short damage;
  private int amount;

  public ItemBuilder(Material material) {
    this.material = material;
  }

  public ItemBuilder setName(String name) {
    this.name = name;
    return this;
  }
  public ItemBuilder setDamage(short damage) {
    this.damage = damage;
    return this;
  }

  public ItemBuilder setAmount(int amount) {
    this.amount = amount;
    return this;
  }

  public ItemStack build() {
      var itemStack = new ItemStack(material);
      return itemStack;
  }

}
