package optic_fusion1.chatbot.utils;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Utils {

  private Utils() {
  }

  public static String colorize(String string) {
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
