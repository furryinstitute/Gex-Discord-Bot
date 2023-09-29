import java.nio.file.Paths;
import com.hexadevlabs.gpt4all.LLModel;

public class GexGPT {
    static LLModel model;
    static LLModel.GenerationConfig config;

    public static void loadModel() {
        model = new LLModel(Paths.get(GexBot.AI_MODEL_PATH+GexBot.AI_MODEL));
        config = LLModel.config()
                .withNPredict(512)
                .withTemp((float)GexBot.AI_TEMP)
                .build();
    }

    public static String generateReply(String prompt) {
        try {
            return model.generate( (GexBot.AI_ROLE+"\n "+prompt+"\n"), config, true);
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, but I don't know how to answer your statement. I'm just a goofy and silly lizard that likes to reference celebrities from the 1990s. You better improve your English before you come back to me again. #SorryNotSorry";
        }
    }
}