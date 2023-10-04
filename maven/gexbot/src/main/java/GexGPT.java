import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import com.hexadevlabs.gpt4all.LLModel;
import com.hexadevlabs.gpt4all.LLModel.ChatCompletionResponse;

public class GexGPT {
    static LLModel model;
    static LLModel.GenerationConfig config;
    static ChatCompletionResponse test;
    static String botcontext = "";

    public static void loadModel() {
        model = new LLModel(Paths.get(GexBot.AI_MODEL_PATH+GexBot.AI_MODEL));
        config = LLModel.config()
                .withNPredict(GexBot.AI_TOKEN_COUNT)
                .withTemp((float)GexBot.AI_TEMP)
                .build();
    }

    public static String generateReply(String prompt) {
        try {
            test = model.chatCompletion(
                List.of(Map.of("role", "system", "content", GexBot.AI_ROLE),
                        Map.of("role", "system", "content", botcontext),
                        Map.of("role", "user", "content", prompt)), config, true, true);
            botcontext = test.choices.toString();
            return (botcontext.substring(26, botcontext.length()-2));
        } catch (Exception e) {
            return "Sorry, but I don't know how to answer your statement. I'm just a goofy and silly lizard that likes to reference celebrities from the 1990s.";
        }
    }
}