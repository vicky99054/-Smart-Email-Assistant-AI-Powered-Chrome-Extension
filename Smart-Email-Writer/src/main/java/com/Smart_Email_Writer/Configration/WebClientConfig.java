package com.Smart_Email_Writer.Configration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Configuration
public class WebClientConfig {

    @Value("google.api.url")  // this url is Gemini url import from a property file
    private String GeminiApi;

    @Value("google.api.key")  // this Api_key is Gemini key import from a property file
    private String GeminiApiKey;

    @Bean
    public WebClient WebclientBuilder(){

        return WebClient.builder()
                //.baseUrl("https://generativelanguage.googleapis.com/v1beta/models/")
               // .defaultUriVariables(Collections.singletonMap("key", GeminiApiKey))
                .build();
    }
    @Bean
    public String getApikey(){
        return this.GeminiApiKey;
    }
}
