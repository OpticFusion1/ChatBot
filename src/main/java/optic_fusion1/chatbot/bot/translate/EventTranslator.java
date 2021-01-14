package optic_fusion1.chatbot.bot.translate;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;

public interface EventTranslator {

  public abstract String execute(Bot bot, CommandSender sender, String origMessage, Event origEvent);

}
