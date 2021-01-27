package optic_fusion1.chatbot.bot.responses.blocks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.responses.ResponseBlock;

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
      bot.sendTimedBroadcast(player, message, silent);
    } else {
      bot.sendTimedBroadcast(player, message, silent, origMessage.replaceAll("-", " ").replaceAll("\\.", " "));
    }
  }

  @Override
  public String getResponseType() {
    return "message";
  }

}
