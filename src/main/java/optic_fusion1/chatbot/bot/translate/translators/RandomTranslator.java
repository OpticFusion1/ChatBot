package optic_fusion1.chatbot.bot.translate.translators;

import java.util.regex.Matcher;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.translate.Translator;
import static optic_fusion1.chatbot.bot.translate.TranslateResponse.PLACEHOLDER_PATTERN;
import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.command.CommandSender;

public class RandomTranslator implements Translator {

  @Override
  public String execute(Bot bot, CommandSender sender, String origMessage) {
    String translatedMessage = origMessage;
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(translatedMessage);
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "random_int":
          translatedMessage = translatedMessage.replaceAll("%random_int%",
                  String.valueOf(RandomUtils.nextInt(Integer.MAX_VALUE) + 1));
      }
    }
    return translatedMessage;
  }

}
