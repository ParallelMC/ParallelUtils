package parallelmc.parallelutils.modules.parallelquests;


public class QuestStatus {
    private final String questId;
    private String questStage;

    public QuestStatus(String questId, String questStage) {
        this.questId = questId;
        this.questStage = questStage;
    }

    public String getQuestId() { return questId; }

    public String getQuestStage() { return questStage; }

    public void setQuestStage(String stage) { this.questStage = stage; }

    public boolean isCompleted() { return questStage.equalsIgnoreCase("completed"); }
}