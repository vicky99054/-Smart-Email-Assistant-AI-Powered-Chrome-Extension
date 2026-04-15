const API_URL = "https://smart-email-backend-69hz.onrender.com/email";

chrome.runtime.onMessage.addListener((request, sender, sendResponse) => {

  if (request.action === "generateReply") {

    (async () => {
      try {
        const res = await fetch(API_URL, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(request.data)
        });

        const text = await res.text();

        sendResponse({
          success: res.ok,
          data: text,
          error: res.ok ? null : text
        });

      } catch (err) {
        sendResponse({
          success: false,
          error: "Server not reachable"
        });
      }
    })();

    return true;
  }
});