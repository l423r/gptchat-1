package ru.svolyrk.gptchat.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class Gpt4Service {

    private final WebClient webClient;

    @Value("${gpt.api.url}")
    private String apiUrl;

    @Value("${gpt.api.key}")
    private String apiKey;

    public Gpt4Service(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(apiUrl).build();
    }

    public Mono<String> getGpt4Response(List<Map<String, String>> context) {
        GptRequest request = new GptRequest("gpt-4", context);

        return webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .body(Mono.just(request), GptRequest.class)
                .retrieve()
                .bodyToMono(GptResponse.class)
                .map(response -> {
                    if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                        return response.getChoices().get(0).getMessage().getContent();
                    }
                    return "";
                });
    }

    public Mono<String> textToSpeech(String text) {
        Map<String, Object> request = Map.of(
                "input", text,
                "voice", "en-US-Wavenet-D",
                "audioConfig", Map.of("audioEncoding", "MP3")
        );

        return webClient.post()
                .uri("/v1/text:synthesize")
                .header("Authorization", "Bearer " + apiKey)
                .body(Mono.just(request), Map.class)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("audioContent"));
    }

    public Mono<String> speechToText(byte[] audioData) {
        Map<String, Object> request = Map.of(
                "audio", Map.of("content", Base64.getEncoder().encodeToString(audioData)),
                "config", Map.of("languageCode", "en-US")
        );

        return webClient.post()
                .uri("/v1/speech:recognize")
                .header("Authorization", "Bearer " + apiKey)
                .body(Mono.just(request), Map.class)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
                    if (results != null && !results.isEmpty()) {
                        final var alternatives = results.get(0).get("alternatives");
//                        return (String) alternatives.get(0).get("transcript");
                        return (String) alternatives;
                    }
                    return "";
                });
    }

    public Mono<String> extractMainIdea(String text) {
        Map<String, Object> request = Map.of("text", text);

        return webClient.post()
                .uri("/v1/extract/mainIdea")
                .header("Authorization", "Bearer " + apiKey)
                .body(Mono.just(request), Map.class)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("mainIdea"));
    }

    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GptRequest {
        private String model;
        private List<Map<String, String>> messages;

        public GptRequest(String model, List<Map<String, String>> messages) {
            this.model = model;
            this.messages = messages;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public List<Map<String, String>> getMessages() {
            return messages;
        }

        public void setMessages(List<Map<String, String>> messages) {
            this.messages = messages;
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GptResponse {
        private List<Choice> choices;

        public List<Choice> getChoices() {
            return choices;
        }

        public void setChoices(List<Choice> choices) {
            this.choices = choices;
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Choice {
            private Message message;

            public Message getMessage() {
                return message;
            }

            public void setMessage(Message message) {
                this.message = message;
            }
        }

        @NoArgsConstructor
        @AllArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message {
            private String content;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }
}
