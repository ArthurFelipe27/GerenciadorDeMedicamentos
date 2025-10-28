document.addEventListener('DOMContentLoaded', () => {
    // --- Variáveis Globais e Configuração Inicial ---
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

    // Cache de dados
    let catalogoMedicamentos = [];
    let inventarioPessoal = [];
    let prescricoes = [];

    // --- Elementos do DOM ---
    const formCatalogo = document.getElementById('form-catalogo');
    const inventarioMedicamentoSelect = document.getElementById('inventario-medicamento-select');
    const formInventario = document.getElementById('form-inventario');
    const listaInventario = document.getElementById('lista-inventario');
    const prescricaoInventarioSelect = document.getElementById('prescricao-inventario-select');
    const formPrescricao = document.getElementById('form-prescricao');
    const listaPrescricoes = document.getElementById('lista-prescricoes');
    const formPrescricaoTitulo = document.getElementById('form-prescricao-titulo');
    const formPrescricaoDesc = document.getElementById('form-prescricao-descricao');
    const prescricaoSubmitBtn = document.getElementById('prescricao-submit-btn');
    const cancelarEdicaoBtn = document.getElementById('cancelar-edicao-btn');
    const listaRelatorio = document.getElementById('lista-relatorio');
    const estoqueAlertBox = document.getElementById('estoque-alert-box');

    // Estado
    let modoEdicao = false;
    let idPrescricaoEdicao = null;
    let monitoramentoIntervalo = null;
    let alertasMostrados = new Set();
    const INTERVALO_VERIFICACAO = 30000;

    // Função auxiliar para converter UTC (do backend) para o formato do input datetime-local
    function converterUTCParaInputLocal(isoString) {
        if (!isoString) return "";
        const date = new Date(isoString);
        const localDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000));
        return localDate.toISOString().slice(0, 16);
    }

    // --- 1. Funções de Carregamento de Dados (Carregamento Inicial) ---

    const carregarCatalogo = async () => {
        try {
            const response = await fetch('/api/medicamentos', { headers });
            if (!response.ok) throw new Error('Falha ao buscar catálogo');
            catalogoMedicamentos = await response.json();

            inventarioMedicamentoSelect.innerHTML = '';
            if (catalogoMedicamentos.length === 0) {
                inventarioMedicamentoSelect.innerHTML = '<option value="">Nenhum medicamento no catálogo. Adicione um.</option>';
                return;
            }

            const defaultOption = document.createElement('option');
            defaultOption.value = "";
            defaultOption.textContent = "Selecione um medicamento do catálogo...";
            defaultOption.selected = true;
            inventarioMedicamentoSelect.appendChild(defaultOption);

            catalogoMedicamentos.forEach(med => {
                const option = document.createElement('option');
                option.value = med.id;
                option.textContent = `${med.nome} (${med.dosagem})`;
                inventarioMedicamentoSelect.appendChild(option);
            });

        } catch (error) {
            console.error('Erro ao carregar catálogo:', error);
            inventarioMedicamentoSelect.innerHTML = '<option value="">Erro ao carregar</option>';
        }
        // SUGESTÃO: Renderiza ícones estáticos (não é estritamente necessário aqui, mas bom para garantir)
        lucide.createIcons();
    };

    const carregarInventario = async () => {
        try {
            const response = await fetch('/api/inventario', { headers });
            if (!response.ok) throw new Error('Falha ao buscar inventário');
            inventarioPessoal = await response.json();

            listaInventario.innerHTML = '';
            prescricaoInventarioSelect.innerHTML = '';

            if (inventarioPessoal.length === 0) {
                listaInventario.innerHTML = '<li>Nenhum item no seu inventário.</li>';
                prescricaoInventarioSelect.innerHTML = '<option value="">Adicione um item ao inventário primeiro.</option>';
                return;
            }

            const defaultPrescricaoOpt = document.createElement('option');
            defaultPrescricaoOpt.value = "";
            defaultPrescricaoOpt.textContent = "Selecione um item do seu inventário...";
            defaultPrescricaoOpt.selected = true;
            prescricaoInventarioSelect.appendChild(defaultPrescricaoOpt);

            let algumEstoqueBaixo = false;

            inventarioPessoal.forEach(item => {
                const li = document.createElement('li');
                const validadeFormatada = item.dataValidade
                    ? new Date(item.dataValidade).toLocaleDateString('pt-BR', { timeZone: 'UTC' })
                    : 'N/A';

                // SUGESTÃO: Adicionados ícones aos botões
                li.innerHTML = `
                    <div>
                        <strong>${item.medicamento.nome} (${item.medicamento.dosagem})</strong><br>
                        <small>Quantidade: ${item.quantidadeAtual} | Alerta em: ${item.limiteAlerta}</small>
                        <small>Validade: ${validadeFormatada}</small>
                    </div>
                    <div class="botoes-acao">
                        <button class="delete-btn delete-inventario-btn" data-id="${item.id}" title="Excluir item">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                `;

                if (item.vencido) {
                    li.classList.add('vencido');
                } else if (item.alertaEstoque) {
                    li.classList.add('alerta-estoque');
                    algumEstoqueBaixo = true;
                }
                listaInventario.appendChild(li);

                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = `${item.medicamento.nome} (Restam: ${item.quantidadeAtual})`;

                if (item.vencido) {
                    option.disabled = true;
                    option.textContent += " [VENCIDO]";
                }
                prescricaoInventarioSelect.appendChild(option);
            });

            if (algumEstoqueBaixo) {
                mostrarAlertaEstoqueGlobal("Um ou mais itens do seu inventário estão baixos.");
            } else {
                esconderAlertaEstoqueGlobal();
            }

        } catch (error) {
            console.error('Erro ao carregar inventário:', error);
            listaInventario.innerHTML = '<li>Erro ao carregar inventário.</li>';
        }
        // SUGESTÃO: Renderiza os ícones dinâmicos (trash-2)
        lucide.createIcons();
    };

    const carregarPrescricoes = async () => {
        try {
            const response = await fetch('/api/prescricoes', { headers });
            if (!response.ok) throw new Error('Falha ao buscar prescrições');
            prescricoes = await response.json();
            listaPrescricoes.innerHTML = '';

            if (prescricoes.length === 0) {
                listaPrescricoes.innerHTML = '<li>Nenhuma prescrição encontrada.</li>';
                return;
            }

            prescricoes.forEach(p => {
                const item = document.createElement('li');

                const dataInicioFormatada = converterUTCParaInputLocal(p.dataHoraInicio);
                const dataInicio = new Date(p.dataHoraInicio);
                const dataInicioExibicao = dataInicio.toLocaleString('pt-BR');
                const dataFim = new Date(dataInicio);
                dataFim.setDate(dataInicio.getDate() + p.duracaoDias);
                const dataFimExibicao = dataFim.toLocaleString('pt-BR');

                // SUGESTÃO: Adicionados ícones aos botões
                item.innerHTML = `
                    <div>
                        <strong>${p.medicamento.nome} (${p.dosagemPrescrita})</strong><br>
                        <small>Início: ${dataInicioExibicao}</small><br>
                        <small>Fim: ${dataFimExibicao}</small><br> 
                        <small>A cada ${p.intervaloHoras} horas (Tomando ${p.quantidadePorDose} unid.)</small>
                        <small>Instruções: ${p.instrucoes || 'N/A'}</small> 
                    </div>
                    <div class="botoes-acao">
                        <button class="edit-btn" 
                            title="Editar prescrição"
                            data-id="${p.id}"
                            data-item-inventario-id="${p.itemInventarioId}"
                            data-dosagem-texto="${p.dosagemPrescrita}"
                            data-dosagem-qtd="${p.quantidadePorDose}"
                            data-inicio="${dataInicioFormatada}" 
                            data-intervalo="${p.intervaloHoras}"
                            data-duracao="${p.duracaoDias}"
                            data-instrucoes="${p.instrucoes || ''}">
                            <i data-lucide="edit-3"></i>
                        </button>
                        <button class="delete-btn delete-prescricao-btn" data-id="${p.id}" title="Excluir prescrição">
                            <i data-lucide="trash-2"></i>
                        </button>
                    </div>
                `;
                listaPrescricoes.appendChild(item);
            });
        } catch (error) {
            console.error('Erro ao carregar prescrições:', error);
        }
        // SUGESTÃO: Renderiza os ícones dinâmicos (edit-3, trash-2)
        lucide.createIcons();
    };

    const carregarRelatorio = async () => {
        try {
            const response = await fetch('/api/doses', { headers });
            if (!response.ok) throw new Error('Falha ao buscar relatório');

            const doses = await response.json();
            listaRelatorio.innerHTML = '';

            if (doses.length === 0) {
                listaRelatorio.innerHTML = '<li>Nenhum registro de dose encontrado.</li>';
                return;
            }

            doses.forEach(dose => {
                const item = document.createElement('li');
                let statusClasse = (dose.status === 'TOMADA') ? 'status-tomada' : 'status-pulada';
                let statusTexto = (dose.status === 'TOMADA') ? 'Tomada' : 'Pulada';
                item.className = statusClasse;
                const dataTomadaExibicao = new Date(dose.dataHoraTomada).toLocaleString('pt-BR');

                item.innerHTML = `
                    <div>
                        <strong>${dose.nomeMedicamento} (${dose.dosagemPrescrita})</strong><br>
                        <small>Status: ${statusTexto} em: ${dataTomadaExibicao}</small>
                    </div>
                `;
                listaRelatorio.appendChild(item);
            });
        } catch (error) {
            console.error('Erro ao carregar relatório:', error);
            listaRelatorio.innerHTML = '<li>Erro ao carregar relatório.</li>';
        }
        // SUGESTÃO: Renderiza ícones (se houver algum no futuro)
        lucide.createIcons();
    };

    // --- 2. Funções de Ação (CRUD) ---

    const cadastrarMedicamento = async (event) => {
        event.preventDefault();
        const body = JSON.stringify({
            nome: document.getElementById('catalogo-nome').value,
            dosagem: document.getElementById('catalogo-dosagem').value,
            laboratorio: document.getElementById('catalogo-lab').value,
            viaAdministracao: document.getElementById('catalogo-via').value
        });

        try {
            const response = await fetch('/api/medicamentos', { method: 'POST', headers, body });
            if (!response.ok) throw new Error('Falha ao cadastrar medicamento');

            formCatalogo.reset();
            carregarCatalogo();
        } catch (error) {
            console.error('Erro ao cadastrar medicamento:', error);
        }
    };

    const adicionarInventario = async (event) => {
        event.preventDefault();

        const medicamentoId = parseInt(inventarioMedicamentoSelect.value, 10);
        if (isNaN(medicamentoId)) {
            console.error('ID do medicamento inválido');
            return;
        }
        const dataValidade = document.getElementById('inventario-validade').value;

        const body = JSON.stringify({
            medicamentoId: medicamentoId,
            quantidadeAtual: parseInt(document.getElementById('inventario-qtd').value, 10),
            limiteAlerta: parseInt(document.getElementById('inventario-limite').value, 10),
            dataValidade: dataValidade ? dataValidade : null
        });

        try {
            const response = await fetch('/api/inventario', { method: 'POST', headers, body });
            if (!response.ok) throw new Error('Falha ao adicionar ao inventário');

            formInventario.reset();
            carregarInventario();

        } catch (error) {
            console.error('Erro ao adicionar inventário:', error);
        }
    };

    const handleDeletarInventario = async (event) => {
        // SUGESTÃO: Melhoria para pegar o botão mesmo se clicar no ícone
        const btn = event.target.closest('.delete-inventario-btn');
        if (!btn) return;

        const id = btn.dataset.id;

        try {
            const response = await fetch(`/api/inventario/${id}`, { method: 'DELETE', headers });
            if (!response.ok) throw new Error('Falha ao deletar item do inventário');

            carregarInventario();
            carregarPrescricoes();
            iniciarMonitoramentoRobusto();

        } catch (error) {
            console.error('Erro ao deletar inventário:', error);
        }
    };

    const resetarFormularioPrescricao = () => {
        formPrescricao.reset();
        modoEdicao = false;
        idPrescricaoEdicao = null;
        formPrescricaoTitulo.textContent = '3. Minhas Prescrições';
        formPrescricaoDesc.textContent = 'Agende lembretes usando itens do *Seu Inventário*.';

        // SUGESTÃO: Atualiza o botão para ter o ícone correto
        prescricaoSubmitBtn.innerHTML = '<i data-lucide="save"></i> Adicionar Prescrição';

        cancelarEdicaoBtn.style.display = 'none';
        prescricaoInventarioSelect.value = "";

        // Renderiza os ícones (do H2 e do botão)
        lucide.createIcons();
    };

    const handleEditarPrescricao = (event) => {
        // SUGESTÃO: Melhoria para pegar o botão mesmo se clicar no ícone
        const btn = event.target.closest('.edit-btn');
        if (!btn) return;

        modoEdicao = true;
        idPrescricaoEdicao = btn.dataset.id;

        prescricaoInventarioSelect.value = btn.dataset.itemInventarioId;
        document.getElementById('prescricao-dosagem-texto').value = btn.dataset.dosagemTexto;
        document.getElementById('prescricao-dosagem-qtd').value = btn.dataset.dosagemQtd;
        document.getElementById('data-inicio').value = btn.dataset.inicio;
        document.getElementById('intervalo').value = btn.dataset.intervalo;
        document.getElementById('duracao').value = btn.dataset.duracao;
        document.getElementById('instrucoes').value = btn.dataset.instrucoes;

        formPrescricaoTitulo.textContent = 'Editar Prescrição';
        formPrescricaoDesc.textContent = 'Modifique os dados da sua prescrição.';

        // SUGESTÃO: Atualiza o botão para ter o ícone correto
        prescricaoSubmitBtn.innerHTML = '<i data-lucide="check-circle"></i> Salvar Alterações';

        cancelarEdicaoBtn.style.display = 'block';

        // Renderiza os ícones (do H2 e do botão)
        lucide.createIcons();
        formPrescricao.scrollIntoView({ behavior: 'smooth', block: 'start' });
    };

    const handleDeletarPrescricao = async (event) => {
        // SUGESTÃO: Melhoria para pegar o botão mesmo se clicar no ícone
        const btn = event.target.closest('.delete-prescricao-btn');
        if (!btn) return;

        const id = btn.dataset.id;

        try {
            const response = await fetch(`/api/prescricoes/${id}`, { method: 'DELETE', headers });
            if (!response.ok) throw new Error('Falha ao deletar');

            carregarPrescricoes();
            iniciarMonitoramentoRobusto();

            if (modoEdicao && idPrescricaoEdicao == id) {
                resetarFormularioPrescricao();
            }
        } catch (error) {
            console.error('Erro ao deletar:', error);
        }
    };

    const handlePrescricaoSubmit = async (event) => {
        event.preventDefault();

        const itemInventarioId = parseInt(prescricaoInventarioSelect.value, 10);
        if (isNaN(itemInventarioId)) {
            console.error('Item do inventário inválido');
            return;
        }

        const dataHoraInicioLocal = document.getElementById('data-inicio').value;
        const dataHoraInicioUTC = new Date(dataHoraInicioLocal).toISOString();

        const body = JSON.stringify({
            itemInventarioId: itemInventarioId,
            quantidadePorDose: parseInt(document.getElementById('prescricao-dosagem-qtd').value, 10),
            dosagemPrescrita: document.getElementById('prescricao-dosagem-texto').value,
            dataHoraInicio: dataHoraInicioUTC,
            intervaloHoras: parseInt(document.getElementById('intervalo').value, 10),
            duracaoDias: parseInt(document.getElementById('duracao').value, 10),
            instrucoes: document.getElementById('instrucoes').value
        });

        const url = modoEdicao ? `/api/prescricoes/${idPrescricaoEdicao}` : '/api/prescricoes';
        const method = modoEdicao ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, { method, headers, body });
            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody || (modoEdicao ? 'Falha ao atualizar prescrição' : 'Falha ao adicionar prescrição'));
            }

            resetarFormularioPrescricao();
            carregarPrescricoes();
            iniciarMonitoramentoRobusto();
        } catch (error) {
            console.error('Erro ao salvar prescrição:', error.message);
        }
    };

    // --- 3. Lógica de Alerta e Estoque ---

    const registrarDose = async (prescricao, status) => {
        const body = JSON.stringify({
            prescricaoId: prescricao.id,
            dataHoraTomada: new Date().toISOString(),
            status: status
        });

        try {
            const response = await fetch('/api/doses', { method: 'POST', headers, body });

            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(errorBody || 'Falha ao registrar dose');
            }

            console.log(`Dose registrada [${status}] para prescrição ${prescricao.id}`);
            carregarRelatorio();

            if (status === 'TOMADA' && response.status === 201) {
                try {
                    const itemInventarioAtualizado = await response.json();
                    carregarInventario();

                    if (itemInventarioAtualizado.alertaEstoque) {
                        mostrarAlertaEstoqueGlobal(`Estoque de ${itemInventarioAtualizado.medicamento.nome} está baixo (${itemInventarioAtualizado.quantidadeAtual} restantes).`);
                    }
                } catch (e) {
                    console.log("Dose pulada, sem atualização de estoque.");
                }
            }
        } catch (error) {
            console.error('Erro ao registrar dose:', error.message);
        }
    };

    const mostrarAlerta = (prescricao, dataDose) => {
        const modal = document.getElementById('alert-modal');
        const span = document.getElementsByClassName('modal-close')[0];
        const btnConfirm = document.getElementById('modal-confirm-dose');
        const btnSkip = document.getElementById('modal-skip-dose');

        const mensagem = `Hora de tomar: ${prescricao.medicamento.nome} (${prescricao.dosagemPrescrita}). Instruções: ${prescricao.instrucoes || 'N/A'}`;
        document.getElementById('alert-message').textContent = mensagem;

        modal.style.display = 'flex';

        // SUGESTÃO: Renderiza ícones no modal
        lucide.createIcons();

        btnConfirm.onclick = null;
        btnSkip.onclick = null;
        span.onclick = null;

        btnConfirm.onclick = () => {
            registrarDose(prescricao, "TOMADA");
            modal.style.display = 'none';
        };
        btnSkip.onclick = () => {
            registrarDose(prescricao, "PULADA");
            modal.style.display = 'none';
        };
        span.onclick = () => {
            modal.style.display = 'none';
        };
        window.onclick = (event) => {
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        };
    };

    const mostrarAlertaEstoqueGlobal = (mensagem) => {
        console.warn("ALERTA DE ESTOQUE:", mensagem);
        // SUGESTÃO: Adiciona ícone ao alerta de estoque
        estoqueAlertBox.innerHTML = `<i data-lucide="alert-triangle"></i> <strong>Alerta:</strong> ${mensagem}`;
        estoqueAlertBox.style.display = 'flex';
        // Renderiza o ícone
        lucide.createIcons();
    };

    const esconderAlertaEstoqueGlobal = () => {
        estoqueAlertBox.style.display = 'none';
    };

    const calcularProximaDose = (prescricao, agora) => {
        const inicio = new Date(prescricao.dataHoraInicio);
        const fim = new Date(inicio);
        fim.setDate(fim.getDate() + prescricao.duracaoDias);

        if (agora > fim) return null;

        let proximaDose = new Date(inicio);

        if (proximaDose > agora) {
            return proximaDose;
        }

        while (proximaDose < agora) {
            let doseSeguinte = new Date(proximaDose);
            doseSeguinte.setHours(doseSeguinte.getHours() + prescricao.intervaloHoras);

            if (doseSeguinte > agora) {
                break;
            }

            proximaDose.setHours(proximaDose.getHours() + prescricao.intervaloHoras);
        }

        if (proximaDose > fim) return null;

        return proximaDose;
    };

    const verificarDoses = async () => {
        console.log("Verificando doses...", new Date().toLocaleTimeString());
        let prescricoesAtivas = [];
        try {
            const response = await fetch('/api/prescricoes/ativas', { headers });
            if (!response.ok) throw new Error('Falha ao buscar prescrições ativas');
            prescricoesAtivas = await response.json();
        } catch (error) {
            console.error('Erro no monitoramento:', error);
            return;
        }

        const agora = new Date();

        prescricoesAtivas.forEach(p => {
            const proximaDose = calcularProximaDose(p, agora);
            if (!proximaDose) return;

            const alertaId = `${p.id}-${proximaDose.getTime()}`;

            const diffMs = proximaDose.getTime() - agora.getTime();
            const diffMin = Math.round(diffMs / 60000);

            if (diffMin <= 1 && !alertasMostrados.has(alertaId)) {
                console.log(`ALERTA: Hora de tomar ${p.medicamento.nome}`);
                mostrarAlerta(p, proximaDose);
                alertasMostrados.add(alertaId);
            }
        });
    };

    const iniciarMonitoramentoRobusto = () => {
        if (monitoramentoIntervalo) {
            clearInterval(monitoramentoIntervalo);
        }
        console.log(`Iniciando monitoramento (verificação a cada ${INTERVALO_VERIFICACAO / 1000}s)`);

        alertasMostrados.clear();
        verificarDoses();
        monitoramentoIntervalo = setInterval(verificarDoses, INTERVALO_VERIFICACAO);
    };

    // --- 4. Inicialização e Event Listeners ---

    document.getElementById('logout-button').addEventListener('click', () => {
        if (monitoramentoIntervalo) {
            clearInterval(monitoramentoIntervalo);
        }
        localStorage.removeItem('token');
        window.location.href = 'login.html';
    });

    formCatalogo.addEventListener('submit', cadastrarMedicamento);
    formInventario.addEventListener('submit', adicionarInventario);
    formPrescricao.addEventListener('submit', handlePrescricaoSubmit);
    cancelarEdicaoBtn.addEventListener('click', resetarFormularioPrescricao);

    listaPrescricoes.addEventListener('click', (event) => {
        if (event.target.closest('.edit-btn')) {
            handleEditarPrescricao(event);
        } else if (event.target.closest('.delete-prescricao-btn')) {
            handleDeletarPrescricao(event);
        }
    });

    listaInventario.addEventListener('click', (event) => {
        if (event.target.closest('.delete-inventario-btn')) {
            handleDeletarInventario(event);
        }
    });

    const carregarTudo = async () => {
        await carregarCatalogo();
        await carregarInventario();
        await carregarPrescricoes();
        await carregarRelatorio();
        iniciarMonitoramentoRobusto();
    };

    carregarTudo();
});

// Função auxiliar para decodificar o Token JWT (Inalterada)
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

