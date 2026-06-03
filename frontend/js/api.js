const API_BASE = localStorage.getItem('apiBase') || 'https://the-inventory-management-system-ni8e.onrender.com/api';

function token() {
    return localStorage.getItem('token');
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('departmentId');
    window.location.href = '../index.html';
}

async function api(path, options = {}) {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    if (token()) headers.Authorization = `Bearer ${token()}`;
    const res = await fetch(`${API_BASE}${path}`, { ...options, headers });
    if (!res.ok) {
        let message = `HTTP ${res.status}`;
        try {
            const body = await res.json();
            message = body.message || message;
        } catch (err) {
            if (res.statusText) message = `${message} ${res.statusText}`;
        }
        throw new Error(message);
    }
    if (res.status === 204) return null;
    return res.json();
}

function requireAuth(role) {
    if (!token()) window.location.href = '../index.html';
    const currentRole = localStorage.getItem('role');
    if (role && currentRole !== role) window.location.href = '../index.html';
    const btn = document.getElementById('logoutBtn');
    if (btn) btn.addEventListener('click', logout);
}

function renderRows(tbody, rows, renderer) {
    tbody.innerHTML = rows.length ? rows.map(renderer).join('') : '<tr><td colspan="8">No records found.</td></tr>';
}
