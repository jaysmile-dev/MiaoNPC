package de.miao.miaonpc.npc;

public enum NPCType {

  KNIGHT("Knight"), SAURON("Sauron");

  private final String type;

  NPCType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }
}
