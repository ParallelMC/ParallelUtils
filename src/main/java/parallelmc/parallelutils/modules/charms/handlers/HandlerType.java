package parallelmc.parallelutils.modules.charms.handlers;


public enum HandlerType {
	PARTICLE, // This is particularly weird. Needs to be a runnable only running when item is equipped
	MESSAGE_KILL, // Listen for kill event
	COUNTER_KILL, // Listen for kill event
	STYLE_NAME, // Handle on charm application
}
