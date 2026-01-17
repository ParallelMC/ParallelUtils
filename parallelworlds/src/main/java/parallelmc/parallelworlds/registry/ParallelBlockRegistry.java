package parallelmc.parallelworlds.registry;

public class ParallelBlockRegistry {
    
    private static ParallelBlockRegistry instance = null;
    
    private ParallelBlockRegistry() {

    }

    public static ParallelBlockRegistry getInstance() {
        if (instance == null) {
            instance = new ParallelBlockRegistry();
        }
        
        return instance;
    }
}
