const API_BASE_URL = "http://localhost:8080/api";

async function apiRequest(path, options = {}) {
    const headers = { ...(options.headers || {}) };
    const token = sessionStorage.getItem("token");
    if (token) headers.Authorization = `Bearer ${token}`;
    if (options.body && !headers["Content-Type"]) headers["Content-Type"] = "application/json";

    const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });
    let data = null;
    try { data = await response.json(); } catch { data = null; }

    if (response.status === 401) {
        sessionStorage.clear();
        window.location.href = "index.html";
        throw new Error("Session expired. Please login again.");
    }

    if (!response.ok) {
        const message = data?.message ||
            (data?.errors ? Object.values(data.errors).join(" ") : "Request failed.");
        throw new Error(message);
    }
    return data;
}

function esc(value) {
    return String(value ?? "").replace(/[&<>'"]/g, c => ({
        "&": "&amp;", "<": "&lt;", ">": "&gt;", "'": "&#39;", '"': "&quot;"
    }[c]));
}
