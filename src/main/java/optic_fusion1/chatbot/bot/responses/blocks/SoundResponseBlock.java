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
import optic_fusion1.chatbot.bot.responses.ResponseBlock;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class SoundResponseBlock extends ResponseBlock {

  private String sound;

  public SoundResponseBlock(String sound) {
    this.sound = sound;
  }

  @Override
  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 0);
  }

  @Override
  public String getResponseType() {
    return "sound";
  }

}
