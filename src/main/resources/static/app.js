document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    if (!token) {
        window.location.href = 'login.html';
        return;
    }

    const tokenPayload = parseJwt(token);
    const username = tokenPayload.sub;
    document.getElementById('username-display').textContent = username;

    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };

    const formPrescricao = document.getElementById('form-prescricao');
    const listaPrescricoes = document.getElementById('lista-prescricoes');

    // NOVO: Formulário de Catálogo
    const formCatalogo = document.getElementById('form-catalogo');

    // NOVO: Select de Medicamentos
    const medicamentoSelect = document.getElementById('medicamento-select');

    // NOVO: Função para carregar medicamentos no <select>
    const carregarMedicamentos = async () => {
        try {
            const response = await fetch('/api/medicamentos', { headers });
            if (!response.ok) {
                throw new Error('Falha ao buscar medicamentos');
            }
            const medicamentos = await response.json();

            medicamentoSelect.innerHTML = ''; // Limpa o "Carregando..."

            if (medicamentos.length === 0) {
                medicamentoSelect.innerHTML = '<option value="">Nenhum medicamento no catálogo. Adicione um acima.</option>';
                return;
            }

            // Adiciona uma opção padrão "Selecione"
            const defaultOption = document.createElement('option');
            defaultOption.value = "";
            defaultOption.textContent = "Selecione um medicamento...";
            defaultOption.disabled = true;
            defaultOption.selected = true;
            medicamentoSelect.appendChild(defaultOption);

            medicamentos.forEach(med => {
                const option = document.createElement('option');
                option.value = med.id;
                // Usando os getters que definimos manualmente (sem Lombok)
                option.textContent = `${med.nome} (${med.dosagem})`;
                medicamentoSelect.appendChild(option);
            });

        } catch (error) {
            console.error('Erro ao carregar medicamentos:', error);
            medicamentoSelect.innerHTML = '<option value="">Erro ao carregar</option>';
        }
    };

    // Função para carregar as prescrições do usuário
    const carregarPrescricoes = async () => {
        try {
            const response = await fetch('/api/prescricoes', { headers });
            if (!response.ok) {
                throw new Error('Falha ao buscar prescrições');
            }
            const prescricoes = await response.json();
            listaPrescricoes.innerHTML = ''; // Limpa a lista antiga

            if (prescricoes.length === 0) {
                listaPrescricoes.innerHTML = '<li>Nenhuma prescrição encontrada.</li>';
            }

            prescricoes.forEach(p => {
                const item = document.createElement('li');
                item.innerHTML = `
                    <div>
                        <strong>${p.medicamento.nome} (${p.dosagemPrescrita})</strong><br>
                        <small>Início: ${new Date(p.dataHoraInicio).toLocaleString('pt-BR')}</small><br>
                        <small>A cada ${p.intervaloHoras} horas por ${p.duracaoDias} dias.</small>
                    </div>
                    <button class="delete-btn" data-id="${p.id}">Excluir</button>
                `;
                listaPrescricoes.appendChild(item);
            });
        } catch (error) {
            console.error('Erro ao carregar prescrições:', error);
        }
    };

    // NOVO: Função para cadastrar novo medicamento no catálogo
    const cadastrarMedicamento = async (event) => {
        event.preventDefault();

        const body = JSON.stringify({
            nome: document.getElementById('catalogo-nome').value,
            dosagem: document.getElementById('catalogo-dosagem').value,
            laboratorio: document.getElementById('catalogo-lab').value,
            quantidadeEstoque: parseInt(document.getElementById('catalogo-estoque').value, 10),
            viaAdministracao: document.getElementById('catalogo-via').value
        });

        try {
            const response = await fetch('/api/medicamentos', {
                method: 'POST',
                headers,
                body
            });

            if (!response.ok) {
                throw new Error('Falha ao cadastrar medicamento');
            }

            alert('Medicamento adicionado ao catálogo!');
            formCatalogo.reset();
            carregarMedicamentos(); // Recarrega o <select>

        } catch (error) {
            console.error('Erro ao cadastrar medicamento:', error);
            alert('Erro ao cadastrar medicamento.');
        }
    };

    // Função para adicionar uma nova prescrição
    const adicionarPrescricao = async (event) => {
        event.preventDefault();

        const medicamentoId = parseInt(medicamentoSelect.value, 10);
        if (isNaN(medicamentoId)) {
            alert('Por favor, selecione um medicamento.');
            return;
        }

        const body = JSON.stringify({
            // ATUALIZADO: Pega o ID do <select>
            medicamentoId: medicamentoId,
            dosagemPrescrita: document.getElementById('prescricao-dosagem').value,
            dataHoraInicio: document.getElementById('data-inicio').value,
            intervaloHoras: parseInt(document.getElementById('intervalo').value, 10),
            duracaoDias: parseInt(document.getElementById('duracao').value, 10),
            instrucoes: document.getElementById('instrucoes').value
        });

        try {
            const response = await fetch('/api/prescricoes', {
                method: 'POST',
                headers,
                body
            });

            if (!response.ok) {
                throw new Error('Falha ao adicionar prescrição');
            }

            formPrescricao.reset();
            carregarMedicamentos(); // Reseta o select
            carregarPrescricoes(); // Atualiza a lista
            iniciarMonitoramento(); // Reinicia os timers

        } catch (error) {
            console.error('Erro ao adicionar prescrição:', error);
            alert('Erro ao adicionar prescrição.');
        }
    };

    // Função para deletar prescrição
    const deletarPrescricao = async (event) => {
        if (event.target.classList.contains('delete-btn')) {
            const id = event.target.dataset.id;
            if (confirm('Tem certeza que deseja excluir esta prescrição?')) {
                try {
                    const response = await fetch(`/api/prescricoes/${id}`, {
                        method: 'DELETE',
                        headers
                    });

                    if (!response.ok) {
                        throw new Error('Falha ao deletar');
                    }

                    carregarPrescricoes(); // Atualiza a lista
                    iniciarMonitoramento(); // Reinicia os timers

                } catch (error) {
                    console.error('Erro ao deletar:', error);
                    alert('Erro ao deletar prescrição.');
                }
            }
        }
    };

    // --- Lógica de Alerta/Notificação ---

    let timers = []; // Array para guardar os timers (setTimeout)

    const mostrarAlerta = (mensagem) => {
        const modal = document.getElementById('alert-modal');
        const span = document.getElementsByClassName('modal-close')[0];
        document.getElementById('alert-message').textContent = mensagem;
        modal.style.display = 'block';

        span.onclick = () => {
            modal.style.display = 'none';
        }
        window.onclick = (event) => {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
    };

    const iniciarMonitoramento = async () => {
        // 1. Limpa timers antigos
        timers.forEach(timerId => clearTimeout(timerId));
        timers = [];
        console.log('Timers antigos limpos.');

        // 2. Busca prescrições ATIVAS
        let prescricoesAtivas = [];
        try {
            const response = await fetch('/api/prescricoes/ativas', { headers });
            if (!response.ok) throw new Error('Falha ao buscar prescrições ativas');
            prescricoesAtivas = await response.json();
            console.log(`Encontradas ${prescricoesAtivas.length} prescrições ativas.`);
        } catch (error) {
            console.error('Erro no monitoramento:', error);
            return;
        }

        const agora = new Date();

        // 3. Agenda os próximos alertas
        prescricoesAtivas.forEach(p => {
            const inicio = new Date(p.dataHoraInicio);
            const fim = new Date(inicio);
            fim.setDate(fim.getDate() + p.duracaoDias);

            // Se a prescrição já acabou, ignora
            if (agora > fim) {
                // (Opcional: Fazer uma chamada API para desativar p.ativa = false)
                return;
            }

            let proximaDose = new Date(inicio);

            // Calcula a próxima dose (encontra a primeira dose futura)
            while (proximaDose < agora) {
                proximaDose.setHours(proximaDose.getHours() + p.intervaloHoras);
            }

            // Se a próxima dose calculada já passou do fim, ignora
            if (proximaDose > fim) {
                return;
            }

            const diffMs = proximaDose.getTime() - agora.getTime();
            // p.medicamento.nome / p.dosagemPrescrita / p.instrucoes
            const mensagem = `Hora de tomar: ${p.medicamento.nome} (${p.dosagemPrescrita}). Instruções: ${p.instrucoes || 'N/A'}`;

            console.log(`Agendando "${p.medicamento.nome}" para ${proximaDose.toLocaleString('pt-BR')} (em ${Math.round(diffMs / 1000 / 60)} min)`);

            // Agenda o alerta
            const timerId = setTimeout(() => {
                mostrarAlerta(mensagem);
                // Agenda a *próxima* dose após esta
                iniciarMonitoramento();
            }, diffMs);

            timers.push(timerId);
        });
    };


    // --- Inicialização ---
    document.getElementById('logout-button').addEventListener('click', () => {
        timers.forEach(timerId => clearTimeout(timerId)); // Limpa os timers ao sair
        localStorage.removeItem('token');
        window.location.href = 'login.html';
    });

    formPrescricao.addEventListener('submit', adicionarPrescricao);
    listaPrescricoes.addEventListener('click', deletarPrescricao);
    formCatalogo.addEventListener('submit', cadastrarMedicamento); // NOVO

    // Carrega tudo ao iniciar a página
    carregarMedicamentos();
    carregarPrescricoes();
    iniciarMonitoramento();
});

// Função auxiliar para decodificar o Token JWT
function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Token inválido", e);
        localStorage.removeItem('token');
        window.location.href = 'login.html';
        return null;
    }
}

