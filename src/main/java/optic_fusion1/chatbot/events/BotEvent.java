package optic_fusion1.chatbot.events;

import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class BotEvent extends Event implements Cancellable {

  private boolean cancelled = false;
  private Bot bot;

  public BotEvent(Bot bot) {
    this.bot = bot;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancelled) {
    this.cancelled = cancelled;
  }

  /**
   * Gets the bot involved in this event
   *
   * @return Bot for this event
   */
  public Bot getBot() {
    return bot;
  }

}
