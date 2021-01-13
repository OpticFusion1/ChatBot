package optic_fusion1.chatbot.bot.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import optic_fusion1.chatbot.bot.Bot;

public class CommandResponse {

  private ResponseBlock[] responseBlocks;

  public CommandResponse(String response) {
    responseBlocks = parseResponse(response);
  }

  private static final Pattern COMMAND_PATTERN = Pattern.compile("\\[cmd type\\=(.+?)\\](.*?)\\[\\/cmd\\]");
  private static final Pattern PERMISSION_PATTERN = Pattern.compile("\\[perm\\=(.+?)\\](.*?)\\[\\/perm\\]");
  private static final Pattern SOUND_PATTERN = Pattern.compile("\\[sound\\=(.+?)\\](.*?)\\[\\/sound\\]");

  private ResponseBlock[] parseResponse(String response) {
    List<ResponseBlock> blocks = new ArrayList<>();
    char[] array = response.toCharArray();
    StringBuilder messageBuilder = new StringBuilder();
    String tag;
    for (int i = 0; i < array.length; i++) {
      tag = getTag(array, i);
      if (tag.isEmpty()) {
        if (i + 1 < array.length && array[i] == '-' && array[i + 1] == 's') {
          if (i + 2 == array.length || !getTag(array, i + 2).isEmpty()) {
            blocks.add(new MessageResponseBlock(messageBuilder.toString(), true));
            i++;
            continue;
          }
        }
        messageBuilder.append(array[i]);
        continue;
      }
      switch (tag) {
        case "cmd":
          Matcher commandMatcher = COMMAND_PATTERN.matcher(response.substring(i));
          if (commandMatcher.find()) {
            blocks.add(new CommandResponseBlock(commandMatcher.group(1).equalsIgnoreCase("op"),
                    commandMatcher.group(2)));
            i += commandMatcher.group().length();
          }
          break;
        case "sound":
          Matcher soundMatcher = SOUND_PATTERN.matcher(response.substring(i));
          if (soundMatcher.find()) {
            blocks.add(new SoundResponseBlock(soundMatcher.group(1).toUpperCase()));
            i += soundMatcher.group().length();
          }
          break;
      }
    }
    return blocks.toArray(new ResponseBlock[]{});
  }

  private String getTag(char[] array, int index) {
    if (array.length >= index + 6) {
      if (array[index] == '[') {
        if (array[index + 1] == 'c' && array[index + 2] == 'm' && array[index + 3] == 'd') {
          return "cmd";
        } else if (array[index + 1] == 'p' && array[index + 2] == 'e' && array[index + 3] == 'r'
                && array[index + 4] == 'm') {
          return "perm";
        } else if (array[index + 1] == 's' && array[index + 2] == 'o' && array[index + 3] == 'u'
                && array[index + 4] == 'n' && array[index + 5] == 'd') {
          return "sound";
        }
      }
    }
    return "";
  }

  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    for (int i = 0; i < responseBlocks.length; i++) {
      responseBlocks[i].execute(bot, SCHEDULER, player, origMessage);
    }
  }

}
