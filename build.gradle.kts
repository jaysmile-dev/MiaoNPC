import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.3.5"
  id("xyz.jpenilla.run-paper") version "1.0.6"
  id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
}

group = "io.papermc.paperweight"
version = "1.1.3"
description = "NPC Plugin"

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
repositories {
  maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {

  paperDevBundle("1.18.2-R0.1-SNAPSHOT")

}

tasks {
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()

    options.release.set(17)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name()

    from(sourceSets.main.get().resources.srcDirs) {
      filesMatching("plugin.yml") {
        expand(
          "name" to rootProject.name,
          "version" to version
        )
      }
      duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

  }

}

bukkit {
  load = BukkitPluginDescription.PluginLoadOrder.STARTUP
  main = "de.miao.miaonpc.MiaoMain"
  apiVersion = "1.18"
  authors = listOf("Author")
  commands {

    register("npc") {
      description = "spawn a npc";
    }
    register("setnpcskin") {
      description = "set the skin for a specific npc type"
    }
  }
}
