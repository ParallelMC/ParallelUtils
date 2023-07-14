package parallelmc.parallelutils.modules.parallelchat.messages;

public record JoinLeaveMessage(String name, String event, String text, String requiredRank) {}
