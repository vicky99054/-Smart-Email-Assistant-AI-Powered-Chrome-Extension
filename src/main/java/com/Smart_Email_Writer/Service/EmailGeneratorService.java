package com.Smart_Email_Writer.Service;

import com.Smart_Email_Writer.Pojo.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@Slf4j
public class EmailGeneratorService {

    @Value("${google.api.url}")
    private String geminiApiUrl;

    private final WebClient webClient;

    public EmailGeneratorService(WebClient webClient) {
        this.webClient = webClient;
    }

    /** Blocking call – waits for full reply */
    public String generateEmailReply(EmailRequest request) {
        try {
            String prompt = buildPrompt(request);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ArrayNode contents = root.putArray("contents");
            ObjectNode content = contents.addObject();
            ArrayNode parts = content.putArray("parts");
            parts.addObject().put("text", prompt);

            String jsonPayload = mapper.writeValueAsString(root);

            String response = webClient.post()
                    .uri(geminiApiUrl) // e.g. gemini-1.5-flash:generateContent
                    .bodyValue(jsonPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                    .block();

            return extractResponseContent(response);

        } catch (Exception e) {
            log.error("Error generating email reply: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate email reply", e);
        }
    }

    /** Streaming call – returns chunks line by line */
    public Flux<String> streamEmailReply(EmailRequest request) {
        try {
            String prompt = buildPrompt(request);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode root = mapper.createObjectNode();
            ArrayNode contents = root.putArray("contents");
            ObjectNode content = contents.addObject();
            ArrayNode parts = content.putArray("parts");
            parts.addObject().put("text", prompt);

            String jsonPayload = mapper.writeValueAsString(root);

            return webClient.post()
                    .uri(geminiApiUrl.replace(":generateContent", ":streamGenerateContent"))
                    .bodyValue(jsonPayload)
                    .retrieve()
                    .bodyToFlux(String.class) // stream chunks
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
                    .map(this::extractStreamChunk);

        } catch (Exception e) {
            log.error("Error streaming email reply: {}", e.getMessage(), e);
            return Flux.error(new RuntimeException("Failed to stream email reply", e));
        }
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);

            return rootNode.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();

        } catch (Exception e) {
            log.error("Error parsing response: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse response", e);
        }
    }

    /** For streaming chunks – just return raw text piece */
    private String extractStreamChunk(String responseChunk) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(responseChunk);

            return rootNode.path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text").asText();
        } catch (Exception e) {
            log.error("Error parsing stream chunk: {}", e.getMessage(), e);
            return "";
        }
    }

    private String buildPrompt(EmailRequest request) {
        StringBuilder prompt = new StringBuilder(
                "Generate exactly ONE clear email reply body. " +
                        "Do not include a subject line or multiple options. " +
                        "Return only the body content with proper sentences and line breaks.\n"
        );

        if (request.getTone() != null && !request.getTone().isEmpty()) {
            prompt.append("Use a ").append(request.getTone()).append(" tone.\n");
        } else {
            prompt.append("Use a professional tone.\n");
        }

        if (request.getLength() != null && !request.getLength().isEmpty()) {
            prompt.append("Make the reply ").append(request.getLength()).append(" in length.\n");
        }

        if (request.getLanguage() != null && !request.getLanguage().isEmpty()) {
            prompt.append("Write the reply in ").append(request.getLanguage()).append(".\n");
        } else {
            prompt.append("Reply in the same language as the original email.\n");
        }

        if (request.getSignature() != null && !request.getSignature().isEmpty()) {
            prompt.append("At the end, add: ").append(request.getSignature()).append("\n");
        }

        prompt.append("Original Email:\n").append(request.getEmailContent());
        return prompt.toString();
    }
}
