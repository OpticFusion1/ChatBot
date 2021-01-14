package optic_fusion1.chatbot.listeners;

import optic_fusion1.chatbot.ChatBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PlayerAdvancementDoneEventListener implements Listener {

  private final ChatBot chatBot;

  public PlayerAdvancementDoneEventListener(ChatBot chatBot) {
    this.chatBot = chatBot;
  }

  @EventHandler
  public void on(PlayerAdvancementDoneEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.playeradvancementdone"))).forEachOrdered(bot -> {
      bot.processEventResponse(event.getPlayer(), "events.playeradvancementdone", true, event);
    });
  }

}
