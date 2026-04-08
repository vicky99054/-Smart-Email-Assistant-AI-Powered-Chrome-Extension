package com.Smart_Email_Writer.Pojo;

import lombok.Data;

@Data
public class EmailRequest {

    private String emailContent;
    private String tone;

    // New optional fields
    private String length;     // e.g., "short", "medium", "detailed"
    private String language;   // e.g., "English", "Hindi", "Tamil"
    private String signature;  // e.g., "Best regards, Vicky Kumar"
}
