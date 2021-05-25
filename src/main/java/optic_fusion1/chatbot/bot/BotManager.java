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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import optic_fusion1.chatbot.events.BotDisableEvent;
import optic_fusion1.chatbot.events.BotEnableEvent;
import optic_fusion1.chatbot.events.BotReloadEvent;
import org.bukkit.Bukkit;

public class BotManager {

  private final Map<String, Bot> BOTS = new HashMap<>();

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
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return false;
    }
    BOTS.put(bot.getName().toLowerCase(), bot);
    return true;
  }

  public void disableAllBots() {
    Iterator<Bot> bots = BOTS.values().iterator();
    while (bots.hasNext()) {
      removeBot(bots.next());
    }
  }

  public void reloadAllBots() {
    BOTS.values().forEach(bot -> reloadBot(bot));
  }

  public void reloadBot(Bot bot) {
    BotReloadEvent event = new BotReloadEvent(bot);
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    bot.reload();
  }

  public Bot getBot(String name) {
    Bot bot = BOTS.get(name.toLowerCase());
    if (bot == null) {
      for (Bot targetBot : BOTS.values()) {
        if (targetBot.getAliases().contains(name)) {
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
    Bukkit.getPluginManager().callEvent(event);
    if (event.isCancelled()) {
      return;
    }
    BOTS.remove(bot.getName().toLowerCase());
  }

}
