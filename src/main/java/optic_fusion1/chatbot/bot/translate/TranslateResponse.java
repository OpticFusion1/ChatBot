package optic_fusion1.chatbot.bot.translate;

import optic_fusion1.chatbot.bot.translate.translators.RandomTranslator;
import optic_fusion1.chatbot.bot.translate.translators.BotTranslator;
import optic_fusion1.chatbot.bot.translate.translators.PlayerTranslator;
import java.util.regex.Pattern;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.translate.translators.EntityDeathEventTranslator;
import optic_fusion1.chatbot.bot.translate.translators.PlayerDeathEventTranslator;
import optic_fusion1.chatbot.bot.translate.translators.PlayerJoinEventTranslator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class TranslateResponse {

  public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+)[%]");

  public TranslateResponse() {
  }

  public static String parseResponse(Bot bot, CommandSender sender, String message, Event event) {
    String translatedMessage = message;
    if (event instanceof PlayerDeathEvent) {
      translatedMessage = new PlayerDeathEventTranslator().execute(bot, sender, message, event);
    }
    if (event instanceof PlayerJoinEvent) {
      translatedMessage = new PlayerJoinEventTranslator().execute(bot, sender, message, event);
    }
    if (event instanceof EntityDeathEvent) {
      translatedMessage = new EntityDeathEventTranslator().execute(bot, sender, message, event);
    }
    return parseResponse(bot, sender, translatedMessage);
  }

  public static String parseResponse(Bot bot, CommandSender sender, String message) {
    String translatedMessage = message;
    if (sender instanceof Player) {
      translatedMessage = new PlayerTranslator().execute(bot, (Player) sender, translatedMessage);
    }
    translatedMessage = new BotTranslator().execute(bot, sender, translatedMessage);
    translatedMessage = new RandomTranslator().execute(bot, sender, translatedMessage);
    return translatedMessage;
  }

}
