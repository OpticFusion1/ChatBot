package optic_fusion1.chatbot.listeners;

import optic_fusion1.chatbot.ChatBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {

  private final ChatBot chatBot;

  public PlayerJoinEventListener(ChatBot chatBot) {
    this.chatBot = chatBot;
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.playerjoin"))).forEachOrdered(bot -> {
      bot.processEventResponse(event.getPlayer(), "events.playerjoin", true, event);
    });
  }

}
