let tabs = document.getElementsByClassName("tab");
let tabHeaders = document.getElementsByClassName("tab-header");


for (let i = 0; i < tabs.length; i++) {
    tabHeaders[i].addEventListener("click", function () {
        showTab(tabHeaders[i], tabs[i]);
    });
}


//clicking a tab will pull up the correct content and show the tab header as selected
function showTab(curTabHeader, curTab) {
    for (let i = 0; i < tabs.length; i++) {
        tabs[i].style.display = "none";
        tabHeaders[i].style.color = "rgba(255, 255, 255, 0.5)";
    }
    curTab.style.display = "flex";
    curTabHeader.style.color = "#f2f2f2";
}
