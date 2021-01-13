package optic_fusion1.chatbot;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.BotManager;
import optic_fusion1.chatbot.command.BotCommand;
import optic_fusion1.chatbot.listeners.EntityDeathEventListener;
import optic_fusion1.chatbot.listeners.PlayerChatEventListener;
import optic_fusion1.chatbot.listeners.PlayerJoinEventListener;
import optic_fusion1.chatbot.listeners.PlayerQuitEventListener;
import optic_fusion1.chatbot.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
  private static final File currDirectory = new File(".");

  @Override
  public void onEnable() {
    INSTANCE = this;
    new MetricsLite(this, 55642);
    checkForUpdate();
    saveDefaultConfig();
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
    if (result == Updater.UpdateResult.UPDATE_AVAILABLE) {
      if (!pluginConfig.isSet("auto-update") || !pluginConfig.getBoolean("auto-update")) {
        logger.info("===== UPDATE AVAILABLE ====");
        logger.info("https://www.spigotmc.org/resources/chatbot-fully-customizable.55642/");
        logger.log(Level.INFO, "Installed Version: {0} New Version:{1}", new Object[]{updater.getOldVersion(), updater.getVersion()});
        logger.info("===== UPDATE AVAILABLE ====");
      } else if (pluginConfig.getBoolean("auto-update")) {
        updater.downloadUpdate();
      }
    }
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