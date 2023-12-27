package parallelmc.parallelutils.modules.parallelquests;

import java.util.List;

public record QuestEntry(String id, String title, List<String> description, boolean available) {}