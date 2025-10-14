console.log("Email script loaded");

// Find Gmail's Send button
function findSendButton() {
  return document.querySelector('.T-I.J-J5-Ji.aoO.v7.T-I-atl.L3');
}

// Find Gmail's reply textbox (returns editable div)
function findReplyBox() {
  const editableDivs = document.querySelectorAll('div[contenteditable="true"]');

  for (const div of editableDivs) {
    const parent = div.closest('[role="textbox"], .Am.Al.editable');
    if (parent && div.offsetParent !== null) {
      return div;
    }
  }

  return null;
}

// Find original email content (quoted thread)
function findOriginalEmailContent() {
  const originalEmail = document.querySelector('.a3s');
  return originalEmail ? originalEmail.innerText.trim() : '';
}

// Find sender name from Gmail thread
function findSenderName() {
  const senderSpan = document.querySelector('.go');
  return senderSpan ? senderSpan.textContent.trim() : 'there';
}

// Create the AI Reply button
function createAIButton() {
  const button = document.createElement('div');
  button.className = 'ai-reply-button';
  button.textContent = 'AI Reply';
  button.setAttribute('role', 'button');
  button.setAttribute('aria-label', 'Generate AI Reply');
  button.setAttribute('data-tooltip', 'Generate AI Reply');

  // Style the button
  Object.assign(button.style, {
    backgroundColor: '#1a73e8',
    color: '#fff',
    borderRadius: '20px',
    padding: '0 12px',
    height: '36px',
    marginRight: '8px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500',
    userSelect: 'none',
    border: 'none'
  });

  // Hover effect
  button.addEventListener('mouseenter', () => {
    button.style.backgroundColor = '#1669c1';
  });
  button.addEventListener('mouseleave', () => {
    button.style.backgroundColor = '#1a73e8';
  });

  // Click event: send to backend and insert reply
  button.addEventListener('click', async () => {
    console.log("AI Reply button clicked!");

    button.innerHTML = 'Generating...';
    button.disabled = true;

    const replyBox = findReplyBox();
    if (!replyBox) {
      console.error("Reply box not found");
      button.innerHTML = 'AI Reply';
      button.disabled = false;
      return;
    }

    const emailText = findOriginalEmailContent();
    const senderName = findSenderName();
    console.log("Original email content:", emailText);
    console.log("Sender name:", senderName);

    const tone = prompt("Select tone: friendly, professional, or casual", "professional");
    if (!tone || !['friendly', 'professional', 'casual'].includes(tone.toLowerCase())) {
      console.warn("Invalid tone selected");
      button.innerHTML = 'AI Reply';
      button.disabled = false;
      return;
    }

    try {
      const response = await fetch('http://localhost:8081/email', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          emailContent: emailText,
          tone: tone.toLowerCase(),
          senderName: senderName
        })
      });

      const replyText = await response.text();
      console.log("AI-generated reply:", replyText);

      // Format reply with line breaks
      const formattedReply = replyText
        .replace(/\n\n/g, '<br><br>')
        .replace(/\n/g, '<br>');

      // Insert reply as a new div with proper formatting
      const replyDiv = document.createElement('div');
      replyDiv.innerHTML = formattedReply;
      replyBox.appendChild(replyDiv);
      replyBox.focus();
    } catch (error) {
      console.error("Error fetching AI reply:", error);
    }

    button.innerHTML = 'AI Reply';
    button.disabled = false;
  });

  return button;
}

// Inject the button next to Gmail's Send button
function injectButton() {
  const existingButton = document.querySelector('.ai-reply-button');
  if (existingButton) existingButton.remove();

  const sendButton = findSendButton();
  if (!sendButton) {
    console.log("Send button not found");
    return;
  }

  const button = createAIButton();
  sendButton.parentNode.insertBefore(button, sendButton); // Insert to the left
}

// Watch for Gmail compose windows
const observer = new MutationObserver((mutations) => {
  for (const mutation of mutations) {
    const addedNodes = Array.from(mutation.addedNodes);
    const hasComposeElement = addedNodes.some(node =>
      node.nodeType === Node.ELEMENT_NODE &&
      (
        node.matches('.aDh, .btc, [role="dialog"]') ||
        node.querySelector('.aDh, .btc, [role="dialog"]')
      )
    );

    if (hasComposeElement) {
      console.log("Compose window detected");
      setTimeout(injectButton, 1000); // Wait for Gmail UI to settle
    }
  }
});

observer.observe(document.body, { childList: true, subtree: true });
