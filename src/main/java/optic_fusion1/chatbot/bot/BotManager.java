package optic_fusion1.chatbot.bot;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import optic_fusion1.chatbot.events.BotDisableEvent;
import optic_fusion1.chatbot.events.BotEnableEvent;
import optic_fusion1.chatbot.events.BotReloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class BotManager {

  private static final PluginManager PLUGIN_MANAGER = Bukkit.getPluginManager();
  private static final HashMap<String, Bot> BOTS = new HashMap<>();

  public Collection<Bot> getBots() {
    return BOTS.values();
  }

  public boolean loadBot(File file) {
    if (!file.exists()) {
      return false;
    }
    return addBot(new Bot(file));
  }

  public boolean addBot(Bot bot) {
    if (bot == null || BOTS.containsKey(bot.getName().toLowerCase())) {
      return false;
    }
    BotEnableEvent event = new BotEnableEvent(bot);
    PLUGIN_MANAGER.callEvent(event);
    if (event.isCancelled()) {
      return false;
    }
    BOTS.put(bot.getName().toLowerCase(), bot);
    return true;
  }

  public void disableAllBots() {
    BOTS.values().forEach(bot -> removeBot(bot));
  }

  public void reloadAllBots() {
    BOTS.values().forEach(bot -> reloadBot(bot));
  }

  public void reloadBot(Bot bot) {
    BotReloadEvent event = new BotReloadEvent(bot);
    PLUGIN_MANAGER.callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    bot.reload();
  }

  public Bot getBot(String name) {
    Bot bot = BOTS.get(name.toLowerCase());
    if (bot == null) {
      for (Bot targetBot : BOTS.values()) {
        if(targetBot.getAliases().contains(name)){
          bot = targetBot;
          break;
        }
      }
    }
    return bot;
  }

  public Bot getDefaultBot() {
    for (Bot bot : BOTS.values()) {
      if (bot.isDefault()) {
        return bot;
      }
    }
    return null;
  }

  public void removeBot(Bot bot) {
    BotDisableEvent event = new BotDisableEvent(bot);
    PLUGIN_MANAGER.callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    BOTS.remove(bot.getName().toLowerCase());
  }

}
