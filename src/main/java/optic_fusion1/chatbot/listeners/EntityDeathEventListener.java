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

package optic_fusion1.chatbot.listeners;

import optic_fusion1.chatbot.ChatBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EntityDeathEventListener implements Listener {

  private final ChatBot chatBot;

  public EntityDeathEventListener(ChatBot chatBot) {
    this.chatBot = chatBot;
  }

  @EventHandler
  public void on(PlayerDeathEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.playerdeath"))).forEachOrdered(bot -> {
      bot.processEventResponse(event.getEntity(), "events.playerdeath", true, event);
    });
  }

  @EventHandler
  public void on(EntityDeathEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.entitydeath"))).forEachOrdered(bot -> {
      bot.processEventResponse(event.getEntity().getKiller(), "events.entitydeath", true, event);
    });
  }

}
