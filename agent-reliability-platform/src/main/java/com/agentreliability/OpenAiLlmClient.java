
package com.agentreliability;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class OpenAiLlmClient implements LlmClient {

    private final OpenAIClient client;

    public OpenAiLlmClient() {
        this(OpenAIOkHttpClient.fromEnv());
    }

    public OpenAiLlmClient(OpenAIClient client) {
        this.client = client;
    }

    @Override
    public String generateText(String prompt) {

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.GPT_4O)
                .input(prompt)
                .build();

        Response response = client.responses().create(params);

        return response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .reduce("", String::concat);
    }
}
