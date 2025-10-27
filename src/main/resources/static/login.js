document.addEventListener('DOMContentLoaded', () => {
    // --- Seleção de Formulários ---
    const loginForm = document.getElementById('login-form');
    const registerForm = document.getElementById('register-form');

    // --- Mensagens de Feedback (Login) ---
    const loginError = document.getElementById('login-error');

    // --- Mensagens de Feedback (Registro) ---
    const registerError = document.getElementById('register-error');
    const registerSuccess = document.getElementById('register-success');

    // --- Campos de Registro (Para Validação) ---
    const regUsername = document.getElementById('reg-username');
    const regPassword = document.getElementById('reg-password');
    const regConfirmPassword = document.getElementById('reg-confirm-password');

    // --- Mensagens de Validação (Registro) ---
    const regUsernameError = document.getElementById('reg-username-error');
    const regPasswordError = document.getElementById('reg-password-error');
    const regConfirmPasswordError = document.getElementById('reg-confirm-password-error');

    // --- Funções Auxiliares de Validação ---
    const showError = (input, messageElement, message) => {
        messageElement.textContent = message;
        messageElement.style.display = 'block';
        input.classList.add('input-error'); // Adiciona borda vermelha
    };

    const hideError = (input, messageElement) => {
        messageElement.style.display = 'none';
        input.classList.remove('input-error'); // Remove borda vermelha
    };

    // --- Funções de Validação Específicas ---
    const validateUsername = () => {
        if (regUsername.value.trim().length < 3) {
            showError(regUsername, regUsernameError, 'Usuário deve ter pelo menos 3 caracteres.');
            return false;
        } else {
            hideError(regUsername, regUsernameError);
            return true;
        }
    };

    const validatePassword = () => {
        if (regPassword.value.length < 6) {
            showError(regPassword, regPasswordError, 'Senha deve ter pelo menos 6 caracteres.');
            return false;
        } else {
            hideError(regPassword, regPasswordError);
            return true;
        }
    };

    const validateConfirmPassword = () => {
        // Valida o comprimento primeiro
        if (regConfirmPassword.value.length < 6) {
            showError(regConfirmPassword, regConfirmPasswordError, 'Confirmação deve ter pelo menos 6 caracteres.');
            return false;
        }
        // Valida se são iguais
        if (regPassword.value !== regConfirmPassword.value) {
            showError(regConfirmPassword, regConfirmPasswordError, 'As senhas não coincidem.');
            return false;
        } else {
            hideError(regConfirmPassword, regConfirmPasswordError);
            return true;
        }
    };

    // --- Adiciona Event Listeners para Validação em Tempo Real ---
    if (regUsername) regUsername.addEventListener('input', validateUsername);
    if (regPassword) regPassword.addEventListener('input', validatePassword);

    // Valida a confirmação E a senha original (se a senha original mudar)
    if (regPassword) regPassword.addEventListener('input', validateConfirmPassword);
    if (regConfirmPassword) regConfirmPassword.addEventListener('input', validateConfirmPassword);


    // --- Processo de Login (Sem alterações) ---
    if (loginForm) {
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
    }

    // --- Processo de Registro (Atualizado com Validação) ---
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            registerError.style.display = 'none';
            registerSuccess.style.display = 'none';

            // *** Roda as validações antes de enviar ***
            const isUsernameValid = validateUsername();
            const isPasswordValid = validatePassword();
            const isConfirmPasswordValid = validateConfirmPassword();

            if (!isUsernameValid || !isPasswordValid || !isConfirmPasswordValid) {
                registerError.textContent = 'Por favor, corrija os erros no formulário.';
                registerError.style.display = 'block';
                return; // Impede o envio
            }

            const username = regUsername.value;
            const password = regPassword.value;

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
                    // Limpa os indicadores de erro
                    hideError(regUsername, regUsernameError);
                    hideError(regPassword, regPasswordError);
                    hideError(regConfirmPassword, regConfirmPasswordError);
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
    }
});
