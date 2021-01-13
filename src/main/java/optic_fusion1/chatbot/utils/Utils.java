package optic_fusion1.chatbot.utils;

import org.bukkit.ChatColor;

public final class Utils {

  private Utils() {
  }

  public static String colorize(final String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
}
