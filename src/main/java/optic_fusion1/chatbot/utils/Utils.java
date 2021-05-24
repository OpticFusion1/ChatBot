/*
* Copyright (C) 2021 Optic_Fusion1
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package optic_fusion1.chatbot.utils;

import com.google.gson.Gson;
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
  private static final Gson gson = new Gson();

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
    amount = drops.stream().map(item -> item.getAmount()).reduce(amount, Integer::sum);
    return amount;
  }

  public static String getEntityName(Entity entity) {
    if (entity instanceof Player) {
      return ((Player) entity).getDisplayName();
    }
    return entity.getCustomName() != null ? entity.getCustomName() : entity.getName();
  }

  public static boolean isJSONValid(String jsonInString) {
    try {
      gson.fromJson(jsonInString, Object.class);
      return true;
    } catch (com.google.gson.JsonSyntaxException ex) {
      return false;
    }
  }

}
