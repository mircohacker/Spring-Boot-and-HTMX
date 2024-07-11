<div class="toast show" role="alert" aria-live="assertive" aria-atomic="true">
    <div class="toast-header bg-danger-subtle">
        <strong class="me-auto">An Error occurred</strong>
        <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
    <div class="toast-body ">
        <p>${message!"This request failed!"}</p>
        <#if exception??>
            <p>${exception.message!}</p>
        </#if>
    </div>
</div>
