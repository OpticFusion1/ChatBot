/*
* Copyright (C) 2021 Optic_Fusion1
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package optic_fusion1.chatbot.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import optic_fusion1.chatbot.ChatBot;
import static optic_fusion1.chatbot.ChatBot.INSTANCE;
import optic_fusion1.chatbot.bot.responses.CommandResponse;
import optic_fusion1.chatbot.bot.translate.TranslateResponse;
import optic_fusion1.chatbot.events.BotResponseEvent;
import optic_fusion1.chatbot.utils.FileUtils;
import optic_fusion1.chatbot.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

// TODO: Update example config to take into account all current features
public class Bot {

  private final Map<String, String> regexes = new HashMap<>();
  private final File file;
  private final File regexFile;
  private FileConfiguration config;
  private String name;
  private String prefix;
  private int responseSpeed;
  private boolean isDefault;
  private List<String> aliases = new ArrayList<>();

  public Bot(File file) {
    this.file = file;
    config = YamlConfiguration.loadConfiguration(file);
    name = config.getString("name");
    prefix = config.getString("prefix");
    responseSpeed = config.getInt("response-speed");
    isDefault = config.getBoolean("default");
    regexFile = new File("plugins/ChatBot/bots", (isDefault ? "default" : name) + "-regexes.txt");
    if (!config.isSet("aliases")) {
      config.set("aliases", new ArrayList<>());
    }
    aliases = config.getStringList("aliases");
    FileUtils.saveResourceIfNonExistant(regexFile.getAbsolutePath(), "regexes.txt");
    loadRegexes();
  }

  private void loadRegexes() {
    if (!regexFile.exists()) {
      return;
    }
    if (!regexes.isEmpty()) {
      regexes.clear();
    }
    try {
      BufferedReader bufferedReader = new BufferedReader(new FileReader(regexFile));
      for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
        if (!line.startsWith("#")) {
          String[] args = line.split(": ");
          regexes.putIfAbsent(args[0], args[1]);
        }
      }
    } catch (FileNotFoundException ex) {
      Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void reload() {
    config = (FileConfiguration) YamlConfiguration.loadConfiguration(file);
    name = config.getString("name");
    prefix = config.getString("prefix");
    responseSpeed = config.getInt("response-speed");
    isDefault = config.getBoolean("default");
    loadRegexes();
  }

  public String translate(CommandSender sender, String originalMessage, String... playerMessages) {
    return TranslateResponse.parseResponse(this, sender, originalMessage);
  }

  public void sendTimedBroadcast(Player player, String message, boolean silent, String... playerMessages) {
    String m = silent ? message.substring(0, message.length() - 2).trim() : message.trim();
    if (m.isEmpty()) {
      return;
    }
    BotResponseEvent event = new BotResponseEvent(this, m);
    Bukkit.getScheduler().runTask(INSTANCE, () -> {
      Bukkit.getPluginManager().callEvent(event);
    });
    if (event.isCancelled()) {
      return;
    }
    Bukkit.getScheduler().scheduleSyncDelayedTask(ChatBot.INSTANCE, () -> {
      ComponentBuilder componentBuilder = new ComponentBuilder();
      if (Utils.isJSONValid(m)) {
        try {
          componentBuilder.append(new TextComponent(Utils.colorize(prefix + " ")));
          componentBuilder.append(ComponentSerializer.parse(Utils.colorize(translate(player, m, playerMessages))));
        } catch (Exception e) {
          componentBuilder = new ComponentBuilder();
        }
      }
      if (m.contains("\\n")) {
        String[] args = m.split("\n");
        for (String arg : args) {
          if (silent) {
            player.sendMessage(Utils.colorize(translate(player, prefix + " " + arg, playerMessages)));
            continue;
          }
          Bukkit.broadcastMessage(Utils.colorize(translate(player, prefix + " " + arg, playerMessages)));
        }
        return;
      }
      if (silent) {
        if (!componentBuilder.getParts().isEmpty()) {
          player.spigot().sendMessage(componentBuilder.create());
          return;
        }
        player.sendMessage(Utils.colorize(translate(player, prefix + " " + m, playerMessages)));
        return;
      }
      if (!componentBuilder.getParts().isEmpty()) {
        Bukkit.spigot().broadcast(componentBuilder.create());
        return;
      }
      Bukkit.broadcastMessage(Utils.colorize(translate(player, prefix + " " + m, playerMessages)));
    }, responseSpeed);
  }

  public void processEventResponse(Player player, String message, boolean getRandomResponse, Event event) {
    String response = getRandomResponse ? getRandomResponse(message.toLowerCase()) : message.toLowerCase();
    new CommandResponse(TranslateResponse.parseResponse(this, player, response, event)).execute(this, Bukkit.getScheduler(), player, message);
  }

  public void processResponse(Player player, String message, boolean getRandomResponse) {
    String response = getRandomResponse ? getRandomResponse(message.toLowerCase()) : message;
    new CommandResponse(response).execute(this, Bukkit.getScheduler(), player, message);
  }

  public List<String> match(String text, String regex) {
    List<String> matches = new ArrayList<>();
    Matcher matcher = Pattern.compile(regex).matcher(text);
    while (matcher.find()) {
      matches.add(matcher.group());
    }
    return matches;
  }

  public void sendRegexResponse(Player player, String message) {
    if (regexes.isEmpty()) {
      return;
    }
    regexes.keySet().forEach(regexKey -> {
      Matcher matcher = Pattern.compile(regexKey).matcher(message);
      if (matcher.find()) {
        processResponse(player, regexes.get(regexKey), false);
      }
    });
  }

  public boolean hasRegexResponse(String message) {
    return !regexes.isEmpty() && regexes.keySet().stream().anyMatch(regex -> !match(message, regex).isEmpty());
  }

  public boolean hasResponse(String message) {
    if (!config.contains(message)) {
      return false;
    }
    if (config.isList(message)) {
      return !config.getStringList(message).isEmpty();
    }
    return config.getString(message) != null && !config.getString(message).isEmpty();
  }

  public void addMiscResponse(String trigger, String response, CommandSender sender) {
    List<String> currentResponses = config.getStringList("miscellaneous." + trigger);
    currentResponses.add(response);
    config.set("miscellaneous." + trigger, currentResponses);
    try {
      config.save(file);
    } catch (IOException ex) {
      ChatBot.INSTANCE.getLogger().log(Level.WARNING,
              "Couldn''''t add miscellaneous response miscellaneous.{0}.{1} to bot {2}",
              new Object[]{trigger, response, file});
      return;
    }
    reload();
  }

  /**
   * Checks if the given message contains only the bot name. If it does then it return true, otherwise false
   *
   * @param message Message to check
   * @return True if message contains only the bot name, otherwise false
   */
  public boolean isBotNameOnly(String message) {
    return message.equals(name) || aliases.contains(message);
  }

  public String getRandomResponse(String string) {
    final List<String> responseList = config.getStringList(string);
    if (responseList.isEmpty()) {
      return "not-found";
    }
    return responseList.get(ThreadLocalRandom.current().nextInt(responseList.size()));
  }

  /**
   * Gets the bot's aliases,
   *
   * @return Bot aliases
   */
  public List<String> getAliases() {
    return aliases;
  }

  /**
   * Gets the bot's name
   *
   * @return Bot name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the bot's prefix
   *
   * @return Bot prefix
   */
  public String getPrefix() {
    return prefix;
  }

  /**
   * Gets the bot's response speed in ticks
   *
   * @return Bot response speed
   */
  public int getResponseSpeed() {
    return responseSpeed;
  }

  /**
   * Gets if the bot is the default bot used handling responses
   *
   * @return Is default
   */
  public boolean isDefault() {
    return isDefault;
  }

}
