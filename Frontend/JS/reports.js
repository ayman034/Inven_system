document.addEventListener("DOMContentLoaded", async () => {
    try {
        const [items, rooms, inventory] = await Promise.all([apiRequest("/items"), apiRequest("/rooms"), apiRequest("/inventory")]);
        const totalStock = items.reduce((s,i) => s + i.quantity, 0);
        const allocatedStock = inventory.reduce((s,r) => s + r.quantity, 0);
        const remainingStock = Math.max(0, totalStock - allocatedStock);
        document.getElementById("totalItemsReport").textContent = items.length;
        document.getElementById("totalRoomsReport").textContent = rooms.length;
        document.getElementById("currentStockReport").textContent = remainingStock;
        document.getElementById("allocatedStockReport").textContent = allocatedStock;

        const status = r => r.remaining === 0 ? "Out of Stock" : r.remaining <= 5 ? "Low Stock" : "Available";
        document.getElementById("reportTableBody").innerHTML = items.length ? items.map(i => `<tr><td>${i.id}</td><td><strong>${esc(i.name)}</strong></td><td>${esc(i.category)}</td><td>${i.quantity}</td><td>${i.allocated}</td><td>${i.remaining}</td><td>${status(i)}</td></tr>`).join("") : `<tr><td colspan="7" class="empty-state">No items registered yet</td></tr>`;
        const low = items.filter(i => i.remaining <= 5);
        document.getElementById("lowStockTableBody").innerHTML = low.length ? low.map(i => `<tr><td>${esc(i.name)}</td><td>${i.quantity}</td><td>${i.allocated}</td><td>${i.remaining}</td><td>${i.remaining === 0 ? "Out of Stock" : "Low Stock"}</td></tr>`).join("") : `<tr><td colspan="5" class="empty-state">No low stock items</td></tr>`;
        const progress = totalStock ? Math.min(100, Math.round(allocatedStock / totalStock * 100)) : 0;
        document.getElementById("reportProgress").innerHTML = `<div class="progress-info"><div><strong>Stock Allocation</strong><span>${progress}% allocated</span></div><div class="progress-bar"><div class="progress-fill" style="width:${progress}%"></div></div><div class="progress-details"><span>Total: <strong>${totalStock}</strong></span><span>Allocated: <strong>${allocatedStock}</strong></span><span>Remaining: <strong>${remainingStock}</strong></span></div></div>`;

        const printBtn = document.getElementById("printReportBtn");
        if (currentRole !== "SUPERVISOR") { printBtn?.remove(); }
        else printBtn?.addEventListener("click", () => {
            const now = new Date();
            const days = ["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"];
            const pad = n => String(n).padStart(2, "0");
            document.getElementById("printDay").textContent = days[now.getDay()];
            document.getElementById("printDate").textContent = `${pad(now.getDate())}/${pad(now.getMonth()+1)}/${now.getFullYear()}`;
            document.getElementById("printTime").textContent = `${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`;
            document.getElementById("printUser").textContent = sessionStorage.getItem("fullName") || currentUsername;
            window.print();
        });
    } catch (err) { alert(err.message); }
});
