package optic_fusion1.chatbot.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.BotManager;
import optic_fusion1.chatbot.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class BotCommand implements TabExecutor, CommandExecutor {

  private FileConfiguration config;
  private String prefix;
  private final ChatBot chatBot;

  public BotCommand(ChatBot chatBot) {
    this.config = chatBot.getConfig();
    this.prefix = config.getString("prefix");
    this.chatBot = chatBot;
  }

  @Override
  public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
    List<String> list = new ArrayList<>();
    if (strings.length == 1) {
      list.add("enable");
      list.add("reload");
      list.add("disable");
      list.add("list");
      return list;
    }
    if (strings[0].equalsIgnoreCase("enable") && strings.length == 2) {
      list.clear();
      list.add("all");
      for (File file : chatBot.getBotStorage().listFiles()) {
        String fileName = file.getName();
        if (fileName.endsWith(".yml")) {
          list.add(fileName.substring(0, file.getName().length() - 4));
        }
      }
      return list;
    } else if (strings[0].equalsIgnoreCase("reload") && strings.length == 2) {
      list.clear();
      list.add("all");
      list.add("config");
      chatBot.getBotManager().getBots().forEach(bot -> {
        list.add(bot.getName());
      });
      return list;
    } else if (strings[0].equalsIgnoreCase("disable") && strings.length == 2) {
      list.clear();
      list.add("all");
      chatBot.getBotManager().getBots().forEach(bot -> {
        list.add(bot.getName());
      });
      return list;
    }
    return list;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] strings) {
    if (!(sender instanceof Player)) {
      if (strings.length == 0) {
        sender.sendMessage(prefix + " " + config.getString("usage"));
        return true;
      }
      if (strings[0].equalsIgnoreCase("reload")) {
        reload(sender, strings);
      }
      return true;
    }
    Player player = (Player) sender;
    if (strings.length == 0) {
      player.sendMessage(Utils.colorize(prefix + " " + config.getString("usage")));
      return true;
    }
    if (strings[0].equalsIgnoreCase("enable")) {
      enable(player, strings);
      return true;
    }
    if (strings[0].equalsIgnoreCase("disable")) {
      disable(player, strings);
      return true;
    }
    if (strings[0].equalsIgnoreCase("reload")) {
      reload(player, strings);
      return true;
    }
    if (strings[0].equalsIgnoreCase("list")) {
      listBots(player);
      return true;
    }
    if (strings.length >= 4 && strings[0].equalsIgnoreCase("add")) {
      addMiscMessage(player, strings);
      return true;
    }
    return true;
  }

  public void addMiscMessage(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      if (!sender.hasPermission("chatbot.add")) {
        sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-permission")));
        return;
      }
    }
    Bot bot = chatBot.getBotManager().getBot(args[1]);
    if (bot == null) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-bot").replaceAll("%bot_name%", args[1])));
      return;
    }
    String response = StringUtils.join(args, " ", 3, args.length);
    bot.addMiscResponse(args[2], response, sender);
    sender.sendMessage("Added " + args[2] + " with a response of " + response + " to the bot " + args[1]);
  }

  public void listBots(CommandSender sender) {
    if (sender instanceof Player) {
      if (!sender.hasPermission("chatbot.list")) {
        sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-permission")));
        return;
      }
    }
    List<String> names = new ArrayList<>();
    chatBot.getBotManager().getBots().forEach(bot -> {
      names.add(bot.getName());
    });
    sender.sendMessage(Utils.colorize(prefix + " " + StringUtils.join(names, ", ")));
  }

  public void enable(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      if (!sender.hasPermission("chatbot.enable")) {
        sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-permission")));
        return;
      }
    }
    if (args.length == 1) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("enable-usage")));
      return;
    }
    BotManager botManager = chatBot.getBotManager();
    if (args[1].equalsIgnoreCase("all")) {
      for (File file : chatBot.getBotStorage().listFiles()) {
        if (!botManager.loadBot(file)) {
          sender.sendMessage(Utils.colorize(prefix + " " + config.getString("file-wasn't-loaded")
                  .replaceAll("$filename", file.getName()).replaceAll("%file_name%", file.getName())));
        }
      }
      String msg = botManager.getBots().isEmpty() ? config.getString("no-bots") : config.getString("all-bots-enabled");
      sender.sendMessage(Utils.colorize(prefix + " " + msg));
    }
    Bot bot = botManager.getBot(args[1]);
    if (bot != null) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("bot-already-enabled").replaceAll("%bot_name%", bot.getName())));
      return;
    }
    if (!botManager.loadBot(new File(chatBot.getBotStorage(), args[1] + ".yml"))) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-bot").replaceAll("%bot_name%", args[1])));
      return;
    }
    sender.sendMessage(Utils.colorize(prefix + " " + config.getString("bot-enabled").replaceAll("%bot_name%", bot.getName())));
  }

  public void disable(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      if (!sender.hasPermission("chatbot.disable")) {
        sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-permission")));
        return;
      }
    }
    if (args.length == 1) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("disable-usage")));
      return;
    }
    BotManager botManager = chatBot.getBotManager();
    if (args[1].equalsIgnoreCase("all")) {
      botManager.disableAllBots();
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("all-bots-disabled")));
      return;
    }
    Bot bot = botManager.getBot(args[1]);
    if (bot == null) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("bot-already-disabled").replaceAll("%bot_name%", args[1])));
      return;
    }
    botManager.removeBot(bot);
    sender.sendMessage(Utils.colorize(prefix + " " + config.getString("bot-disabled").replaceAll("%bot_name%", bot.getName())));
  }

  public void reload(CommandSender sender, String[] args) {
    if (sender instanceof Player) {
      if (!sender.hasPermission("chatbot.reload")) {
        sender.sendMessage(prefix + " " + config.getString("no-permission"));
        return;
      }
    }
    if (args.length == 1) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("reload-usage")));
      return;
    }
    if (args[1].equalsIgnoreCase("config")) {
      chatBot.reloadConfig();
      config = chatBot.getConfig();
      prefix = config.getString("prefix");
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("reloaded")));
      return;
    }
    BotManager botManager = chatBot.getBotManager();
    if (args[1].equalsIgnoreCase("all")) {
      botManager.reloadAllBots();
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("all-bots-reloaded")));
      return;
    }
    Bot bot = botManager.getBot(args[1]);
    if (bot == null) {
      sender.sendMessage(Utils.colorize(prefix + " " + config.getString("no-bot").replaceAll("%bot_name%", args[1])));
      return;
    }
    bot.reload();
    sender.sendMessage(Utils.colorize(prefix + " " + config.getString("bot-reloaded").replaceAll("%bot_name%", bot.getName())));
  }

}
