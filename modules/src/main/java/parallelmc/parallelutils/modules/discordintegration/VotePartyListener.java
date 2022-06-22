package parallelmc.parallelutils.modules.discordintegration;

import me.clip.voteparty.VoteParty;
import me.clip.voteparty.events.VoteReceivedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import parallelmc.parallelutils.Parallelutils;

import java.util.logging.Level;

public class VotePartyListener implements Listener {

	private final VoteParty vp;

	public VotePartyListener(VoteParty vp) {
		this.vp = vp;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onReceive(VoteReceivedEvent event) {
		int currVotes = vp.getVotes();
		int neededVotes = vp.getVotesNeeded();

		if (BotManager.getInstance() != null) {
			if (!BotManager.getInstance().editMessage("vpMessage", "vpMessage",
					"" + (neededVotes-currVotes) + " votes are needed to have a Vote Party!")) {
				Parallelutils.log(Level.WARNING, "Unable to edit message. Unknown error.");
			}
		} else {
			Parallelutils.log(Level.WARNING, "BotManager not initialized. Can't edit message!");
		}
	}
}
