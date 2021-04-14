package optic_fusion1.chatbot.events;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BotDisableEvent extends Event implements Cancellable {

  private static final HandlerList HANDLER_LIST = new HandlerList();
  private boolean cancelled;
  private final Bot bot;

  public BotDisableEvent(Bot bot) {
    this.bot = bot;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean bln) {
    cancelled = bln;
  }

  public Bot getBot() {
    return bot;
  }

}
