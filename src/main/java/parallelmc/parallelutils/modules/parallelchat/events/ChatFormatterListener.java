package parallelmc.parallelutils.modules.parallelchat.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import parallelmc.parallelutils.modules.parallelchat.ChatOptions;
import parallelmc.parallelutils.modules.parallelchat.ParallelRenderer;

public class ChatFormatterListener implements Listener {

	private final ChatOptions options;

	public ChatFormatterListener(ChatOptions options) {
		this.options = options;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChatMessage(final AsyncChatEvent event) {
		event.renderer(new ParallelRenderer(options));
	}
}
