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
