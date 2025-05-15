const TOKEN_KEY = "Authorization";
const token = sessionStorage.getItem(TOKEN_KEY);  // 或用 localStorage.getItem(TOKEN_KEY)

if (!token) {
    // 没有 token，直接跳转登录页
    window.location.href = "/login.html";
}

// === 配置全局响应拦截器，统一处理 401 ===
axios.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.data.code === 401) {
            console.warn("认证失败，跳转登录页");
            window.location.href = "/login.html";
        } else {
            console.error("请求出错:", error);
        }
        return Promise.reject(error);
    }
);

// === 验证 token 是否有效 ===
axios.get('/token/verify', {
    headers: {
        Authorization: token
    }
}).then(resp => {
    console.log("验证成功:", resp.data);
});
