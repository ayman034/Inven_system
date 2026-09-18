document.addEventListener("DOMContentLoaded", async () => {
    try {
        const [items, rooms, inventory] = await Promise.all([apiRequest("/items"), apiRequest("/rooms"), apiRequest("/inventory")]);
        const totalStock = items.reduce((s,i) => s + i.quantity, 0);
        const allocatedStock = inventory.reduce((s,r) => s + r.quantity, 0);
        const remainingStock = Math.max(0, totalStock - allocatedStock);
        document.getElementById("itemCount").textContent = items.length;
        document.getElementById("roomCount").textContent = rooms.length;
        document.getElementById("stockCount").textContent = remainingStock;
        document.getElementById("allocatedCount").textContent = allocatedStock;

        const actions = {
            STOREKEEPER: `<a href="items.html" class="quick-action"><strong>Manage Items</strong><small>Add or edit items</small></a><a href="rooms.html" class="quick-action"><strong>Manage Rooms</strong><small>Add or edit rooms</small></a><a href="inventory.html" class="quick-action"><strong>Manage Inventory</strong><small>Allocate stock to rooms</small></a><a href="reports.html" class="quick-action"><strong>View Reports</strong><small>Check inventory reports</small></a>`,
            ADMIN: `<a href="users.html" class="quick-action"><strong>Manage Users</strong><small>Create and manage users</small></a><a href="items.html" class="quick-action"><strong>View Items</strong><small>Check registered items</small></a><a href="rooms.html" class="quick-action"><strong>View Rooms</strong><small>Check registered rooms</small></a><a href="reports.html" class="quick-action"><strong>View Reports</strong><small>Monitor system reports</small></a>`,
            SUPERVISOR: `<a href="items.html" class="quick-action"><strong>View Items</strong><small>Check registered items</small></a><a href="rooms.html" class="quick-action"><strong>View Rooms</strong><small>Check available rooms</small></a><a href="inventory.html" class="quick-action"><strong>View Inventory</strong><small>Monitor stock allocation</small></a><a href="reports.html" class="quick-action"><strong>View Reports</strong><small>Check inventory reports</small></a>`
        };
        document.getElementById("quickActions").innerHTML = actions[currentRole] || "";
        const percentage = totalStock ? Math.min(100, Math.round(allocatedStock / totalStock * 100)) : 0;
        document.getElementById("progressBox").innerHTML = `<div class="progress-info"><div><strong>Stock Allocation</strong><span>${percentage}% allocated</span></div><div class="progress-bar"><div class="progress-fill" style="width:${percentage}%"></div></div><div class="progress-details"><span>Total Stock: <strong>${totalStock}</strong></span><span>Allocated: <strong>${allocatedStock}</strong></span><span>Remaining: <strong>${remainingStock}</strong></span></div></div>`;
    } catch (err) { alert(err.message); }
});
