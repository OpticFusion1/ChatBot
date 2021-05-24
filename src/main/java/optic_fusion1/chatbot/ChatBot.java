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

package optic_fusion1.chatbot;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import optic_fusion1.chatbot.Updater.UpdateResult;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.BotManager;
import optic_fusion1.chatbot.command.BotCommand;
import optic_fusion1.chatbot.listeners.EntityDeathEventListener;
import optic_fusion1.chatbot.listeners.PlayerAdvancementDoneEventListener;
import optic_fusion1.chatbot.listeners.PlayerChatEventListener;
import optic_fusion1.chatbot.listeners.PlayerJoinEventListener;
import optic_fusion1.chatbot.listeners.PlayerQuitEventListener;
import optic_fusion1.chatbot.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatBot extends JavaPlugin {

  private static final BotManager BOT_MANAGER = new BotManager();
  public static ChatBot INSTANCE;
  private File botStorage;
  public static boolean usePlaceholderAPI = false;
  public static boolean useMVDWPlaceholderAPI = false;
  private PluginManager pluginManager;

  @Override
  public void onEnable() {
    INSTANCE = this;
    new MetricsLite(this, 55642);
    checkForUpdate();
    File file = new File("chatbot", "config.yml");
    if (!file.exists()) {
      saveDefaultConfig();
    }
    createFiles();
    loadBots();
    useMVDWPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("MVdWPlaceholderAPI");
    usePlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    registerCommand();
    registerListeners();
  }

  private void registerListeners() {
    pluginManager = Bukkit.getPluginManager();
    register(new EntityDeathEventListener(this));
    register(new PlayerChatEventListener(this));
    register(new PlayerJoinEventListener(this));
    register(new PlayerQuitEventListener(this));
    register(new PlayerAdvancementDoneEventListener(this));
  }

  private void register(Listener listener) {
    pluginManager.registerEvents(listener, this);
  }

  private void registerCommand() {
    PluginCommand command = getCommand("bot");
    BotCommand cmd = new BotCommand(this);
    command.setExecutor(cmd);
    command.setTabCompleter(cmd);
  }

  private void loadBots() {
    for (File file : botStorage.listFiles()) {
      if (file.getName().endsWith(".yml")) {
        BOT_MANAGER.addBot(new Bot(file));
      }
    }
  }

  private void createFiles() {
    File dataFolder = getDataFolder();
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    botStorage = new File(dataFolder, "bots");
    if (!botStorage.exists()) {
      botStorage.mkdirs();
    }
    File file = new File(botStorage, "default.yml");
    if (!file.exists()) {
      FileUtils.copy(ChatBot.class.getResourceAsStream("/default.yml"), new File(botStorage, "default.yml").toString());
    }
  }

  private void checkForUpdate() {
    Logger logger = getLogger();
    FileConfiguration pluginConfig = getConfig();
    Updater updater = new Updater(this, 55642, false);
    Updater.UpdateResult result = updater.getResult();
    if (result != UpdateResult.UPDATE_AVAILABLE) {
      return;
    }
    if (!pluginConfig.getBoolean("download-update")) {
      logger.info("===== UPDATE AVAILABLE ====");
      logger.info("https://www.spigotmc.org/resources/chatbot-fully-customizable.55642/");
      logger.log(Level.INFO, "Installed Version: {0} New Version:{1}", new Object[]{updater.getOldVersion(), updater.getVersion()});
      logger.info("===== UPDATE AVAILABLE ====");
      return;
    }
    logger.info("==== UPDATE AVAILABLE ====");
    logger.info("====    DOWNLOADING   ====");
    updater.downloadUpdate();
  }

  @Override
  public void onDisable() {
  }

  public File getBotStorage() {
    return botStorage;
  }

  public boolean usePlaceholderAPI() {
    return usePlaceholderAPI;
  }

  public BotManager getBotManager() {
    return BOT_MANAGER;
  }

}
