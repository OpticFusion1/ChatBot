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

package optic_fusion1.chatbot.bot.translate.translators;

import java.util.regex.Matcher;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.translate.EventTranslator;
import static optic_fusion1.chatbot.bot.translate.TranslateResponse.PLACEHOLDER_PATTERN;
import optic_fusion1.chatbot.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathEventTranslator implements EventTranslator {

  @Override
  public String execute(Bot bot, CommandSender sender, String origMessage, Event origEvent) {
    EntityDeathEvent event = (EntityDeathEvent) origEvent;
    String translatedMessage = origMessage;
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(translatedMessage);
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "dropped_xp_amount":
          translatedMessage = translatedMessage.replaceAll("%dropped_xp_amount%", String.valueOf(event.getDroppedExp()));
          continue;
        case "drop_amount":
          translatedMessage = translatedMessage.replaceAll("%drop_amount%", String.valueOf(Utils.getTotalDropAmount(event.getDrops())));
          continue;
        case "killed_name":
          translatedMessage = translatedMessage.replaceAll("%killed_name%", Utils.getEntityName(event.getEntity()));
          continue;
        case "killer_name":
          if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event2 = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
            translatedMessage = translatedMessage.replaceAll("%killer_name%", Utils.getEntityName(event2.getDamager()));
            continue;
          }
          if (event.getEntity().getKiller() != null) {
            translatedMessage = translatedMessage.replaceAll("%killer_name%", Utils.getEntityName(event.getEntity().getKiller()));
          }
          continue;
        case "entity_type":
          translatedMessage = translatedMessage.replaceAll("%entity_type", event.getEntityType().toString());
          continue;
      }
    }
    return translatedMessage;
  }

}
