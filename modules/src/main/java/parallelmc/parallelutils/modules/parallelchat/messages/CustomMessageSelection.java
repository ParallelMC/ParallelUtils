package parallelmc.parallelutils.modules.parallelchat.messages;

import javax.annotation.Nullable;

public class CustomMessageSelection {
    private String joinMessage;
    private String leaveMessage;

    public CustomMessageSelection(@Nullable String joinMessage, @Nullable String leaveMessage) {
        this.joinMessage = joinMessage;
        this.leaveMessage = leaveMessage;
    }

    @Nullable
    public String getJoinMessage() {
        return joinMessage;
    }

    public void setJoinMessage(@Nullable String joinMessage) {
        this.joinMessage = joinMessage;
    }

    @Nullable
    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(@Nullable String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }
}
