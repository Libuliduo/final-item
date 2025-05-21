// 获取当前主题状态
let darkMode = localStorage.getItem('darkMode');

// 启用暗黑模式
const enableDarkMode = () => {
    document.body.classList.add('darkMode');
    localStorage.setItem('darkMode', 'active');
};

// 关闭暗黑模式
const disableDarkMode = () => {
    document.body.classList.remove('darkMode');
    localStorage.setItem('darkMode', null);
};

// 页面初始化时，根据 localStorage 的状态判断是否启用暗黑模式
if (darkMode === 'active') {
    enableDarkMode();
} else {
    disableDarkMode();
}

// 监听主页面的消息，同步暗黑状态
window.addEventListener('message', (event) => {
    if (event.data === 'dark-mode') {
        enableDarkMode();
    } else if (event.data === 'light-mode') {
        disableDarkMode();
    }
});