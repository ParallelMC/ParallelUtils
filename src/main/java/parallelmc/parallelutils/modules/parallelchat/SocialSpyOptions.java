package parallelmc.parallelutils.modules.parallelchat;


public class SocialSpyOptions {
    private boolean socialSpy;
    private boolean cmdSpy;
    private boolean chatRoomSpy;

    public SocialSpyOptions(boolean socialSpy, boolean cmdSpy, boolean chatRoomSpy) {
        this.socialSpy = socialSpy;
        this.cmdSpy = cmdSpy;
        this.chatRoomSpy = chatRoomSpy;
    }

    public boolean isSocialSpy() {
        return socialSpy;
    }

    public void setSocialSpy(boolean socialSpy) {
        this.socialSpy = socialSpy;
    }

    public boolean isCmdSpy() {
        return cmdSpy;
    }

    public void setCmdSpy(boolean cmdSpy) {
        this.cmdSpy = cmdSpy;
    }

    public boolean isChatRoomSpy() {
        return chatRoomSpy;
    }

    public void setChatRoomSpy(boolean chatRoomSpy) {
        this.chatRoomSpy = chatRoomSpy;
    }
}
