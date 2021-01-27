package optic_fusion1.chatbot.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Utils {

  private static int SERVER_VERSION = -1;

  private Utils() {
  }

  public static int getVersion() {
    if (SERVER_VERSION != -1) {
      return SERVER_VERSION;
    }
    SERVER_VERSION = Integer.parseInt(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3]
            .replace("1_", "").replaceAll("_R\\d", "").replaceAll("v", ""));
    return SERVER_VERSION;
  }

  public static String colorize(String string) {
    if (getVersion() >= 16) {
      Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
      for (Matcher matcher = pattern.matcher(string); matcher.find(); matcher = pattern.matcher(string)) {
        String color = string.substring(matcher.start(), matcher.end());
        string = string.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
      }
      string = ChatColor.translateAlternateColorCodes('&', string);
      return string;
    }
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static int getTotalDropAmount(List<ItemStack> drops) {
    int amount = 0;
    for (ItemStack item : drops) {
      amount += item.getAmount();
    }
    return amount;
  }

  public static String getEntityName(Entity entity) {
    if (entity instanceof Player) {
      return ((Player) entity).getDisplayName();
    }
    return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
  }

}
