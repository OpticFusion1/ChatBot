package optic_fusion1.chatbot.bot.responses;

import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class WaitResponseBlock extends ResponseBlock {

  private long millis;
  private String rest;

  public WaitResponseBlock(long millis, String rest) {
    this.millis = millis;
    this.rest = rest;
  }

  @Override
  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(ChatBot.INSTANCE, () -> {
      new CommandResponse(rest).execute(bot, SCHEDULER, player, origMessage);
    }, millis);
  }

  @Override
  public String getResponseType() {
    return "wait";
  }

}
