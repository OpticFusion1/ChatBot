package optic_fusion1.chatbot.bot.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import optic_fusion1.chatbot.bot.Bot;
import optic_fusion1.chatbot.bot.responses.blocks.CommandResponseBlock;
import optic_fusion1.chatbot.bot.responses.blocks.MessageResponseBlock;
import optic_fusion1.chatbot.bot.responses.blocks.PermissionResponseBlock;
import optic_fusion1.chatbot.bot.responses.blocks.SoundResponseBlock;
import optic_fusion1.chatbot.bot.responses.blocks.WaitResponseBlock;
import optic_fusion1.chatbot.utils.Time;
import optic_fusion1.chatbot.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class CommandResponse {

  private ResponseBlock[] responseBlocks;

  public CommandResponse(String response) {
    responseBlocks = parseResponse(response);
  }

  private static final Pattern COMMAND_PATTERN = Pattern.compile("\\[cmd type\\=(.+?)\\](.*?)\\[\\/cmd\\]");
  private static final Pattern PERMISSION_PATTERN = Pattern.compile("\\[perm\\=(.+?)\\](.*?)\\[\\/perm\\]");
  // TODO: Implement volume and pitch support for sound blocks
  private static final Pattern SOUND_PATTERN = Pattern.compile("\\[sound](.*?)\\[\\/sound\\]");
  private static final Pattern WAIT_PATTERN = Pattern.compile("\\[wait\\=(.+?)\\](.*?)\\[\\/wait\\]");

  private ResponseBlock[] parseResponse(String response) {
    List<ResponseBlock> blocks = new ArrayList<>();
    if (response.equals("not-found")) {
      return blocks.toArray(new ResponseBlock[]{});
    }
    char[] array = response.toCharArray();
    StringBuilder messageBuilder = new StringBuilder();
    String tag;
    boolean silent = false;
    for (int i = 0; i < array.length; i++) {
      tag = getTag(array, i);
      if (tag.isEmpty()) {
        if (array[i] == '{') {
          String json = response.substring(response.indexOf("{"));
          json = json.substring(0, json.lastIndexOf("}") + 1).trim();
          if (!Utils.isJSONValid(json)) {
            throw new IllegalArgumentException(json + " is not valid json");
          }
          blocks.add(new MessageResponseBlock(json, false));
          i += json.length() + 1;
          continue;
        }
        if (i + 1 < array.length && array[i] == '-' && array[i + 1] == 's') {
          if (i + 2 == array.length || !getTag(array, i + 2).isEmpty()) {
            silent = true;
            blocks.add(new MessageResponseBlock(messageBuilder.toString(), true));
            i++;
            continue;
          }
        }
        messageBuilder.append(array[i]);
        continue;
      }
      switch (tag) {
        case "wait":
          Matcher waitMatcher = WAIT_PATTERN.matcher(response.substring(1));
          if (waitMatcher.find()) {
            blocks.add(new WaitResponseBlock((long) Time.parseString(waitMatcher.group(1)).toTicks(), waitMatcher.group(2)));
            i += waitMatcher.group().length();
          }
          break;
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
        case "perm":
          Matcher permissionMatcher = PERMISSION_PATTERN.matcher(response.substring(i));
          if (permissionMatcher.find()) {
            blocks.add(new PermissionResponseBlock(permissionMatcher.group(1), permissionMatcher.group(2)));
            i += permissionMatcher.group().length();
          }
          break;
      }
    }
    if (!silent) {
      blocks.add(new MessageResponseBlock(messageBuilder.toString(), false));
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
        } else if (array[index + 1] == 'w' && array[index + 2] == 'a' && array[index + 3] == 'i'
                && array[index + 4] == 't') {
          return "wait";
        }
      }
    }
    return "";
  }

  public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
    for (ResponseBlock responseBlock : responseBlocks) {
      responseBlock.execute(bot, SCHEDULER, player, origMessage);
    }
  }

}
