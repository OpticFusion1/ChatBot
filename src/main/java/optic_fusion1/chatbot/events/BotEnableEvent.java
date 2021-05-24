package optic_fusion1.chatbot.events;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.event.HandlerList;

public class BotEnableEvent extends BotEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();

  public BotEnableEvent(Bot bot) {
    super(bot);
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

}
