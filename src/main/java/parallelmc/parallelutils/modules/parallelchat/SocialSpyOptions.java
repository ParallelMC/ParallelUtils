package parallelmc.parallelutils.modules.parallelchat;


public class SocialSpyOptions {
    private boolean socialSpy;
    private boolean cmdSpy;

    public SocialSpyOptions(boolean socialSpy, boolean cmdSpy) {
        this.socialSpy = socialSpy;
        this.cmdSpy = cmdSpy;
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
}
