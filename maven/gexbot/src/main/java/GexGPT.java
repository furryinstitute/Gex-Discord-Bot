import java.nio.file.Paths;
import com.hexadevlabs.gpt4all.LLModel;

public class GexGPT {
        public static String generateAIReply(String prompt) {

            try (LLModel model = new LLModel(Paths.get(GexBot.AI_MODEL_PATH))) {
                LLModel.GenerationConfig config = LLModel.config()
                        .withNPredict(192).build();
                return model.generate(prompt, config, true);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
}
