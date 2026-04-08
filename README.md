# -Smart-Email-Assistant-AI-Powered-Chrome-Extension
Smart Email Assistant is a Chrome extension that adds an intelligent "AI Reply" button to Gmail, enabling users to generate smart, personalized email replies instantly. Powered by a Spring Boot backend, it analyzes incoming emails, detects sender details, and crafts context responses in your chosen tone all without disrupting Gmail’s native UI.                                                                                                                                                   
✨ Features
🧠 AI-Powered Replies Automatically generates professional, friendly, or casual replies based on the original email content.

🎯 Tone Selection Prompt Choose your preferred tone before generating a reply: friendly, professional, or casual.

👤 Sender Name Detection Extracts the sender’s name from the email thread for personalized greetings like “Dear John,”.

📬 Seamless Gmail Integration Injects a custom "AI Reply" button next to Gmail’s Send button and inserts replies without breaking layout or formatting.

🧼 Clean UI/UX Replies are inserted as properly spaced paragraphs, preserving Gmail’s rich text editor and quoted thread.                                                  
🔍 Real-Time Logging Console logs for debugging, reply tracking, and backend communication.

🛠️ Tech Stack
Frontend: Vanilla JavaScript, DOM APIs, MutationObserver

Backend: Spring Boot (REST API for AI-generated replies)

Platform: Gmail Web UI (Chrome Extension)                                                                                                                                                                                                                                                                                                                                                                                                                                                                      📦 Project Structure                                                                                                                                                                                /extension
  ├── content.js         # Main Gmail integration script
  ├── manifest.json      # Chrome extension manifest
  └── styles.css         # Optional styling

/backend
  └── Spring Boot app    # Receives email content + tone + sender name, returns AI-generated reply
                                                                                                                                                                                                                                                                                                                                      🧪 How It Works
Detects Gmail compose or reply window using MutationObserver.

Injects an "AI Reply" button next to the Send button.

On click:

Extracts original email content

Detects sender name

Prompts user to select tone

Sends data to backend

Backend returns a formatted reply.

Reply is inserted into Gmail’s rich text box as a clean paragraph block.                                                                                                                                                                                                                                                               📧Example Output                                                                                                                                                                                                                                                                                                               Subject: Regarding Croma Gift Card - Order Confirmation: 7354ce90...

Dear Microsoft,

Thank you for the Croma Gift Card received as part of the Microsoft Rewards program. I am writing to acknowledge the receipt of the email containing the card details.

I will ensure to review the activation process and terms and conditions before using the gift card.

Sincerely,  
Vicky                                                                                                                                                                       
🧠 Backend API Payload                                                                                                                                                 {
  "emailContent": "...",
  "tone": "professional",
  "senderName": "Microsoft"
}                                                                                                                                                                        
🙌 Author                                                                                                                                                                                                                                                                                                                                            Vicky Kumar                                                                                                                                                        Java Backend Developer expanding into full-stack and product-focused roles. 🔧 Passionate about building seamless frontend-backend workflows and                    polished user experiences.
