package optic_fusion1.chatbot.bot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
import optic_fusion1.chatbot.bot.responses.CommandResponse;
import optic_fusion1.chatbot.bot.translate.TranslateResponse;
import optic_fusion1.chatbot.utils.FileUtils;
import optic_fusion1.chatbot.utils.JSONUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitScheduler;

//TODO: Update example config to take into account all current features
public class Bot {

  private static final Random RANDOM = new Random();
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");
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
    return TranslateResponse.parseResponse(this, sender, originalMessage);
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
        componentBuilder.append(ComponentSerializer.parse(ChatColor.translateAlternateColorCodes('&', translate(player, m, playerMessages))));
      }
      if (m.contains("\n")) {
        String[] args = m.split("\n");
        for (String arg : args) {
          if (silent) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', translate(player, prefix + " " + arg, playerMessages)));
            continue;
          }
          Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', translate(player, prefix + " " + arg, playerMessages)));
        }
        return;
      }
      if (silent) {
        if (!componentBuilder.getParts().isEmpty()) {
          player.spigot().sendMessage(componentBuilder.create());
          return;
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', translate(player, prefix + " " + m, playerMessages)));
        return;
      }
      if (!componentBuilder.getParts().isEmpty()) {
        Bukkit.spigot().broadcast(componentBuilder.create());
        return;
      }
      Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', translate(player, prefix + " " + m, playerMessages)));
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
    new CommandResponse(response).execute(this, SCHEDULER, player, message);
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

  public boolean isBotNameOnly(String message) {
    return message.equals(name) || aliases.contains(message);
  }

}
