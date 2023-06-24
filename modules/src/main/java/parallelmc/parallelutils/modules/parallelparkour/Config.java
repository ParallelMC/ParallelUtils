package parallelmc.parallelutils.modules.parallelparkour;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Config implements parallelmc.parallelutils.Config {
    @NotNull
    @Override
    public List<String> getHardDepends() {
        return List.of("ParallelChat");
    }

    @NotNull
    @Override
    public List<String> getSoftDepends() {
        return new ArrayList<>();
    }
}
