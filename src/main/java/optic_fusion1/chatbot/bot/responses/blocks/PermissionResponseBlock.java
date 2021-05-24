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
