const loginForm = document.getElementById("loginForm");
loginForm?.addEventListener("submit", async (event) => {
    event.preventDefault();
    const message = document.getElementById("message");
    message.textContent = "";
    try {
        const data = await apiRequest("/auth/login", {
            method: "POST",
            body: JSON.stringify({
                username: document.getElementById("username").value.trim(),
                password: document.getElementById("password").value
            })
        });
        sessionStorage.setItem("token", data.token);
        sessionStorage.setItem("username", data.username);
        sessionStorage.setItem("fullName", data.fullName || "");
        sessionStorage.setItem("role", data.role);
        window.location.href = "dashboard.html";
    } catch (error) {
        message.textContent = error.message || "Unable to login.";
        message.style.color = "red";
    }
});
