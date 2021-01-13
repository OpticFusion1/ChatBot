package optic_fusion1.chatbot.bot.responses;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import optic_fusion1.chatbot.bot.Bot;

public class SoundResponseBlock extends ResponseBlock {

	private String sound;

	public SoundResponseBlock(String sound) {
		this.sound = sound;
	}

	@Override
	public void execute(Bot bot, BukkitScheduler SCHEDULER, Player player, String origMessage) {
		player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 0);
	}

	@Override
	public String getResponseType() {
		return "sound";
	}

}
