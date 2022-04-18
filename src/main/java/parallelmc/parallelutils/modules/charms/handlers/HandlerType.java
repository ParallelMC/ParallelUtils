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
	PLAYER_PARTICLE(HandlerCategory.APPLY); // PlayerParticle handler

	private final HandlerCategory category;

	HandlerType(HandlerCategory category) {
		this.category = category;
	}

	public HandlerCategory getCategory() {
		return category;
	}
}
