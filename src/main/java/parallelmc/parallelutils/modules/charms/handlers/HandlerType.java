package parallelmc.parallelutils.modules.charms.handlers;

public enum HandlerType {
	PARTICLE(HandlerCategory.RUNNABLE), // This is particularly weird. Needs to be a runnable only running when item is equipped
	MESSAGE_KILL(HandlerCategory.EVENT), // Listen for kill event
	COUNTER_KILL(HandlerCategory.EVENT), // Listen for kill event
	STYLE_NAME(HandlerCategory.APPLY), // Handle on charm application
	LORE(HandlerCategory.APPLY), // Add lore to item
	TEST_RUNNABLE(HandlerCategory.RUNNABLE), // Test runnables
	TEST_EVENT(HandlerCategory.EVENT), // Test events
	TEST_APPLY(HandlerCategory.APPLY), // Test apply
	PLAYER_PARTICLE(HandlerCategory.EVENT), // PlayerParticle handler
	COMMAND_KILL(HandlerCategory.EVENT), // On killing a player, run commands
	COMMAND_HIT(HandlerCategory.EVENT), // On entity hit, run command
	COMMAND_RUNNABLE(HandlerCategory.RUNNABLE), // Run a command at specified intervals
	NONE(HandlerCategory.APPLY); // Do not put on charms. Does nothing

	private final HandlerCategory category;

	HandlerType(HandlerCategory category) {
		this.category = category;
	}

	public HandlerCategory getCategory() {
		return category;
	}
}
