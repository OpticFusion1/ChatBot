package optic_fusion1.chatbot.bot;

import java.util.regex.Pattern;

import optic_fusion1.chatbot.bot.responses.CommandResponse;

public class ChatCommand {

  private Bot owner;
  private String id;
  private Pattern[] regexes;
  private CommandResponse[] responses;

  public ChatCommand(Bot owner, String id, String[] inputs, String[] responseStrings) {
    this.owner = owner;
    this.id = id;
    regexes = new Pattern[inputs.length];
    for (int i = 0; i < inputs.length; i++) {
      regexes[i] = Pattern.compile(inputs[i], Pattern.CASE_INSENSITIVE);
    }
    responses = new CommandResponse[responseStrings.length];
    for (int i = 0; i < responseStrings.length; i++) {
      responses[i] = new CommandResponse(responseStrings[i]);
    }
  }

  public String getId() {
    return id;
  }

  public boolean triggers(String message) {
    for (Pattern p : regexes) {
      if (p.matcher(message).matches()) {
        return true;
      }
    }
    return false;
  }

  public Pattern[] getRegexes() {
    return regexes;
  }

  public CommandResponse[] getResponses() {
    return responses;
  }

  public CommandResponse getRandomResponse() {
    return responses[(int) Math.round(Math.random() * responses.length)];
  }

}
