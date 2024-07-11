<#import "../fragments/shared-components.ftlh" as com>

<@com.page>
    <h1>
        ${message!"This request failed!"}
    </h1>
    <#if exception??>
        <p>${exception.message!}</p>
        <p>${exception.stacktrace!}</p>
    </#if>
</@com.page>

