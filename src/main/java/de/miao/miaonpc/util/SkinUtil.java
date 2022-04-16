package de.miao.miaonpc.util;

import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkinUtil {

  private SkinUtil() {

  }

  public static CompletableFuture<Property> getSkin(UUID uuid) {
    try {
      var client = HttpClient.newHttpClient();
      var request = HttpRequest.newBuilder()
        .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"))
        .build();
      return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(stringHttpResponse -> {
          var property = JsonParser.parseString(stringHttpResponse.body()).getAsJsonObject().get("properties")
            .getAsJsonArray().get(0).getAsJsonObject();
          return new Property("textures", property.get("value").getAsString(), property.get("signature").getAsString());
        });
    } catch (Exception e) {
      System.out.println("Errooooooooor");
      return CompletableFuture.failedFuture(e);
    }
  }
}
