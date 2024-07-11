<h2>Products</h2>

<#list products as p>
    <div class="card mb-3">
        <div class="card-body">
            <h5 class="card-title">${p.name}</h5>
            <h6 class="card-subtitle mb-2 text-muted">Id: ${p.id}</h6>
            <p class="card-text">${p.desc}</p>
            <a href="/products/${p.id}" class="card-link">Details</a>
        </div>
    </div>
</#list>
