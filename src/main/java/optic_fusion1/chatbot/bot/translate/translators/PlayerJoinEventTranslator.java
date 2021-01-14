package optic_fusion1.chatbot.bot.translate.translators;

import java.util.regex.Matcher;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.translate.EventTranslator;
import static optic_fusion1.chatbot.bot.translate.TranslateResponse.PLACEHOLDER_PATTERN;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventTranslator implements EventTranslator {

  @Override
  public String execute(Bot bot, CommandSender sender, String origMessage, Event origEvent) {
    PlayerJoinEvent event = (PlayerJoinEvent) origEvent;
    String translatedMessage = origMessage;
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(translatedMessage);
    while (matcher.find()) {
      String group = matcher.group(1);
      switch (group) {
        case "join_message":
          translatedMessage = translatedMessage.replaceAll("%join_message%", event.getJoinMessage());
          continue;
      }
    }
    return translatedMessage;
  }

}
