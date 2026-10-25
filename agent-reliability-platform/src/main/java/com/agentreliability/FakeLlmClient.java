package com.agentreliability;

public class FakeLlmClient implements LlmClient {

    private final String response;
    private String lastPrompt;

    public FakeLlmClient(String response) {
        this.response = response;
    }

    @Override
    public String generateText(String prompt) {
        this.lastPrompt = prompt;
        return response;
    }

    public String getLastPrompt() {
        return lastPrompt;
    }
}