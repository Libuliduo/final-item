/*
实现逻辑：
    通过 localStorage 记录用户是否启用暗黑模式（"active" / null）
    当用户点击主题切换按钮时，给body加上class="darkMode",反之去除
    页面初始化时读取状态，自动应用主题
    支持所有 iframe 页面继承暗黑主题，前提是 iframe 内容与主页面同源
    监听 iframe 的 load 事件，在其加载完成后动态同步暗黑状态

    js不能直接跨域控制子页面DOM,除非同源
    同源:网页具有三个相同的部分:协议、域名、端口号完全一样
    如：都是http或者都是https、域名都为https://example.com、端口号都为8080
*/
// 获取当前主题状态
let darkMode = localStorage.getItem('darkMode')
// 获取按钮
const themeSwitch = document.getElementById('theme-switch')
// 获取iframe
const iframe = document.querySelector("iframe")

// 启用暗黑模式
const enableDarkMode = () => {
    document.body.classList.add('darkMode')
    localStorage.setItem('darkMode','active')
    applyDarkModeToIframe()
}

// 关闭暗黑模式
const disableDarkMode = () => {
    document.body.classList.remove('darkMode')
    localStorage.setItem('darkMode',null)
    removeDarkModeFromIframe()
}

// 页面初始化时，根据 localStorage 的状态判断是否启用暗黑模式
if(darkMode === "active") enableDarkMode()

// 监听主题按钮点击事件，切换暗黑模式状态
themeSwitch.addEventListener("click",()=> {
    darkMode = localStorage.getItem('darkMode')
    if(darkMode !== "active") {
        enableDarkMode()
    } else {
        disableDarkMode()
    }
})

// 给iframe加上darkMode类
function applyDarkModeToIframe() {
    try {
        const iframeDoc = iframe.contentWindow.document
        const iframeBody = iframeDoc.body
        if (iframeBody) {
            iframeBody.classList.add("darkMode")
        }
    } catch (e) {
        console.error("应用 darkMode 到 iframe 时出错:", e)
    }
}

// 移除iframe中的darkMode类
function removeDarkModeFromIframe() {
    try {
        const iframeDoc = iframe.contentWindow.document
        const iframeBody = iframeDoc.body
        if (iframeBody) {
            iframeBody.classList.remove("darkMode")
        }
    } catch (e) {
        console.error("移除 iframe 中的 darkMode 时出错:", e)
    }
}

// 监听iframe切换页面后自动加darkMode
iframe.addEventListener("load",()=> {
    if(localStorage.getItem('darkMode') === 'active') {
        applyDarkModeToIframe()
    }
})