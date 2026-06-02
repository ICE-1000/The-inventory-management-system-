const LOGIN_API_BASE = localStorage.getItem('apiBase') || 'http://localhost:8080/api';

document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;
    const error = document.getElementById('errorMsg');
    error.textContent = '';
    try {
        const res = await fetch(`${LOGIN_API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Invalid login');
        localStorage.setItem('token', data.token);
        localStorage.setItem('role', data.role);
        if (data.departmentId) localStorage.setItem('departmentId', data.departmentId);
        window.location.href = data.role === 'ADMIN' ? 'admin/dashboard.html' : 'department/dashboard.html';
    } catch (err) {
        error.textContent = err.message || 'Unable to login';
    }
});
