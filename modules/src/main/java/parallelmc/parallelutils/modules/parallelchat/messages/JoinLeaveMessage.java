package parallelmc.parallelutils.modules.parallelchat.messages;

public record JoinLeaveMessage(String event, String text, String requiredRank) {}
