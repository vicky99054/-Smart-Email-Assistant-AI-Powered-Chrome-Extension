package com.Smart_Email_Writer.Controller;

import com.Smart_Email_Writer.Pojo.EmailRequest;
import com.Smart_Email_Writer.Service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
//@RequestMapping("/api/v1")
@AllArgsConstructor
public class EmailGeneratorController {

    private EmailGeneratorService emailGeneratorService;

   // @CrossOrigin(origins = "http://localhost:5173")
    @CrossOrigin(origins = "https://mail.google.com")
    @PostMapping("/email")
    public ResponseEntity<?> generateEmail(@RequestBody EmailRequest request){

        System.out.println(request.getEmailContent());
       String response= emailGeneratorService.generateEmailReply(request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/")
    public String test(){
        return " hello java";
    }

//    @PostMapping("/reply")
//    public ResponseEntity<Map<String, String>> generateReply(@RequestBody Map<String, String> request) {
//        String emailBody = request.get("emailBody");
//
//        String response= emailGeneratorService.generateEmailReply(request);
//        // Your AI logic here
//        String reply = "Okay, since you asked: " + response;
//
//        // ✅ Return as JSON
//        return ResponseEntity.ok(Map.of("reply", reply));
//    }
}



