(() => {
    const canEdit = currentRole === "STOREKEEPER";
    const formBox = document.getElementById("inventoryFormContainer");
    const form = document.getElementById("inventoryForm");
    const itemSel = document.getElementById("inventoryItem");
    const roomSel = document.getElementById("inventoryRoom");
    const qty = document.getElementById("sendQuantity");
    const sumBody = document.getElementById("inventorySummaryBody");
    const tableBody = document.getElementById("inventoryTableBody");
    let items = [], rooms = [], inventory = [];
    if (!canEdit && formBox) formBox.remove();

    const allocated = id => inventory.filter(x => x.itemId === id).reduce((s,x) => s + x.quantity, 0);
    const remaining = id => Math.max(0, (items.find(x => x.id === id)?.quantity || 0) - allocated(id));

    async function load() {
        [items, rooms, inventory] = await Promise.all([apiRequest("/items"), apiRequest("/rooms"), apiRequest("/inventory")]);
        itemSel.innerHTML = '<option value="">Select Item</option>' + items.map(i => `<option value="${i.id}">${esc(i.name)} - Remaining: ${remaining(i.id)}</option>`).join("");
        roomSel.innerHTML = '<option value="">Select Room</option>' + rooms.map(r => `<option value="${r.id}">${esc(r.name)}</option>`).join("");
        display();
    }

    function display() {
        sumBody.innerHTML = items.length ? items.map(i => `<tr><td><strong>${esc(i.name)}</strong></td><td>${i.quantity}</td><td>${allocated(i.id)}</td><td><strong>${remaining(i.id)}</strong></td></tr>`).join("") : `<tr><td colspan="4" class="empty-state">No items registered yet</td></tr>`;
        tableBody.innerHTML = inventory.length ? inventory.map(r => `<tr>
            <td>${r.id}</td><td>${esc(r.itemName)}</td><td>${esc(r.roomName)}</td><td><strong>${r.quantity}</strong></td>
            <td>${canEdit ? `<button class="btn btn-sm btn-primary" onclick="editInventory(${r.id})">Edit</button>
            <button class="btn btn-sm btn-success" onclick="addQuantity(${r.id})">+ Add</button>
            <button class="btn btn-sm btn-warning" onclick="removeQuantity(${r.id})">- Remove</button>
            <button class="btn btn-sm btn-danger" onclick="deleteInventory(${r.id})">Delete</button>` : `<span class="view-only">View Only</span>`}</td>
        </tr>`).join("") : `<tr><td colspan="5" class="empty-state">No stock has been allocated yet</td></tr>`;
    }

    form?.addEventListener("submit", async e => {
        e.preventDefault();
        try {
            const itemId = Number(itemSel.value), roomId = Number(roomSel.value), quantity = Number(qty.value);
            if (!itemId || !roomId || !Number.isInteger(quantity) || quantity <= 0) throw new Error("Please select item, room and valid quantity.");
            await apiRequest("/inventory", { method: "POST", body: JSON.stringify({ itemId, roomId, quantity }) });
            form.reset(); await load(); alert("Item allocated successfully.");
        } catch (err) { alert(err.message); }
    });

    window.editInventory = async id => {
        if (!canEdit) return;
        const record = inventory.find(x => x.id === id); if (!record) return;
        const quantity = Number(prompt(`Quantity for ${record.itemName} in ${record.roomName}:`, record.quantity));
        if (!Number.isInteger(quantity) || quantity <= 0) return;
        try { await apiRequest(`/inventory/${id}`, { method: "PUT", body: JSON.stringify({ itemId: record.itemId, roomId: record.roomId, quantity }) }); await load(); }
        catch (err) { alert(err.message); }
    };
    window.addQuantity = async id => {
        const quantity = Number(prompt("Quantity to add:")); if (!Number.isInteger(quantity) || quantity <= 0) return;
        try { await apiRequest(`/inventory/${id}/add`, { method: "PATCH", body: JSON.stringify({ quantity }) }); await load(); }
        catch (err) { alert(err.message); }
    };
    window.removeQuantity = async id => {
        const quantity = Number(prompt("Quantity to remove:")); if (!Number.isInteger(quantity) || quantity <= 0) return;
        try { await apiRequest(`/inventory/${id}/remove`, { method: "PATCH", body: JSON.stringify({ quantity }) }); await load(); }
        catch (err) { alert(err.message); }
    };
    window.deleteInventory = async id => {
        if (!confirm("Delete this inventory record?")) return;
        try { await apiRequest(`/inventory/${id}`, { method: "DELETE" }); await load(); }
        catch (err) { alert(err.message); }
    };
    load().catch(err => alert(err.message));
})();
