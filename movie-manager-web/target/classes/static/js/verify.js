axios.get('/admin/verify',{
    headers: {
        Authorization: localStorage.getItem("Authorization")
    }
}).then(resp => {
    console.log(resp.data)
    if (resp.data.code == 401) {
        localStorage.removeItem("Authorization")
        window.parent.location.href = "/login.html";
    }
})