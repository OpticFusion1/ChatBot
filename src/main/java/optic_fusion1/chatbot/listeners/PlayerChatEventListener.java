package optic_fusion1.chatbot.listeners;

import optic_fusion1.chatbot.ChatBot;
import optic_fusion1.chatbot.bot.Bot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatEventListener implements Listener {

  private final ChatBot chatBot;

  public PlayerChatEventListener(ChatBot chatBot) {
    this.chatBot = chatBot;
  }

  @EventHandler
  public void on(AsyncPlayerChatEvent event) {
    String message = event.getMessage();
    Player player = event.getPlayer();
    Bot bot = chatBot.getBotManager().getBot(message.split(" ")[0]);
    if (bot == null) {
      bot = chatBot.getBotManager().getDefaultBot();
      if (bot == null) {
        String miscConfigString = "miscellaneous." + message.replaceAll("[^\\p{L} ]", "").replaceAll(" ", "-").toLowerCase();
        chatBot.getBotManager().getBots().stream().filter(cBot -> (cBot.hasResponse(miscConfigString))).forEachOrdered(cBot -> {
          if (!event.isCancelled()) {
            if (message.endsWith("-s")) {
              event.setCancelled(true);
            }
          }
          cBot.processResponse(player, miscConfigString, true);
        });
        return;
      }
    }
    if (player.hasPermission("chatbot.use." + bot.getName())) {
      processMessage(player, bot, message.replaceAll("\\\\.", ""));
      if (message.endsWith("-s")) {
        event.setCancelled(true);
      }
    }
  }

  private void processMessage(Player player, Bot bot, String message) {
    if (bot.isBotNameOnly(message)) {
      bot.processResponse(player, "no-matches.bot-name-only", true);
      return;
    }
    boolean sentRegex = false;
    for (Bot cBot : chatBot.getBotManager().getBots()) {
      if (cBot.hasRegexResponse(message)) {
        cBot.sendRegexResponse(player, message);
        if (!sentRegex) {
          sentRegex = true;
        }
      }
    }
    if (sentRegex) {
      return;
    }
    String msg = "responses." + message.replaceAll("[^\\p{L} ]", "").replaceAll(bot.getName() + " ", "").trim().replaceAll(" ", ".").toLowerCase();
    if (bot.hasResponse(msg)) {
      bot.processResponse(player, msg, true);
      return;
    }
    String miscConfigString = "miscellaneous." + message.replaceAll("[^\\p{L} ]", "").replaceAll(" ", "-").toLowerCase();
    if (bot.hasResponse(miscConfigString)) {
      bot.processResponse(player, miscConfigString, true);
    }
  }
}
