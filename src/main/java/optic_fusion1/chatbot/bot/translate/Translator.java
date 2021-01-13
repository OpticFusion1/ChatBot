package optic_fusion1.chatbot.bot.translate;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.entity.Player;

public interface Translator {

  public abstract String execute(Bot bot, Player player, String origMessage);

}
