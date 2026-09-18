const currentUsername = sessionStorage.getItem("username");
const currentRole = String(sessionStorage.getItem("role") || "").toUpperCase();

if (!sessionStorage.getItem("token") || !currentUsername || !currentRole) {
    window.location.href = "index.html";
}

function logout() {
    sessionStorage.clear();
    window.location.href = "index.html";
}

function hasRole(...roles) {
    return roles.includes(currentRole);
}

function checkRole(roles) {
    if (!roles.includes(currentRole)) {
        alert("You are not authorized to access this page.");
        window.location.href = "dashboard.html";
    }
}

function protectSection(section) {
    const access = {
        dashboard: ["ADMIN", "STOREKEEPER", "SUPERVISOR"],
        users: ["ADMIN"],
        items: ["ADMIN", "STOREKEEPER", "SUPERVISOR"],
        rooms: ["ADMIN", "STOREKEEPER", "SUPERVISOR"],
        inventory: ["STOREKEEPER", "SUPERVISOR"],
        reports: ["ADMIN", "STOREKEEPER", "SUPERVISOR"]
    };
    if (!(access[section] || []).includes(currentRole)) {
        alert("You do not have access to this section.");
        window.location.href = "dashboard.html";
    }
}
