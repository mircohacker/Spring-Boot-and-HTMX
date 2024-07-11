<#macro page errors=""><!doctype html>
<html lang="en">

<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <title>SSR Spring boot demo</title>

    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <script src="/webjars/bootstrap/js/bootstrap.bundle.min.js"></script>

    <script src="/webjars/htmx.org/dist/htmx.min.js"></script>
    <script src="/webjars/htmx.org/dist/ext/response-targets.js"></script>

    <link rel="stylesheet" type="text/css" href="/global.css"/>
    <script src="/navbar-highlighting.js"></script>

    <#include "vue-imports.ftlh">

</head>

<body
        hx-target="#container"
        hx-boost="true"
        hx-indicator="#spinner"
        hx-ext="response-targets"
        hx-target-error="#any-errors"
>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand"
           href="/"
        >Spring boot SSR example</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <@navItem href="/products" text="Products"/>
                <@navItem href="/vue-app" text="display embedded Vue App"/>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        Errors
                    </a>
                    <ul class="dropdown-menu">
                        <@dropdownItem href="/triggerError" text="Graceful Error"/>
                        <@dropdownItem href="/exception" text="Uncaugt Exception"/>
                        <@dropdownItem href="/nothing/is/here" text="Not Found"/>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container my-5">
    <div id="container">
        <#nested/>
    </div>
    <div id="any-errors" class="toast-container position-fixed bottom-0 end-0 p-3">
        ${errors!""}
    </div>
    <div id="spinner" class="spinner-grow loading-indicator" role="status">
        <span class="visually-hidden">Loading...</span>
    </div>
</div>
</body>
</html>

</#macro>

<#macro navItem href text>
    <li class="nav-item">
        <a class="nav-link"
           href="${href}">
            ${text}
        </a>
    </li>
</#macro>

<#macro dropdownItem href text>
    <li>
        <a class="dropdown-item"
           href="${href}">
            ${text}
        </a>
    </li>
</#macro>