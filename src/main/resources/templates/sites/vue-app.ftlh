<#import "../fragments/shared-components.ftlh" as com>

<@com.page>
    <script>
      document.body.addEventListener("vueReady", () => {
        document.body.dispatchEvent(new Event("mountVueApp"))
      })
    </script>
    <#include "../fragments/vue-app.ftlh">
</@com.page>