package de.miao.miaonpc.util;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;

import javax.annotation.Nullable;

public class BufferUtil {

  public static void writeOnRemove(FriendlyByteBuf buf, ClientboundPlayerInfoPacket.PlayerUpdate entry) {
    buf.writeUUID(entry.getProfile().getId());
  }

  public static void writeOnAdd(FriendlyByteBuf buf, ClientboundPlayerInfoPacket.PlayerUpdate entry) {
    buf.writeUUID(entry.getProfile().getId());
    buf.writeUtf(entry.getProfile().getName());
    buf.writeCollection(entry.getProfile().getProperties().values(), (bufx, property) -> {
      bufx.writeUtf(property.getName());
      bufx.writeUtf(property.getValue());
      if (property.hasSignature()) {
        bufx.writeBoolean(true);
        bufx.writeUtf(property.getSignature());
      } else {
        bufx.writeBoolean(false);
      }

    });
    buf.writeVarInt(entry.getGameMode().getId());
    buf.writeVarInt(entry.getLatency());
    writeDisplayName(buf, entry.getDisplayName());
  }

  public static void writeDisplayName(FriendlyByteBuf buf, @Nullable Component text) {
    if (text == null) {
      buf.writeBoolean(false);
    } else {
      buf.writeBoolean(true);
      buf.writeComponent(text);
    }
  }
}
