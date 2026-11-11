let productPage = 1;
let orderPage = 1;

const PAGE_SIZE = 4;

let currentSection = "";

const BASE_URL = "http://localhost:8080";

document.addEventListener("DOMContentLoaded", () => {

    loadStats();

});

async function loadStats() {

    const response =
        await fetch(`${BASE_URL}/dashboard/stats`);

    const stats = await response.json();

    document.getElementById("totalProducts").textContent =
        stats.totalProducts;

    document.getElementById("totalOrders").textContent =
        stats.totalOrders;

    document.getElementById("availableStock").textContent =
        stats.availableStock;

    document.getElementById("cancelledOrders").textContent =
        stats.cancelledOrders;
}

function openAddProductModal() {

    document
        .getElementById("addProductModal")
        .style.display = "flex";
}

async function saveProduct() {

    const product = {

        name: document.getElementById(
            "productName").value,

        price: Number(
            document.getElementById(
                "productPrice").value),

        stock: Number(
            document.getElementById(
                "productStock").value)
    };
    if (!product.name.trim()) {

        showToast(
            "❌ Product name is required"
        );

        return;
    }
    if (product.price <= 0) {

        showToast(
            "❌ Price must be greater than 0"
        );

        return;
    }
    if (product.stock <= 0) {

        showToast(
            "❌ Stock must be greater than 0"
        );

        return;
    }


    try {

        const response = await fetch(

            `${BASE_URL}/products`,

            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify(product)
            }
        );

        if (response.ok) {

            document.getElementById(
                "addProductModal"
            ).style.display = "none";

            document.getElementById(
                "productName"
            ).value = "";

            document.getElementById(
                "productPrice"
            ).value = "";

            document.getElementById(
                "productStock"
            ).value = "";

            showToast(
                "✅ Product added successfully!"
            );

            loadStats();

        } else {

            const errorResponse =
                await response.json();

            showToast(
                `❌ ${errorResponse.message}`
            );
        }

    } catch (error) {

        showToast(
            "❌ Failed to add product!"
        );

        console.error(error);
    }
}

async function openPlaceOrderModal() {

    const response =
        await fetch(
            `${BASE_URL}/products`
        );

    const products =
        await response.json();

    const dropdown =
        document.getElementById(
            "orderProduct"
        );

    dropdown.innerHTML = "";

    products.forEach(product => {

        dropdown.innerHTML += `

            <option value="${product.id}">
                ${product.name}
            </option>

        `;
    });

    document
        .getElementById(
            "placeOrderModal")
        .style.display = "flex";
}

async function placeOrder() {

    const order = {

        productId: Number(
            document.getElementById(
                "orderProduct").value),

        quantity: Number(
            document.getElementById(
                "orderQuantity").value)
    };

    try {

        const response = await fetch(

            `${BASE_URL}/orders`,

            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/json"
                },

                body: JSON.stringify(order)
            }
        );

        if (response.ok) {

            document.getElementById(
                "placeOrderModal"
            ).style.display = "none";

            document.getElementById(
                "orderQuantity"
            ).value = "";

            showToast(
                "✅ Order placed successfully!"
            );

            loadStats();

        } else {

            const error =
                await response.text();

            showToast(
                `❌ Not enough stock available. Please reduce the quantity.`
            );
        }

    } catch (error) {

        showToast(
            "❌ Failed to place order!"
        );

        console.error(error);
    }
}


