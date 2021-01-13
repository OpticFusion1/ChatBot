package optic_fusion1.chatbot.listeners;

import optic_fusion1.chatbot.ChatBot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EntityDeathEventListener implements Listener {

  private final ChatBot chatBot;

  public EntityDeathEventListener(ChatBot chatBot) {
    this.chatBot = chatBot;
  }

  @EventHandler
  public void on(PlayerDeathEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.playerdeath"))).forEachOrdered(bot -> {
      bot.processResponse(event.getEntity(), bot.translatePlayerDeathEvent(event, event.getEntity(), bot.getRandomResponse("events.playerdeath")), false);
    });
  }

  @EventHandler
  public void on(EntityDeathEvent event) {
    chatBot.getBotManager().getBots().stream().filter(bot -> (bot.hasResponse("events.entitydeath"))).forEachOrdered(bot -> {
      bot.processResponse(event.getEntity().getKiller(), bot.translateEntityDeathEvent(event, event.getEntity().getKiller(), bot.getRandomResponse("events.entitydeath")), false);
    });
  }

}
