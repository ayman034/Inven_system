(() => {
    const sidebar = document.querySelector(".sidebar");
    const sidebarMenu = document.getElementById("sidebarMenu");
    const userInfo = document.getElementById("userInfo");
    const topbar = document.querySelector(".topbar");
    const page = location.pathname.split("/").pop() || "dashboard.html";
    const links = [
        ["dashboard", "dashboard.html", "Dashboard", ""],
        ["items", "items.html", "Items", ""],
        ["rooms", "rooms.html", "Rooms", ""],
        ["inventory", "inventory.html", "Inventory", ""],
        ["reports", "reports.html", "Reports", ""],
        ["users", "users.html", "Users Management", ""]
    ];
    const access = {
        ADMIN: ["dashboard", "users", "items", "rooms", "reports"],
        STOREKEEPER: ["dashboard", "items", "rooms", "inventory", "reports"],
        SUPERVISOR: ["dashboard", "items", "rooms", "inventory", "reports"]
    };
    const allowed = access[currentRole] || [];

    if (sidebarMenu) {
        sidebarMenu.className = "sidebar-nav";
        sidebarMenu.innerHTML = links
            .filter(x => allowed.includes(x[0]))
            .map(([id, href, label, icon]) => `<a href="${href}" class="${page === href ? "active" : ""}"><span class="nav-icon" aria-hidden="true">${icon}</span><span>${label}</span></a>`)
            .join("") +
            `<div class="sidebar-spacer"></div><a href="index.html" class="logout-link" onclick="logout()">Logout</a>`;
    }

    if (userInfo) {
        const roleNames = { ADMIN: "System Administrator", STOREKEEPER: "Storekeeper", SUPERVISOR: "Supervisor" };
        userInfo.innerHTML = `<div class="user-details"><strong>${esc(sessionStorage.getItem("fullName") || currentUsername)}</strong><span class="role-pill">${roleNames[currentRole] || currentRole}</span></div>`;
    }

})();