function clearContent() {

    document.getElementById("contentArea").innerHTML = "";
}
async function showStockDistribution() {
    if(!toggleSection("distribution")){

        return;
    }
    clearContent();

    const response =
        await fetch(`${BASE_URL}/products`);

    const products =
        await response.json();

    document.getElementById("contentArea")
        .innerHTML = `

    <h2>📈 Stock Distribution</h2>

    <div class="chart-container">
        <canvas id="stockChart"></canvas>
    </div>

`;


    const labels =
        products.map(product => product.name);

    const data =
        products.map(product => product.stock);

    new Chart(
        document.getElementById("stockChart"),
        {
            type: "bar",

            data: {

                labels: labels,

                datasets: [{
                    data: data,

                    backgroundColor: [
                        "#f59e0b",
                        "#fbbf24",
                        "#d97706",
                        "#fcd34d",
                        "#f97316",

                        "#fb923c",
                        "#fdba74",
                        "#ea580c",
                        "#facc15",
                        "#fde68a"
                    ]
                }]
            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                indexAxis: 'y',

                plugins: {

                    legend: {

                        display: false
                    }
                },

                scales: {

                    x: {

                        ticks: {

                            color: "#ffffff"
                        },

                        grid: {

                            color: "rgba(255,255,255,0.1)"
                        }
                    },

                    y: {

                        ticks: {

                            color: "#ffffff",

                            font: {

                                size: 14
                            }
                        },

                        grid: {

                            display: false
                        }
                    }
                }
            }
        }
    );
}

async function showProducts(page = productPage, fromPagination = false) {

    if (!fromPagination) {

        if (!toggleSection("products")) {
            return;
        }
    }

    const response =
        await fetch(`${BASE_URL}/products`);

    const products =
        await response.json();

    productPage = page;

    const start =
        (page - 1) * PAGE_SIZE;

    const end =
        start + PAGE_SIZE;

    const paginatedProducts =
        products.slice(start, end);

    const totalPages =
        Math.ceil(products.length / PAGE_SIZE);

    let html = `

        <h2>📦 Product Inventory</h2>

        <table>

            <thead>

                <tr>

                    <th>📦 Product Name</th>
                    <th>💰 Price</th>
                    <th>📊 Available Stock</th>
                    <th>🔄 Version</th>

                </tr>

            </thead>

            <tbody>
    `;

    paginatedProducts.forEach(product => {

        html += `

            <tr>

                <td>${product.name}</td>

                <td>${product.price}</td>

                <td>${product.stock}</td>

                <td>${product.version}</td>

            </tr>
        `;
    });

    html += `

            </tbody>

        </table>

        <div class="pagination">

            <button
                ${page === 1 ? "disabled" : ""}
                onclick="showProducts(${page - 1}, true)">

                ◀ Previous

            </button>

            <span>
                Page ${page} of ${totalPages}
            </span>

            <button
                ${page === totalPages ? "disabled" : ""}
                onclick="showProducts(${page + 1}, true)">

                Next ▶

            </button>

        </div>
    `;

    document.getElementById(
        "contentArea"
    ).innerHTML = html;
}

async function showOrders(page = orderPage, fromPagination = false) {

    if (!fromPagination) {

        if (!toggleSection("orders")) {
            return;
        }
    }

    const response =
        await fetch(`${BASE_URL}/orders`);

    let orders =
        await response.json();

    orders = orders.reverse();

    orderPage = page;

    const start =
        (page - 1) * PAGE_SIZE;

    const end =
        start + PAGE_SIZE;

    const paginatedOrders =
        orders.slice(start, end);

    const totalPages =
        Math.ceil(orders.length / PAGE_SIZE);

    let html = `

        <h2>🛒 Recent Orders</h2>

        <table>

            <thead>

                <tr>

                    <th>📦 Product Name</th>
                    <th>🔢 Quantity</th>
                    <th>📊 Status</th>
                    <th>⚙️ Action</th>

                </tr>

            </thead>

            <tbody>
    `;

    paginatedOrders.forEach(order => {

        html += `

            <tr>

                <td>${order.productName}</td>

                <td>${order.quantity}</td>

                <td class="${
            order.status === "PLACED"
                ? "status-placed"
                : "status-cancelled"
        }">

                    ${order.status}

                </td>

                <td>

                    ${
            order.status === "PLACED"

                ?

                `
                        <button
                            class="cancel-btn"
                            onclick="cancelOrder(${order.id})">

                            Cancel

                        </button>
                        `

                :

                `
                        Cancelled
                        `
        }

                </td>

            </tr>
        `;
    });

    html += `

            </tbody>

        </table>

        <div class="pagination">

            <button
                ${page === 1 ? "disabled" : ""}
                onclick="showOrders(${page - 1}, true)">

                ◀ Previous

            </button>

            <span>
                Page ${page} of ${totalPages}
            </span>

            <button
                ${page === totalPages ? "disabled" : ""}
                onclick="showOrders(${page + 1}, true)">

                Next ▶

            </button>

        </div>
    `;

    document.getElementById(
        "contentArea"
    ).innerHTML = html;
}

