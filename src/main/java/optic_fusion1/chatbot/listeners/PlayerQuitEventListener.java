package optic_fusion1.chatbot.listeners;

import optic_fusion1.chatbot.ChatBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitEventListener implements Listener {

  private final ChatBot chatBot;

  public PlayerQuitEventListener(ChatBot chatBot) {
    this.chatBot = chatBot;
  }

  @EventHandler
  public void on(PlayerQuitEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.playerquit")))
            .forEachOrdered(bot -> {
              bot.processResponse(event.getPlayer(), "events.playerquit", true);
            });
  }

}
