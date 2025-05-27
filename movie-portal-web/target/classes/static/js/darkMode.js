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
// 启用暗黑模式
const enableDarkMode = () => {
    document.body.classList.add('darkMode');
    localStorage.setItem('darkMode', 'active');
    // 同步到所有 iframe
    const iframes = document.querySelectorAll('iframe');
    iframes.forEach((iframe) => {
        if (iframe.contentWindow) {
            iframe.contentWindow.postMessage('dark-mode', '*');
        }
    });
};

// 关闭暗黑模式
const disableDarkMode = () => {
    document.body.classList.remove('darkMode');
    localStorage.setItem('darkMode', null);
    // 同步到所有 iframe
    const iframes = document.querySelectorAll('iframe');
    iframes.forEach((iframe) => {
        if (iframe.contentWindow) {
            iframe.contentWindow.postMessage('light-mode', '*');
        }
    });
};

// 获取当前主题状态
let darkMode = localStorage.getItem('darkMode');

// 页面初始化时，根据 localStorage 的状态判断是否启用暗黑模式
if (darkMode === 'active') {
    enableDarkMode();
}

// 获取按钮
const themeSwitch = document.getElementById('theme-switch');

// 监听主题按钮点击事件，切换暗黑模式状态
if (themeSwitch) {
    themeSwitch.addEventListener('click', () => {
        darkMode = localStorage.getItem('darkMode');
        if (darkMode !== 'active') {
            enableDarkMode();
        } else {
            disableDarkMode();
        }
    });
}

// 监听 iframe 的 load 事件，同步暗黑状态
const iframes = document.querySelectorAll('iframe');
iframes.forEach((iframe) => {
    iframe.addEventListener('load', () => {
        if (darkMode === 'active') {
            iframe.contentWindow.postMessage('dark-mode', '*');
        } else {
            iframe.contentWindow.postMessage('light-mode', '*');
        }
    });
});