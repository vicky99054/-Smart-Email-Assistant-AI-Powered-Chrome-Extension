package com.Smart_Email_Writer.Controller;

import com.Smart_Email_Writer.Pojo.EmailRequest;
import com.Smart_Email_Writer.Service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@AllArgsConstructor
public class EmailGeneratorController {

    private final EmailGeneratorService emailGeneratorService;

    // Blocking endpoint – returns full reply

    @CrossOrigin(origins = "*")
    @PostMapping("/email")
    public ResponseEntity<?> generateEmail(@RequestBody EmailRequest request) {
        String response = emailGeneratorService.generateEmailReply(request);
        return ResponseEntity.ok(response);
    }

    // Streaming endpoint – returns chunks line by line via SSE
    @CrossOrigin(origins = "*")
    @PostMapping(value = "/email/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamEmail(@RequestBody EmailRequest request) {
        return emailGeneratorService.streamEmailReply(request);
    }
}