async function showLowStockProducts() {
    if(!toggleSection("lowstock")){

        return;
    }
    clearContent();

    const response =
        await fetch(`${BASE_URL}/products`);

    const products = await response.json();

    const filtered =
        products.filter(product => product.stock > 0 && product.stock <= 5);

    let html =
        `<h2>⚠️ Low Stock Products</h2>`;

    if (filtered.length === 0) {

        html += `
        <p style="margin-top:20px">
            No low stock products found.
        </p>
    `;
    }

    html += `
<div class="low-stock-grid">
`;

    filtered.forEach(product => {

        html += `
        <div class="low-stock-card">

            <h3>📦 ${product.name}</h3>

            <p>
                <strong>Current Stock:</strong>
                ${product.stock}
            </p>

            <p class="danger-status">
                ⚠️ Low Stock Alert
            </p>

        </div>
    `;
    });

    html += `
</div>
`;

    document.getElementById("contentArea").innerHTML =
        html;
}

async function showInventoryHealth() {

    if(!toggleSection("health")){

        return;
    }

    clearContent();

    const response =
        await fetch(`${BASE_URL}/products`);

    const products = await response.json();

    const inStock =
        products.filter(p => p.stock > 5).length;

    const lowStock =
        products.filter(
            p => p.stock > 0 && p.stock <= 5
        ).length;

    const outOfStock =
        products.filter(
            p => p.stock === 0
        ).length;

    document.getElementById("contentArea").innerHTML = `
        <h2>📊 Inventory Health</h2>

        <div class="health-grid">

            <div class="health-card">
                <h2 class="in-stock">${inStock}</h2>
                <p>In Stock Products</p>
            </div>

            <div class="health-card">
                <h2 class="low-stock">${lowStock}</h2>
                <p>Low Stock Products</p>
            </div>

            <div class="health-card">
                <h2 class="out-stock">${outOfStock}</h2>
                <p>Out Of Stock Products</p>
            </div>

        </div>
    `;
}

function closeAddProductModal() {

    document
        .getElementById("addProductModal")
        .style.display = "none";
}

function closePlaceOrderModal() {

    document
        .getElementById("placeOrderModal")
        .style.display = "none";
}
function showToast(message) {

    const toast =
        document.getElementById("toast");

    toast.textContent = message;

    toast.classList.add("show");

    setTimeout(() => {

        toast.classList.remove("show");

    }, 3000);
}
async function cancelOrder(orderId) {

    try {

        const response =
            await fetch(

                `${BASE_URL}/orders/${orderId}/cancel`,

                {
                    method: "POST"
                }
            );

        if (response.ok) {

            showToast(
                "✅ Order cancelled successfully!"
            );

            loadStats();

            showOrders(
                orderPage,
                true
            );

        } else {

            const errorResponse =
                await response.json();

            showToast(
                `❌ ${errorResponse.message}`
            );
        }

    } catch (error) {

        showToast(
            "❌ Failed to cancel order!"
        );

        console.error(error);
    }
}

function hideContentArea() {

    document.getElementById(
        "contentArea"
    ).style.display = "none";

    document.getElementById(
        "contentArea"
    ).innerHTML = "";

    currentSection = "";
}

function toggleSection(sectionName) {

    if (currentSection === sectionName) {

        hideContentArea();

        return false;
    }

    currentSection = sectionName;

    document.getElementById(
        "contentArea"
    ).style.display = "block";

    return true;
}