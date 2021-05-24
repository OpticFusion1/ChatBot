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
