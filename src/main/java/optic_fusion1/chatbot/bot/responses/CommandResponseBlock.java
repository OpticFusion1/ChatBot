package optic_fusion1.chatbot.bot.responses;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.bot.Bot;

public class CommandResponseBlock extends ResponseBlock {

  private boolean op;
  private String command;

  public CommandResponseBlock(boolean op, String command) {
    this.op = op;
    this.command = command;
  }

  @Override
  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    SCHEDULER.scheduleSyncDelayedTask(ChatBot.INSTANCE, () -> {
      Bukkit.dispatchCommand(op ? Bukkit.getConsoleSender() : player, bot.translate(player, command));
    }, bot.getResponseSpeed());
  }

  @Override
  public String getResponseType() {
    return "command";
  }

}
