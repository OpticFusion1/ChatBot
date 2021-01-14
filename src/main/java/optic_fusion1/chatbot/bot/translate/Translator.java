package optic_fusion1.chatbot.bot.translate;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.command.CommandSender;

public interface Translator {

  public abstract String execute(Bot bot, CommandSender sender, String origMessage);

}
