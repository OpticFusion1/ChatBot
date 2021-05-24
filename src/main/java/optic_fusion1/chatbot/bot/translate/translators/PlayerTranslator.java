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
import me.clip.placeholderapi.PlaceholderAPI;
import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.translate.Translator;
import static optic_fusion1.chatbot.bot.translate.TranslateResponse.PLACEHOLDER_PATTERN;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerTranslator implements Translator {

  @Override
  public String execute(Bot bot, CommandSender sender, String origMessage) {
    Player player = (Player) sender;
    String translatedMessage = origMessage;
    if (ChatBot.usePlaceholderAPI) {
      translatedMessage = PlaceholderAPI.setBracketPlaceholders(player, translatedMessage);
      translatedMessage = PlaceholderAPI.setPlaceholders(player, translatedMessage);
    }
    if (ChatBot.useMVDWPlaceholderAPI) {
      translatedMessage = be.maximvdw.placeholderapi.PlaceholderAPI.replacePlaceholders(player, translatedMessage);
    }
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(translatedMessage);
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "player_name":
          translatedMessage = translatedMessage.replaceAll("%player_name%", player.getName());
          continue;
        case "player_name_lowercase":
          translatedMessage = translatedMessage.replaceAll("%player_name_lowercase%", player.getName().toLowerCase());
          continue;
        case "player_name_uppercase":
          translatedMessage = translatedMessage.replaceAll("%player_name_uppercase%", player.getName().toUpperCase());
          continue;
        case "player_displayname":
          translatedMessage = translatedMessage.replaceAll("%player_displayname%", player.getDisplayName());
          continue;
        case "player_displayname_lowercase":
          translatedMessage = translatedMessage.replaceAll("%player_displayname_lowercase%", player.getDisplayName().toLowerCase());
          continue;
        case "player_displayname_uppercase":
          translatedMessage = translatedMessage.replaceAll("%player_displayname_uppercase%", player.getDisplayName().toUpperCase());
      }
    }
    return translatedMessage;
  }

}
