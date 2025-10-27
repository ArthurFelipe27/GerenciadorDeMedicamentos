document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    const loginError = document.getElementById('login-error');
    const registerError = document.getElementById('register-error');
    const registerSuccess = document.getElementById('register-success');

    // --- Processo de Login ---
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        loginError.style.display = 'none';

        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('token', data.token); // Salva o token
                window.location.href = 'index.html'; // Redireciona
            } else {
                loginError.textContent = 'Usuário ou senha inválidos.';
                loginError.style.display = 'block';
            }
        } catch (error) {
            console.error('Erro no login:', error);
            loginError.textContent = 'Erro ao tentar conectar. Tente novamente.';
            loginError.style.display = 'block';
        }
    });

    // --- Processo de Registro ---
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        registerError.style.display = 'none';
        registerSuccess.style.display = 'none';

        const username = document.getElementById('reg-username').value;
        const password = document.getElementById('reg-password').value;

        try {
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });

            if (response.ok) {
                registerSuccess.textContent = 'Usuário registrado com sucesso! Você já pode fazer o login acima.';
                registerSuccess.style.display = 'block';
                registerForm.reset();
            } else {
                const errorMsg = await response.text();
                registerError.textContent = `Erro: ${errorMsg}`;
                registerError.style.display = 'block';
            }
        } catch (error) {
            console.error('Erro no registro:', error);
            registerError.textContent = 'Erro ao tentar registrar. Tente novamente.';
            registerError.style.display = 'block';
        }
    });
});

