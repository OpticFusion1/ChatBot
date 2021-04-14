package optic_fusion1.chatbot.bot.responses.blocks;

import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.responses.CommandResponse;
import optic_fusion1.chatbot.bot.responses.ResponseBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class PermissionResponseBlock extends ResponseBlock {

  private String permission;
  private String rest;

  public PermissionResponseBlock(String permission, String rest) {
    this.permission = permission;
    this.rest = rest;
  }

  @Override
  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    if (!player.hasPermission(permission)) {
      return;
    }
    new CommandResponse(rest).execute(bot, SCHEDULER, player, origMessage);
  }

  @Override
  public String getResponseType() {
    return "Permission";
  }

}
