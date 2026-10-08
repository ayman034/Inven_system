(() => {
    const canEdit = currentRole === "STOREKEEPER";
    const formBox = document.getElementById("itemFormContainer");
    const form = document.getElementById("itemForm");
    const editBox = document.getElementById("editItemContainer");
    const editForm = document.getElementById("editItemForm");
    const body = document.getElementById("itemTableBody");
    const search = document.getElementById("searchItem");
    const stockModal = document.getElementById("stockModal");
    const stockForm = document.getElementById("stockForm");
    const stockError = document.getElementById("stockError");
    let items = [];

    if (!canEdit && formBox) formBox.remove();
    if (editBox) editBox.style.display = "none";

    function validatePositiveInteger(value) {
        if (value === "" || value === null || value === undefined) return "Quantity is required.";
        if (!/^\d+$/.test(String(value).trim())) return "Quantity must be a whole number greater than 0.";
        const qty = Number(value);
        if (!Number.isInteger(qty) || qty <= 0) return "Quantity must be greater than 0.";
        return "";
    }

    function closeStockModal() {
        stockModal?.classList.remove("open");
        stockForm?.reset();
        stockError.textContent = "";
    }

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
            <td>${canEdit ? `<div class="table-actions"><button class="btn btn-sm btn-primary" onclick="editItem(${i.id})">Edit</button>
            <button class="btn btn-sm btn-primary" onclick="openAddStockModal(${i.id})">Add Stock</button>
            <button class="btn btn-sm btn-danger" onclick="deleteItem(${i.id})">Delete</button></div>` : `<span class="view-only">View Only</span>`}</td>
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

    window.openAddStockModal = id => {
        if (!canEdit) return;
        const item = items.find(x => x.id === id); if (!item) return;
        document.getElementById("stockItemId").value = item.id;
        document.getElementById("stockItemName").value = item.name;
        document.getElementById("stockCurrentQuantity").value = item.quantity;
        document.getElementById("stockQuantityToAdd").value = "";
        stockError.textContent = "";
        stockModal.classList.add("open");
        document.getElementById("stockQuantityToAdd").focus();
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

    stockForm?.addEventListener("submit", async e => {
        e.preventDefault();
        const qtyInput = document.getElementById("stockQuantityToAdd");
        const error = validatePositiveInteger(qtyInput.value);
        if (error) {
            stockError.textContent = error;
            qtyInput.focus();
            return;
        }

        try {
            const id = Number(document.getElementById("stockItemId").value);
            const quantity = Number(qtyInput.value);
            await apiRequest(`/items/${id}/add-stock`, { method: "POST", body: JSON.stringify({ quantity }) });
            closeStockModal();
            await load();
            alert("Stock added successfully.");
        } catch (err) {
            stockError.textContent = err.message;
        }
    });

    window.deleteItem = async id => {
        if (!canEdit || !confirm("Delete this item?")) return;
        try { await apiRequest(`/items/${id}`, { method: "DELETE" }); await load(); }
        catch (err) { alert(err.message); }
    };

    document.getElementById("cancelEditBtn")?.addEventListener("click", () => editBox.style.display = "none");
    document.getElementById("cancelAddStockBtn")?.addEventListener("click", closeStockModal);
    document.getElementById("closeStockModalBtn")?.addEventListener("click", closeStockModal);
    stockModal?.addEventListener("click", e => {
        if (e.target === stockModal) closeStockModal();
    });
    search?.addEventListener("input", e => display(e.target.value));
    load().catch(err => alert(err.message));
})();
