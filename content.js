console.log("Smart Email Assistant loaded ✅");

// ─────────────────────────────────────────
// Find Gmail's Send button
// ─────────────────────────────────────────
function findSendButton() {
  return document.querySelector('.T-I.J-J5-Ji.aoO.v7.T-I-atl.L3');
}

// ─────────────────────────────────────────
// Find Gmail's reply textbox
// ─────────────────────────────────────────
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

// ─────────────────────────────────────────
// Find original email content
// ─────────────────────────────────────────
function findOriginalEmailContent() {
  const originalEmail = document.querySelector('.a3s');
  return originalEmail ? originalEmail.innerText.trim() : '';
}

// ─────────────────────────────────────────
// Find sender name
// ─────────────────────────────────────────
function findSenderName() {
  const senderSpan = document.querySelector('.go');
  return senderSpan ? senderSpan.textContent.trim() : 'there';
}

// ─────────────────────────────────────────
// Tone dropdown
// ─────────────────────────────────────────
function createToneDropdown() {
  const select = document.createElement('select');
  select.className = 'tone-dropdown';

  ['professional', 'friendly', 'casual'].forEach(tone => {
    const option = document.createElement('option');
    option.value = tone;
    option.textContent = tone.charAt(0).toUpperCase() + tone.slice(1);
    select.appendChild(option);
  });

  Object.assign(select.style, {
    marginRight: '8px',
    height: '36px',
    borderRadius: '4px',
    padding: '0 8px',
    fontSize: '14px'
  });

  return select;
}

// ─────────────────────────────────────────
// Toast
// ─────────────────────────────────────────
function showToast(message, isError = false) {
  const existing = document.querySelector('.ai-toast');
  if (existing) existing.remove();

  const toast = document.createElement('div');
  toast.className = 'ai-toast';
  toast.textContent = message;

  Object.assign(toast.style, {
    position: 'fixed',
    bottom: '24px',
    right: '24px',
    backgroundColor: isError ? '#d93025' : '#1a73e8',
    color: '#fff',
    padding: '12px 20px',
    borderRadius: '8px',
    zIndex: '99999'
  });

  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 3000);
}

// ─────────────────────────────────────────
// Create AI Button
// ─────────────────────────────────────────
function createAIButton(toneDropdown) {
  const button = document.createElement('div');
  button.className = 'ai-reply-button';
  button.textContent = '✦ AI Reply';

  Object.assign(button.style, {
    backgroundColor: '#1a73e8',
    color: '#fff',
    borderRadius: '20px',
    padding: '0 16px',
    height: '36px',
    marginRight: '8px',
    display: 'flex',
    alignItems: 'center',
    cursor: 'pointer'
  });

  // ─────────────────────────────────────────
  // CLICK EVENT (UPDATED)
  // ─────────────────────────────────────────
  button.addEventListener('click', () => {

    const replyBox = findReplyBox();
    const emailText = findOriginalEmailContent();

    if (!replyBox) {
      showToast("⚠️ Open reply box first", true);
      return;
    }

    if (!emailText) {
      showToast("⚠️ No email content found", true);
      return;
    }

    button.innerHTML = '⏳ Generating...';
    button.style.pointerEvents = 'none';

    chrome.runtime.sendMessage({
      action: "generateReply",
      data: {
        emailContent: emailText,
        tone: toneDropdown.value,
        senderName: findSenderName()
      }
    }, (response) => {

      // Restore button
      button.innerHTML = '✦ AI Reply';
      button.style.pointerEvents = 'auto';

      if (!response || !response.success) {
        showToast(`⚠️ ${response?.error || "Something went wrong"}`, true);
        return;
      }

      const formattedReply = response.data
        .replace(/\n\n/g, '<br><br>')
        .replace(/\n/g, '<br>');

      replyBox.innerHTML = formattedReply;
      replyBox.dispatchEvent(new InputEvent('input', { bubbles: true }));
      replyBox.focus();

      showToast("✅ Reply inserted!");
    });
  });

  return button;
}

// ─────────────────────────────────────────
// Inject UI
// ─────────────────────────────────────────
function injectButton() {

  const sendButton = findSendButton();
  if (!sendButton) {
    setTimeout(injectButton, 1000);
    return;
  }

  if (document.querySelector('.ai-reply-button')) return;

  const dropdown = createToneDropdown();
  const button = createAIButton(dropdown);

  sendButton.parentNode.insertBefore(dropdown, sendButton);
  sendButton.parentNode.insertBefore(button, sendButton);

  console.log("AI Button Injected ✅");
}

// ─────────────────────────────────────────
// Observe Gmail
// ─────────────────────────────────────────
const observer = new MutationObserver(() => {
  setTimeout(injectButton, 1000);
});

observer.observe(document.body, {
  childList: true,
  subtree: true
});