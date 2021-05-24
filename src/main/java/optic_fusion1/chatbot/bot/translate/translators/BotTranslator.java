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
import optic_fusion1.chatbot.bot.translate.Translator;
import static optic_fusion1.chatbot.bot.translate.TranslateResponse.PLACEHOLDER_PATTERN;
import org.bukkit.command.CommandSender;

public class BotTranslator implements Translator {

  @Override
  public String execute(Bot bot, CommandSender sender, String origMessage) {
    String translatedString = origMessage;
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(translatedString);
    while (matcher.find()) {
      String group = matcher.group(1);
      if (group.contains("bot_name")) {
        translatedString = translatedString.replaceAll("%bot_name%", bot.getName());
      } else if (group.contains("bot_name_lowercase")) {
        translatedString = translatedString.replaceAll("%bot_name_lowercase%", bot.getName().toLowerCase());
      } else if (group.contains("bot_name_uppercase")) {
        translatedString = translatedString.replaceAll("%bot_name_uppercase%", bot.getName().toUpperCase());
      } else if (group.contains("bot_prefix")) {
        translatedString = translatedString.replaceAll("%bot_prefix%", bot.getPrefix());
      } else if (group.contains("%bot_prefix_lowercase%")) {
        translatedString = translatedString.replaceAll("%bot_prefix_lowercase%", bot.getPrefix().toLowerCase());
      } else if (group.contains("%bot_prefix_uppercase%")) {
        translatedString = translatedString.replaceAll("%bot_prefix_uppercase%", bot.getPrefix().toUpperCase());
      } else if (group.contains("%response_speed%")) {
        translatedString = translatedString.replaceAll("%response_speed%", String.valueOf(bot.getResponseSpeed()));
      } else if (group.contains("arg-")) {
        String[] args = translatedString.split(" ");
        int arg = Integer.parseInt(group.replace("arg-", ""));
        String found = "not-found";
        try {
          found = args[arg];
        } catch (Exception e) {

        }
        translatedString = translatedString.replaceAll("%arg-" + arg + "%", found);
      }
    }
    return translatedString;
  }

}
