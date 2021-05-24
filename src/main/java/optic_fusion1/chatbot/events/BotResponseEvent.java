package optic_fusion1.chatbot.events;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.event.HandlerList;

public class BotResponseEvent extends BotEvent {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final String response;

  public BotResponseEvent(Bot bot, String response) {
    super(bot);
    this.response = response;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  /**
   * Gets the bots response
   *
   * @return Bot response
   */
  public String getResponse() {
    return response;
  }

}
