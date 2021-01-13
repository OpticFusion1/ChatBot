package optic_fusion1.chatbot.events;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BotResponseEvent extends Event implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Bot bot;
  private final String response;
  private boolean cancelled;

  public BotResponseEvent(Bot bot, String response) {
    this.bot = bot;
    this.response = response;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList(){
    return HANDLER_LIST;
  }
  
  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean bln) {
    this.cancelled = bln;
  }

  public Bot getBot() {
    return bot;
  }

  public String getResponse() {
    return response;
  }

}
