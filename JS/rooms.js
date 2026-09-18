(() => {
    const canEdit = currentRole === "STOREKEEPER";
    const formBox = document.getElementById("roomFormContainer");
    const form = document.getElementById("roomForm");
    const body = document.getElementById("roomTableBody");
    const contents = document.getElementById("roomContents");
    const search = document.getElementById("searchRoom");
    let rooms = [], inventory = [];

    if (!canEdit && formBox) formBox.remove();

    async function load() {
        [rooms, inventory] = await Promise.all([apiRequest("/rooms"), apiRequest("/inventory")]);
        display(search?.value || "");
    }

    function display(term = "") {
        const q = term.trim().toLowerCase();
        const list = rooms.filter(r => r.name.toLowerCase().includes(q));
        body.innerHTML = list.length ? list.map(r => `<tr>
            <td>${r.id}</td><td><strong>${esc(r.name)}</strong></td><td>${r.allocated}</td>
            <td>${canEdit ? `<button class="btn btn-sm btn-primary" onclick="editRoom(${r.id})">Edit</button>
            <button class="btn btn-sm btn-danger" onclick="deleteRoom(${r.id})">Delete</button>` : `<span class="view-only">View Only</span>`}</td>
        </tr>`).join("") : `<tr><td colspan="4" class="empty-state">No rooms registered yet</td></tr>`;

        contents.innerHTML = list.length ? list.map(room => {
            const rows = inventory.filter(x => x.roomId === room.id && x.quantity > 0);
            return `<div class="room-content-box"><div class="room-content-title"><strong>${esc(room.name)}</strong></div>
                ${rows.length ? `<div class="table-responsive"><table><thead><tr><th>Item</th><th>Category</th><th>Quantity</th></tr></thead><tbody>${rows.map(x => `<tr><td>${esc(x.itemName)}</td><td>${esc(x.category)}</td><td>${x.quantity}</td></tr>`).join("")}</tbody></table></div>` : `<p class="empty-state">No items placed in this room.</p>`}</div>`;
        }).join("") : `<p class="empty-state">No rooms registered yet</p>`;
    }

    form?.addEventListener("submit", async e => {
        e.preventDefault();
        try {
            await apiRequest("/rooms", { method: "POST", body: JSON.stringify({ name: document.getElementById("roomName").value.trim() }) });
            form.reset(); await load(); alert("Room added successfully.");
        } catch (err) { alert(err.message); }
    });

    window.editRoom = async id => {
        if (!canEdit) return;
        const room = rooms.find(r => r.id === id); if (!room) return;
        const name = prompt("Enter room name:", room.name); if (name === null) return;
        try { await apiRequest(`/rooms/${id}`, { method: "PUT", body: JSON.stringify({ name: name.trim() }) }); await load(); }
        catch (err) { alert(err.message); }
    };

    window.deleteRoom = async id => {
        if (!canEdit || !confirm("Delete this room?")) return;
        try { await apiRequest(`/rooms/${id}`, { method: "DELETE" }); await load(); }
        catch (err) { alert(err.message); }
    };

    search?.addEventListener("input", e => display(e.target.value));
    load().catch(err => alert(err.message));
})();
