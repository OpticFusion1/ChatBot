package optic_fusion1.chatbot.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.utils.FileUtils;
import optic_fusion1.chatbot.utils.JSONUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;

//TODO: Add json text component support
//TODO: Update example config to take into account all current features
public class Bot {

  private static final Random RANDOM = new Random();
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");
  private static final Pattern COMMAND_PATTERN = Pattern.compile("\\[cmd type\\=(.+?)\\](.*?)\\[\\/cmd\\]");
  private static final Pattern PERMISSION_PATTERN = Pattern.compile("\\[perm\\=(.+?)\\](.*?)\\[\\/perm\\]");
  private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();
  private final HashMap<String, String> regexes = new HashMap<>();
  private final File file;
  private final File regexFile;
  private FileConfiguration config;
  private String name;
  private String prefix;
  private int responseSpeed;
  private boolean isDefault;
  private List<String> aliases;

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

  public List<String> getAliases() {
    return aliases;
  }

  public String getName() {
    return name;
  }

  public String getPrefix() {
    return prefix;
  }

  public int getResponseSpeed() {
    return responseSpeed;
  }

  public boolean isDefault() {
    return isDefault;
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
    String translatedString = originalMessage;
    if (sender instanceof Player) {
      Player player = (Player) sender;
      if (ChatBot.usePlaceholderAPI) {
        translatedString = PlaceholderAPI.setBracketPlaceholders(player, translatedString);
        translatedString = PlaceholderAPI.setPlaceholders(player, translatedString);
      }
      if (ChatBot.useMVDWPlaceholderAPI) {
        translatedString = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, translatedString);
      }
      translatedString = translatePlayerPlaceholders(player, translatedString);
    }
    translatedString = translateBotPlaceholders(translatedString, playerMessages);
    translatedString = translateRandomPlaceholders(translatedString);
    translatedString = ChatColor.translateAlternateColorCodes('&', translatedString);
    return translatedString;
  }

  public String translateFilePlaceholders(File file, String text) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "file_name": {
          text = text.replaceAll("%file_name%", file.getName());
        }
      }
    }
    return text;
  }

  public String translateRandomPlaceholders(String text) {
    Matcher placeholderMatcher = PLACEHOLDER_PATTERN.matcher(text);
    while (placeholderMatcher.find()) {
      String group = placeholderMatcher.group(0);
      switch (group) {
        case "random_int": {
          text = text.replaceAll("%random_int%", String.valueOf(RANDOM.nextInt(Integer.MAX_VALUE) + 1));
        }
      }
    }
    return text;
  }

  public String translateBotPlaceholders(String text, String... playerMessages) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    while (matcher.find()) {
      String group = matcher.group(1);
      if (group.contains("bot_name")) {
        text = text.replaceAll("%bot_name%", name);
      } else if (group.contains("bot_name_lowercase")) {
        text = text.replaceAll("%bot_name_lowercase%", name.toLowerCase());
      } else if (group.contains("bot_name_uppercase")) {
        text = text.replaceAll("%bot_name_uppercase%", name.toUpperCase());
      } else if (group.contains("bot_prefix")) {
        text = text.replaceAll("%bot_prefix%", prefix);
      } else if (group.contains("%bot_prefix_lowercase%")) {
        text = text.replaceAll("%bot_prefix_lowercase%", prefix.toLowerCase());
      } else if (group.contains("%bot_prefix_uppercase%")) {
        text = text.replaceAll("%bot_prefix_uppercase%", prefix.toUpperCase());
      } else if (group.contains("%response_speed%")) {
        text = text.replaceAll("%response_speed%", String.valueOf(responseSpeed));
      } else if (group.contains("arg-")) {
        if (playerMessages.length == 0) {
          continue;
        }
        String playerMessage = playerMessages[0];
        if (playerMessage.isEmpty()) {
          continue;
        }
        String[] args = playerMessage.split(" ");
        int arg = Integer.parseInt(group.replace("arg-", ""));
        String found = "not-found";
        try {
          found = args[arg];
        } catch (Exception e) {

        }
        text = text.replaceAll("%arg-" + arg + "%", found);
      }
    }
    return text;
  }

  public String translatePlayerPlaceholders(Player player, String text) {
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "player_name": {
          text = text.replaceAll("%player_name%", player.getName());
          continue;
        }
        case "player_name_lowercase": {
          text = text.replaceAll("%player_name_lowercase%", player.getName().toLowerCase());
          continue;
        }
        case "player_name_uppercase": {
          text = text.replaceAll("%player_name_uppercase%", player.getName().toUpperCase());
          continue;
        }
        case "player_displayname": {
          text = text.replaceAll("%player_displayname%", player.getDisplayName());
          continue;
        }
        case "player_displayname_lowercase": {
          text = text.replaceAll("%player_displayname_lowercase%", player.getDisplayName().toLowerCase());
          continue;
        }
        case "player_displayname_uppercase": {
          text = text.replaceAll("%player_displayname_uppercase%", player.getDisplayName().toUpperCase());
        }

      }
    }
    return text;
  }

  public String translatePlayerDeathEvent(PlayerDeathEvent event, Player player, String message) {
    String finalString = message;
    Entity killed = (Entity) event.getEntity();
    Entity killer = (Entity) event.getEntity().getKiller();
    finalString = finalString.replaceAll("%dropped_xp_amount%", String.valueOf(event.getDroppedExp()));
    finalString = finalString.replaceAll("%drop_amount%", String.valueOf(event.getDrops().size()));
    finalString = finalString.replaceAll("%killed_name", getEntityName(killed));
    if (killer != null) {
      finalString = finalString.replaceAll("%killer_name%", getEntityName(killer));
    }
    return translate(player, finalString);
  }

  public String translateEntityDeathEvent(EntityDeathEvent event, Player player, String message) {
    String finalString = message;
    Entity killed = (Entity) event.getEntity();
    Entity killer = (Entity) event.getEntity().getKiller();
    finalString = finalString.replaceAll("%killed_name%", getEntityName(killed));
    if (killer != null) {
      finalString = finalString.replaceAll("%killer_name%", getEntityName(killer));
    }
    return translate(player, finalString);
  }

  private String getEntityName(final Entity entity) {
    if (entity instanceof Player) {
      return ((Player) entity).getDisplayName();
    }
    return (entity.getCustomName() == null) ? entity.getName() : entity.getCustomName();
  }

  public void sendTimedBroadcast(Player player, String message, String... playerMessages) {
    boolean silent = message.endsWith("-s");
    String m = silent ? message.substring(0, message.length() - 2).trim() : message;

    SCHEDULER.scheduleSyncDelayedTask(ChatBot.INSTANCE, () -> {
      ComponentBuilder componentBuilder = new ComponentBuilder();
      if (JSONUtils.isJSONValid(m)) {
        componentBuilder.append(new TextComponent(ChatColor.translateAlternateColorCodes('&', prefix + " ")));
        componentBuilder.append(ComponentSerializer.parse(translate(player, m, playerMessages)));
      }
      if (m.contains("\n")) {
        String[] args = m.split("\n");
        for (String arg : args) {
          if (silent) {
            player.sendMessage(translate(player, prefix + " " + arg, playerMessages));
            continue;
          }
          Bukkit.broadcastMessage(translate(player, prefix + " " + arg, playerMessages));
        }
        return;
      }
      if (silent) {
        if (!componentBuilder.getParts().isEmpty()) {
          player.spigot().sendMessage(componentBuilder.create());
          return;
        }
        player.sendMessage(translate(player, prefix + " " + m, playerMessages));
        return;
      }
      if (!componentBuilder.getParts().isEmpty()) {
        Bukkit.spigot().broadcast(componentBuilder.create());
        return;
      }
      Bukkit.broadcastMessage(translate(player, prefix + " " + m, playerMessages));
    }, responseSpeed);
  }

  public void sendPlayerDeathEventTimedBroadcast(PlayerDeathEvent event, Player player, String message) {
    processResponse(player, translatePlayerDeathEvent(event, player, message), true);
  }

  public void sendEntityDeathEventTimedBroadcast(EntityDeathEvent event, Player player, String message) {
    processResponse(player, translateEntityDeathEvent(event, player, message), true);
  }

  public String getRandomResponse(String string) {
    final List<String> responseList = config.getStringList(string);
    if (responseList.isEmpty()) {
      return "not-found";
    }
    return responseList.get(RANDOM.nextInt(responseList.size()));
  }

  public void processResponse(Player player, String message, boolean getRandomResponse) {
    String response = getRandomResponse ? getRandomResponse(message.toLowerCase()) : message.toLowerCase();
//    if (response.equals("not-found")) {
//      processResponse(player, "no-matches.sentence-not-found", true);
//      return;
//    }
    if (!response.contains("[cmd") && !response.contains("[perm")) {
      if (!response.contains("%arg-")) {
        sendTimedBroadcast(player, response);
      } else {
        sendTimedBroadcast(player, response, message.replaceAll("-", " ").replaceAll("\\.", " "));
      }
      return;
    }
    while (!response.isEmpty()) {
      if (response.contains("[cmd")) {
        Matcher matcher = COMMAND_PATTERN.matcher(response);
        if (!matcher.find()) {
          continue;
        }
        response = response.replaceFirst(COMMAND_PATTERN.pattern(), "").trim();

        SCHEDULER.scheduleSyncDelayedTask(ChatBot.INSTANCE, () -> {
          boolean silent = message.endsWith("-s");
          String m = silent ? message.substring(0, message.length() - 2).trim() : message;
          Bukkit.dispatchCommand(matcher.group(1).equalsIgnoreCase("op") ? Bukkit.getConsoleSender() : player,
                  translate(player, m));
        }, responseSpeed);
      } else if (response.contains("[perm")) {
        Matcher matcher = PERMISSION_PATTERN.matcher(response);
        if (!matcher.find()) {
          continue;
        }
        response = response.replaceFirst(PERMISSION_PATTERN.pattern(), "").trim();
        if (!player.hasPermission(matcher.group(1))) {
          return;
        }
        processResponse(player, matcher.group(2), false);
      } else {
        if (!response.contains("%arg-")) {
          sendTimedBroadcast(player, response);
        } else {
          sendTimedBroadcast(player, response, message.replaceAll("-", " ").replaceAll("\\.", " "));
        }
        response = "";
      }
    }
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

  public void addMiscResponse(String message, String response, CommandSender sender) {
    List<String> currentResponses = config.getStringList("miscellaneous." + message);
    currentResponses.add(response);
    config.set("miscellaneous." + message, currentResponses);
    try {
      config.save(file);
    } catch (IOException ex) {
      ChatBot.INSTANCE.getLogger().log(Level.WARNING,
              "Couldn''''t add miscellaneous response miscellaneous.{0}.{1} to bot {2}",
              new Object[]{message, response, file});
      return;
    }
    sender.sendMessage(translate(sender, config.getString("message-added")));
    reload();
  }

}
