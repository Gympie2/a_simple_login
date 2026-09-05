document.querySelectorAll("[data-password-toggle]").forEach((toggle) => {
    const field = document.getElementById(toggle.dataset.passwordToggle);
    if (!field) {
        return;
    }

    toggle.addEventListener("click", () => {
        const isVisible = field.type === "text";
        field.type = isVisible ? "password" : "text";
        toggle.textContent = isVisible ? "Show" : "Hide";
        toggle.setAttribute("aria-pressed", String(!isVisible));
    });
});
