package com.Smart_Email_Writer.Service;

import com.Smart_Email_Writer.Configration.WebClientConfig;
import com.Smart_Email_Writer.Pojo.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class EmailGeneratorService {

    @Value("${google.api.url}")
    private final String GeminiApi;
    @Value("${google.api.key}")
    private final String GeminiApikey;
    private final WebClientConfig webClientConfig;

    public String generateEmailReply(EmailRequest request) {
        try {
            // Build prompt
            String prompt = buildPrompt(request);

            // Prepare JSON payload
            String jsonPayload = String.format("""
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ]
                }""", prompt);

            // Construct full URL
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GeminiApikey;

            // Send request
            String response = webClientConfig.WebclientBuilder()
                    .post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(jsonPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Extract and return response
            return extractResponseContent(response);

        } catch (Exception e) {
            log.error("Error generating email reply: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate email reply", e);
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

    private String buildPrompt(EmailRequest request) {
        StringBuilder prompt = new StringBuilder("Generate a professional email reply for the following email:\n");

        if (request.getTone() != null && !request.getTone().isEmpty()) {
            prompt.append("Use a ").append(request.getTone()).append(" tone.\n");
        } else {
            prompt.append("Use a casual tone.\n");
        }

        prompt.append("Original Email:\n").append(request.getEmailContent());
        return prompt.toString();
    }
}
