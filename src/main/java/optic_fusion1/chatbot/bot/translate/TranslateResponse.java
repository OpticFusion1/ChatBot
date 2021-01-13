package optic_fusion1.chatbot.bot.translate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TranslateResponse {

  public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");

  public TranslateResponse() {
  }

  public static String parseResponse(Bot bot, CommandSender sender, String message) {
    String translatedMessage = message;
    if (sender instanceof Player) {
      translatedMessage = new PlayerTranslator().execute(bot, (Player) sender, translatedMessage);
    }
    translatedMessage = bot.translateBotPlaceholders(translatedMessage, new String[]{});
    translatedMessage = bot.translateRandomPlaceholders(translatedMessage);
    return translatedMessage;
  }

}
