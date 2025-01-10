//all code related to handling the loading animation

//on page load get the current value of the display for the loading animation
let contentDiv = document.getElementById("dashboard-content");
let loadingDiv = document.getElementById("loading");
sessionStorage.setItem("contentDisplay", window.getComputedStyle(contentDiv).display);
sessionStorage.setItem("loadingDisplay", window.getComputedStyle(loadingDiv).display);

//trigger the loading animation
function showLoading() {
    contentDiv.style.display = "none";
    loadingDiv.style.display = "flex";
    sessionStorage.setItem("contentDisplay", contentDiv.style.display);
    sessionStorage.setItem("loadingDisplay", loadingDiv.style.display);

}

//stop the loading animation
function hideLoading() {
    if (sessionStorage.getItem("contentDisplay") === "none") {
        contentDiv.style.display = "flex";
        loadingDiv.style.display = "none";
        sessionStorage.setItem("contentDisplay", contentDiv.style.display);
        sessionStorage.setItem("loadingDisplay", loadingDiv.style.display);
    }
}

//ensure that the loading is hidden when accessing the page normally and when navigating back to it
document.addEventListener('DOMContentLoaded', hideLoading);
window.addEventListener('pageshow', hideLoading);

