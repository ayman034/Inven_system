(() => {
    const canEdit = currentRole === "STOREKEEPER";
    const formBox = document.getElementById("itemFormContainer");
    const form = document.getElementById("itemForm");
    const editBox = document.getElementById("editItemContainer");
    const editForm = document.getElementById("editItemForm");
    const body = document.getElementById("itemTableBody");
    const search = document.getElementById("searchItem");
    let items = [];

    if (!canEdit && formBox) formBox.remove();
    if (editBox) editBox.style.display = "none";

    async function load() {
        items = await apiRequest("/items");
        display(search?.value || "");
    }

    function display(term = "") {
        const q = term.trim().toLowerCase();
        const list = items.filter(i => `${i.name} ${i.category}`.toLowerCase().includes(q));
        body.innerHTML = list.length ? list.map(i => `<tr>
            <td>${i.id}</td><td><strong>${esc(i.name)}</strong></td><td>${esc(i.category)}</td>
            <td>${i.quantity}</td><td>${i.allocated}</td><td><strong>${i.remaining}</strong></td>
            <td>${canEdit ? `<button class="btn btn-sm btn-primary" onclick="editItem(${i.id})">Edit</button>
            <button class="btn btn-sm btn-danger" onclick="deleteItem(${i.id})">Delete</button>` : `<span class="view-only">View Only</span>`}</td>
        </tr>`).join("") : `<tr><td colspan="7" class="empty-state">No items registered yet</td></tr>`;
    }

    form?.addEventListener("submit", async e => {
        e.preventDefault();
        try {
            await apiRequest("/items", { method: "POST", body: JSON.stringify({
                name: document.getElementById("itemName").value.trim(),
                category: document.getElementById("category").value.trim(),
                quantity: Number(document.getElementById("quantity").value)
            })});
            form.reset(); await load(); alert("Item added successfully.");
        } catch (err) { alert(err.message); }
    });

    window.editItem = id => {
        if (!canEdit) return;
        const item = items.find(x => x.id === id); if (!item) return;
        document.getElementById("editItemId").value = item.id;
        document.getElementById("editItemName").value = item.name;
        document.getElementById("editCategory").value = item.category;
        document.getElementById("editQuantity").value = item.quantity;
        editBox.style.display = "block";
        editBox.scrollIntoView({ behavior: "smooth" });
    };

    editForm?.addEventListener("submit", async e => {
        e.preventDefault();
        try {
            const id = Number(document.getElementById("editItemId").value);
            await apiRequest(`/items/${id}`, { method: "PUT", body: JSON.stringify({
                name: document.getElementById("editItemName").value.trim(),
                category: document.getElementById("editCategory").value.trim(),
                quantity: Number(document.getElementById("editQuantity").value)
            })});
            editBox.style.display = "none"; await load(); alert("Item updated successfully.");
        } catch (err) { alert(err.message); }
    });

    window.deleteItem = async id => {
        if (!canEdit || !confirm("Delete this item?")) return;
        try { await apiRequest(`/items/${id}`, { method: "DELETE" }); await load(); }
        catch (err) { alert(err.message); }
    };

    document.getElementById("cancelEditBtn")?.addEventListener("click", () => editBox.style.display = "none");
    search?.addEventListener("input", e => display(e.target.value));
    load().catch(err => alert(err.message));
})();
