package optic_fusion1.chatbot.bot.responses;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import optic_fusion1.chatbot.bot.Bot;

public abstract class ResponseBlock {

  public abstract void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage);

  public abstract String getResponseType();

}
