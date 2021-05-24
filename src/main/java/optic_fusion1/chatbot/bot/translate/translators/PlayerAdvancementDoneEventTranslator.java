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

import java.util.Arrays;
import java.util.regex.Matcher;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.translate.EventTranslator;
import static optic_fusion1.chatbot.bot.translate.TranslateResponse.PLACEHOLDER_PATTERN;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PlayerAdvancementDoneEventTranslator implements EventTranslator {

  @Override
  public String execute(Bot bot, CommandSender sender, String origMessage, Event origEvent) {
    PlayerAdvancementDoneEvent event = (PlayerAdvancementDoneEvent) origEvent;
    String translatedMessage = origMessage;
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(translatedMessage);
    Advancement advancement = event.getAdvancement();
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "advancement_key":
          translatedMessage = translatedMessage.replaceAll("%advancement_key%", advancement.getKey().getKey());
          continue;
        case "advancement_namespace":
          translatedMessage = translatedMessage.replaceAll("%advancement_namespace%", advancement.getKey().getNamespace());
          continue;
        case "advancement_criteria":
          translatedMessage = translatedMessage.replaceAll("%advancement_critera%", Arrays.toString(advancement.getCriteria().toArray()));
      }
    }
    return translatedMessage;
  }

}
