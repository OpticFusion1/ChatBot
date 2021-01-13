package optic_fusion1.chatbot.bot.responses;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.bot.Bot;

public class MessageResponseBlock extends ResponseBlock {

  private String message;
  private boolean silent;

  public MessageResponseBlock(String message, boolean silent) {
    this.message = message;
    this.silent = silent;
  }

  @Override
  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    if (!message.contains("%arg-")) {
      bot.sendTimedBroadcast(player, message);
    } else {
      bot.sendTimedBroadcast(player, message, origMessage.replaceAll("-", " ").replaceAll("\\.", " "));
    }
  }

  @Override
  public String getResponseType() {
    return "message";
  }

}
