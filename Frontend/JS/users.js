(() => {
    const form = document.getElementById("userForm");
    const body = document.getElementById("userTableBody");
    let users = [];

    async function load() {
        users = await apiRequest("/users");
        display();
    }
    function display() {
        body.innerHTML = users.length ? users.map(u => `<tr><td>${u.id}</td><td>${esc(u.fullName)}</td><td>${esc(u.username)}</td><td>${u.role}</td><td>${u.disabled ? "Disabled" : "Active"}</td><td><button class="btn btn-sm btn-primary" onclick="editUser(${u.id})">Edit</button> <button class="btn btn-sm ${u.disabled ? "btn-success" : "btn-warning"}" onclick="toggleUser(${u.id})">${u.disabled ? "Enable" : "Disable"}</button></td></tr>`).join("") : `<tr><td colspan="6" class="empty-state">No users created yet</td></tr>`;
    }
    form?.addEventListener("submit", async e => {
        e.preventDefault();
        try {
            await apiRequest("/users", { method: "POST", body: JSON.stringify({
                fullName: document.getElementById("fullName").value.trim(), username: document.getElementById("userName").value.trim(),
                password: document.getElementById("userPassword").value, role: document.getElementById("role").value
            })});
            form.reset(); await load(); alert("User created successfully.");
        } catch (err) { alert(err.message); }
    });
    window.editUser = async id => {
        const u = users.find(x => x.id === id); if (!u) return;
        const fullName = prompt("Enter full name:", u.fullName); if (fullName === null) return;
        const role = prompt("Enter role (ADMIN, STOREKEEPER, SUPERVISOR):", u.role); if (role === null) return;
        const newRole = role.trim().toUpperCase(); if (!["ADMIN","STOREKEEPER","SUPERVISOR"].includes(newRole)) return alert("Invalid role.");
        const password = prompt("New password (leave empty to keep current):", "");
        try { await apiRequest(`/users/${id}`, { method: "PUT", body: JSON.stringify({ fullName: fullName.trim(), role: newRole, password: password || null }) }); await load(); }
        catch (err) { alert(err.message); }
    };
    window.toggleUser = async id => {
        if (!confirm("Change this user's access status?")) return;
        try { await apiRequest(`/users/${id}/toggle`, { method: "PATCH" }); await load(); }
        catch (err) { alert(err.message); }
    };
    load().catch(err => alert(err.message));
})();
