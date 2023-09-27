import java.nio.file.Paths;
import com.hexadevlabs.gpt4all.LLModel;

public class GexGPT {
    static LLModel model;
    static LLModel.GenerationConfig config;

    public static void loadModel() {
        model = new LLModel(Paths.get(GexBot.AI_MODEL_PATH+GexBot.AI_MODEL));
        config = LLModel.config()
                .withNPredict(512)
                .withTemp((float)2)
                .build();
        if(Runtime.getRuntime().availableProcessors()-GexBot.THREAD_OFFSET >= 1) {
            model.setThreadCount(Runtime.getRuntime().availableProcessors()-GexBot.THREAD_OFFSET);
        } else {
            System.out.println("ERROR! Thread offset is higher than available processors. Defaulting to use all available threads.");
            model.setThreadCount(Runtime.getRuntime().availableProcessors());
        }
    }

    public static String generateReply(String prompt) {
        try {
           /*model.chatCompletion( List.of(Map.of("role", "system", "content", role),
                            Map.of("role", "user", "content", prompt)), config, true, true);*/
            return model.generate( (GexBot.AI_ROLE+"\n "+prompt+"\n"), config, true);
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, but I don't know how to answer your statement. I'm just a goofy and silly lizard that likes to reference celebrities from the 1990s. You better improve your English before you come back to me again. #SorryNotSorry";
        }
    }
}