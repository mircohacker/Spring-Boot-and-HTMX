const updateNavActive = () => {
  document.activeElement && document.activeElement.blur(); // remove focus from clicked link
  document.querySelectorAll(`a.nav-link.active`).forEach(e => e.classList.remove("active"))
  document.querySelectorAll(`a.nav-link[href$="${location.pathname}"]`).forEach(e => e.classList.add("active"))
}

htmx.on("htmx:afterSettle", updateNavActive)
document.addEventListener("DOMContentLoaded", updateNavActive);