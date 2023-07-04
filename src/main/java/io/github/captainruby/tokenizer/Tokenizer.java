package io.github.captainruby.tokenizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tokenizer {

    private static final String GPT_4 = "gpt-4";
    private static final String GPT_4_0314 = "gpt-4-0314";
    private static final String GPT_4_0613 = "gpt-4-0613";
    private static final String GPT_4_32K_0314 = "gpt-4-32k-0314";
    private static final String GPT_4_32K_0613 = "gpt-4-32k-0613";
    private static final String GPT_35_TURBO = "gpt-3.5-turbo";
    private static final String GPT_35_TURBO_0301 = "gpt-3.5-turbo-0301";
    private static final String GPT_35_TURBO_0613 = "gpt-3.5-turbo-0613";
    private static final String GPT_35_TURBO_16K = "gpt-3.5-turbo-16k";
    private static final String GPT_35_TURBO_16K_0613 = "gpt-3.5-turbo-16k-0613";
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private static final Map<String, Encoding> modelMap = new HashMap<String, Encoding>();

    static {
        for (ModelType modelType : ModelType.values()) {
            modelMap.put(modelType.getName(), registry.getEncodingForModel(modelType));
        }
        modelMap.put(GPT_4_0314, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_4_0613, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_4_32K_0314, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_4_32K_0613, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_35_TURBO_0301, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_35_TURBO_0613, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_35_TURBO_16K, registry.getEncoding(EncodingType.CL100K_BASE));
        modelMap.put(GPT_35_TURBO_16K_0613, registry.getEncoding(EncodingType.CL100K_BASE));
    }

    public static int tokens(String modelName, String content) {
        Encoding encoding = modelMap.get(modelName);
        return tokens(encoding, content);
    }

    public static int tokens(String modelName, List<? extends IMessage> IMessages) {
        Encoding encoding = modelMap.get(modelName);
        int tokensPerMessage = 0;
        int tokensPerName = 0;
        if (modelName.equals(GPT_35_TURBO_0613)
                || modelName.equals(GPT_35_TURBO_16K_0613)
                || modelName.equals(GPT_4_0314)
                || modelName.equals(GPT_4_32K_0314)
                || modelName.equals(GPT_4_0613)
                || modelName.equals(GPT_4_32K_0613)
        ) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        } else if (modelName.equals(GPT_35_TURBO_0301)) {
            tokensPerMessage = 4;
            tokensPerName = -1;
        } else if (modelName.contains(GPT_35_TURBO)) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        } else if (modelName.contains(GPT_4)) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int sum = 0;
        for (IMessage msg : IMessages) {
            sum += tokensPerMessage;
            sum += tokens(encoding, msg.getContent());
            sum += tokens(encoding, msg.getRole());
            sum += tokens(encoding, msg.getName());
            IFunctionCall functionCall = msg.getFunction_call();
            sum += (functionCall == null ? 0 : tokens(encoding, functionCall.toString()));
            if (msg.getName() != null && msg.getName().length() > 0) {
                sum += tokensPerName;
            }
        }
        sum += 3;
        return sum;
    }

    private static int tokens(Encoding enc, String text) {
        return encode(enc, text).size();
    }

    private static List<Integer> encode(Encoding enc, String text) {
        return text == null || text.length() == 0 ? new ArrayList<Integer>() : enc.encode(text);
    }
}
