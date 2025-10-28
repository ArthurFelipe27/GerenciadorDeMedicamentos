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
    // Catálogo
    const formCatalogo = document.getElementById('form-catalogo');
    const inventarioMedicamentoSelect = document.getElementById('inventario-medicamento-select');

    // Inventário
    const formInventario = document.getElementById('form-inventario');
    const listaInventario = document.getElementById('lista-inventario');
    const prescricaoInventarioSelect = document.getElementById('prescricao-inventario-select');

    // Prescrição
    const formPrescricao = document.getElementById('form-prescricao');
    const listaPrescricoes = document.getElementById('lista-prescricoes');
    const formPrescricaoTitulo = document.getElementById('form-prescricao-titulo');
    const formPrescricaoDesc = document.getElementById('form-prescricao-descricao');
    const prescricaoSubmitBtn = document.getElementById('prescricao-submit-btn');
    const cancelarEdicaoBtn = document.getElementById('cancelar-edicao-btn');

    // Relatório
    const listaRelatorio = document.getElementById('lista-relatorio');

    // Alerta de Estoque
    const estoqueAlertBox = document.getElementById('estoque-alert-box');

    // Estado
    let modoEdicao = false;
    let idPrescricaoEdicao = null;

    // *** SUGESTÃO 3: Variáveis de Monitoramento (Modificado) ***
    let monitoramentoIntervalo = null;
    let alertasMostrados = new Set(); // Rastreia alertas já exibidos [prescricaoId-timestamp]
    const INTERVALO_VERIFICACAO = 30000; // Verifica a cada 30 segundos


    // --- 1. Funções de Carregamento de Dados (Carregamento Inicial) ---

    // Carrega o CATÁLOGO GLOBAL (para o formulário de inventário)
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
    };

    // Carrega o INVENTÁRIO PESSOAL (para a lista de inventário E o form de prescrição)
    const carregarInventario = async () => {
        try {
            const response = await fetch('/api/inventario', { headers });
            if (!response.ok) throw new Error('Falha ao buscar inventário');
            inventarioPessoal = await response.json();

            listaInventario.innerHTML = '';
            prescricaoInventarioSelect.innerHTML = ''; // Limpa os dois

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
                // Popula a Lista do Inventário
                const li = document.createElement('li');
                li.innerHTML = `
                    <div>
                        <strong>${item.medicamento.nome} (${item.medicamento.dosagem})</strong><br>
                        <small>Quantidade: ${item.quantidadeAtual} | Alerta em: ${item.limiteAlerta}</small>
                        <small>Validade: ${item.dataValidade ? new Date(item.dataValidade).toLocaleDateString('pt-BR', { timeZone: 'UTC' }) : 'N/A'}</small>
                    </div>
                    <div class="botoes-acao">
                        <!-- TODO: Botão de Editar Inventário -->
                        <button class="delete-btn delete-inventario-btn" data-id="${item.id}">Excluir</button>
                    </div>
                `;

                // *** SUGESTÃO 2: Adiciona classes de alerta/vencido ***
                if (item.vencido) {
                    li.classList.add('vencido');
                } else if (item.alertaEstoque) {
                    li.classList.add('alerta-estoque');
                    algumEstoqueBaixo = true;
                }
                // *** FIM DA SUGESTÃO 2 ***

                listaInventario.appendChild(li);

                // Popula o <select> do Formulário de Prescrição
                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = `${item.medicamento.nome} (Restam: ${item.quantidadeAtual})`;

                // *** SUGESTÃO 2: Desabilita itens vencidos no select ***
                if (item.vencido) {
                    option.disabled = true;
                    option.textContent += " [VENCIDO]";
                }

                prescricaoInventarioSelect.appendChild(option);
            });

            // Mostra o alerta de estoque baixo (se houver)
            if (algumEstoqueBaixo) {
                mostrarAlertaEstoqueGlobal("Um ou mais itens do seu inventário estão baixos.");
            } else {
                esconderAlertaEstoqueGlobal();
            }

        } catch (error) {
            console.error('Erro ao carregar inventário:', error);
            listaInventario.innerHTML = '<li>Erro ao carregar inventário.</li>';
        }
    };

    // Carrega as PRESCRIÇÕES
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
                const dataInicioFormatada = p.dataHoraInicio.slice(0, 16);

                item.innerHTML = `
                    <div>
                        <strong>${p.medicamento.nome} (${p.dosagemPrescrita})</strong><br>
                        <small>Início: ${new Date(p.dataHoraInicio).toLocaleString('pt-BR')}</small><br>
                        <small>A cada ${p.intervaloHoras} horas (Tomando ${p.quantidadePorDose} unid.)</small>
                    </div>
                    <div class="botoes-acao">
                        <button class="edit-btn" 
                            data-id="${p.id}"
                            data-item-inventario-id="${p.itemInventarioId}"
                            data-dosagem-texto="${p.dosagemPrescrita}"
                            data-dosagem-qtd="${p.quantidadePorDose}"
                            data-inicio="${dataInicioFormatada}"
                            data-intervalo="${p.intervaloHoras}"
                            data-duracao="${p.duracaoDias}"
                            data-instrucoes="${p.instrucoes || ''}">
                            Editar
                        </button>
                        <button class="delete-btn delete-prescricao-btn" data-id="${p.id}">Excluir</button>
                    </div>
                `;
                listaPrescricoes.appendChild(item);
            });
        } catch (error) {
            console.error('Erro ao carregar prescrições:', error);
        }
    };

    // Carrega o RELATÓRIO
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

                item.innerHTML = `
                    <div>
                        <strong>${dose.nomeMedicamento} (${dose.dosagemPrescrita})</strong><br>
                        <small>Status: ${statusTexto} em: ${new Date(dose.dataHoraTomada).toLocaleString('pt-BR')}</small>
                    </div>
                `;
                listaRelatorio.appendChild(item);
            });
        } catch (error) {
            console.error('Erro ao carregar relatório:', error);
            listaRelatorio.innerHTML = '<li>Erro ao carregar relatório.</li>';
        }
    };

    // --- 2. Funções de Ação (CRUD) ---

    // (Catálogo) Adicionar definição de medicamento
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

            alert('Definição de medicamento adicionada ao catálogo!');
            formCatalogo.reset();
            carregarCatalogo(); // Recarrega o select do inventário
        } catch (error) {
            console.error('Erro ao cadastrar medicamento:', error);
            alert('Erro ao cadastrar medicamento.');
        }
    };

    // (Inventário) Adicionar item ao inventário pessoal
    const adicionarInventario = async (event) => {
        event.preventDefault();

        const medicamentoId = parseInt(inventarioMedicamentoSelect.value, 10);
        if (isNaN(medicamentoId)) {
            alert('Por favor, selecione um medicamento do catálogo.');
            return;
        }

        const dataValidade = document.getElementById('inventario-validade').value;

        const body = JSON.stringify({
            medicamentoId: medicamentoId,
            quantidadeAtual: parseInt(document.getElementById('inventario-qtd').value, 10),
            limiteAlerta: parseInt(document.getElementById('inventario-limite').value, 10),
            dataValidade: dataValidade ? dataValidade : null // Envia nulo se vazio
        });

        try {
            const response = await fetch('/api/inventario', { method: 'POST', headers, body });
            if (!response.ok) throw new Error('Falha ao adicionar ao inventário');

            formInventario.reset();
            carregarInventario(); // Recarrega a lista de inventário E o select de prescrição

        } catch (error) {
            console.error('Erro ao adicionar inventário:', error);
            alert('Erro ao adicionar ao inventário.');
        }
    };

    // (Inventário) Deletar item do inventário
    const handleDeletarInventario = async (event) => {
        const btn = event.target;
        const id = btn.dataset.id;

        // *** CORREÇÃO: Removido o 'if (confirm(...))' ***
        // A função 'confirm()' não funciona no ambiente (iframe)
        // e impedia a execução do código abaixo.
        try {
            const response = await fetch(`/api/inventario/${id}`, { method: 'DELETE', headers });
            if (!response.ok) throw new Error('Falha ao deletar item do inventário');

            carregarInventario(); // Recarrega lista e select
            carregarPrescricoes(); // Recarrega prescrições (caso alguma tenha sido afetada)
            iniciarMonitoramentoRobusto(); // Reinicia o monitoramento

        } catch (error) {
            console.error('Erro ao deletar inventário:', error);
            // *** CORREÇÃO: Removido 'alert()' que também não funciona ***
            // alert('Erro ao deletar item do inventário.'); 
        }

    };

    // (Prescrição) Resetar formulário
    const resetarFormularioPrescricao = () => {
        formPrescricao.reset();
        modoEdicao = false;
        idPrescricaoEdicao = null;
        formPrescricaoTitulo.textContent = '3. Minhas Prescrições';
        formPrescricaoDesc.textContent = 'Agende lembretes usando itens do *Seu Inventário*.';
        prescricaoSubmitBtn.textContent = 'Adicionar Prescrição';
        cancelarEdicaoBtn.style.display = 'none';
        prescricaoInventarioSelect.value = ""; // Reseta o select
    };

    // (Prescrição) Preencher formulário para edição
    const handleEditarPrescricao = (event) => {
        const btn = event.target;
        modoEdicao = true;
        idPrescricaoEdicao = btn.dataset.id;

        // Popula o formulário
        prescricaoInventarioSelect.value = btn.dataset.itemInventarioId;
        document.getElementById('prescricao-dosagem-texto').value = btn.dataset.dosagemTexto;
        document.getElementById('prescricao-dosagem-qtd').value = btn.dataset.dosagemQtd;
        document.getElementById('data-inicio').value = btn.dataset.inicio;
        document.getElementById('intervalo').value = btn.dataset.intervalo;
        document.getElementById('duracao').value = btn.dataset.duracao;
        document.getElementById('instrucoes').value = btn.dataset.instrucoes;

        // Muda a UI
        formPrescricaoTitulo.textContent = 'Editar Prescrição';
        formPrescricaoDesc.textContent = 'Modifique os dados da sua prescrição.';
        prescricaoSubmitBtn.textContent = 'Salvar Alterações';
        cancelarEdicaoBtn.style.display = 'block';

        formPrescricao.scrollIntoView({ behavior: 'smooth', block: 'start' });
    };

    // (Prescrição) Deletar
    const handleDeletarPrescricao = async (event) => {
        const btn = event.target;
        const id = btn.dataset.id;

        // *** CORREÇÃO: Removido o 'if (confirm(...))' ***
        try {
            const response = await fetch(`/api/prescricoes/${id}`, { method: 'DELETE', headers });
            if (!response.ok) throw new Error('Falha ao deletar');

            carregarPrescricoes();
            iniciarMonitoramentoRobusto(); // Reinicia o monitoramento

            if (modoEdicao && idPrescricaoEdicao == id) {
                resetarFormularioPrescricao();
            }
        } catch (error) {
            console.error('Erro ao deletar:', error);
            // *** CORREÇÃO: Removido 'alert()' que também não funciona ***
            // alert('Erro ao deletar prescrição.');
        }

    };

    // (Prescrição) Submit (Adicionar ou Editar)
    const handlePrescricaoSubmit = async (event) => {
        event.preventDefault();

        const itemInventarioId = parseInt(prescricaoInventarioSelect.value, 10);
        if (isNaN(itemInventarioId)) {
            alert('Por favor, selecione um item do seu inventário.');
            return;
        }

        const body = JSON.stringify({
            itemInventarioId: itemInventarioId,
            quantidadePorDose: parseInt(document.getElementById('prescricao-dosagem-qtd').value, 10),
            dosagemPrescrita: document.getElementById('prescricao-dosagem-texto').value,
            dataHoraInicio: document.getElementById('data-inicio').value,
            intervaloHoras: parseInt(document.getElementById('intervalo').value, 10),
            duracaoDias: parseInt(document.getElementById('duracao').value, 10),
            instrucoes: document.getElementById('instrucoes').value
        });

        const url = modoEdicao ? `/api/prescricoes/${idPrescricaoEdicao}` : '/api/prescricoes';
        const method = modoEdicao ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, { method, headers, body });
            if (!response.ok) {
                // *** SUGESTÃO 2: Tenta ler a mensagem de erro (ex: item vencido) ***
                const errorBody = await response.text();
                throw new Error(errorBody || (modoEdicao ? 'Falha ao atualizar prescrição' : 'Falha ao adicionar prescrição'));
            }

            resetarFormularioPrescricao();
            carregarPrescricoes();
            iniciarMonitoramentoRobusto(); // Reinicia o monitoramento
        } catch (error) {
            console.error('Erro ao salvar prescrição:', error);
            alert('Erro ao salvar prescrição: ' + error.message);
        }
    };

    // --- 3. Lógica de Alerta e Estoque ---

    // (Dose) Registrar dose (Tomada ou Pulada)
    const registrarDose = async (prescricao, dataHoraTomada, status) => {
        const body = JSON.stringify({
            prescricaoId: prescricao.id,
            dataHoraTomada: dataHoraTomada.toISOString(),
            status: status
        });

        try {
            const response = await fetch('/api/doses', { method: 'POST', headers, body });

            if (!response.ok) {
                // *** SUGESTÃO 1: Tenta ler a mensagem de erro (ex: estoque insuficiente) ***
                const errorBody = await response.text();
                throw new Error(errorBody || 'Falha ao registrar dose');
            }

            console.log(`Dose registrada [${status}] para prescrição ${prescricao.id}`);
            carregarRelatorio(); // Atualiza a lista de relatórios

            // Se a dose foi TOMADA, o backend retorna o DTO do inventário atualizado
            if (status === 'TOMADA' && response.status === 201) {
                try {
                    const itemInventarioAtualizado = await response.json();
                    // Atualiza o inventário no frontend (UI)
                    carregarInventario();

                    // Verifica o alerta de estoque
                    if (itemInventarioAtualizado.alertaEstoque) {
                        mostrarAlertaEstoqueGlobal(`Estoque de ${itemInventarioAtualizado.medicamento.nome} está baixo (${itemInventarioAtualizado.quantidadeAtual} restantes).`);
                    }
                } catch (e) {
                    console.log("Dose pulada, sem atualização de estoque.");
                }
            }
        } catch (error) {
            console.error('Erro ao registrar dose:', error);
            alert('Erro ao registrar dose: ' + error.message);
        }
    };

    // (Alerta de Dose)
    const mostrarAlerta = (prescricao, dataDose) => {
        const modal = document.getElementById('alert-modal');
        const span = document.getElementsByClassName('modal-close')[0];
        const btnConfirm = document.getElementById('modal-confirm-dose');
        const btnSkip = document.getElementById('modal-skip-dose');

        // Usa o DTO aninhado
        const mensagem = `Hora de tomar: ${prescricao.medicamento.nome} (${prescricao.dosagemPrescrita}). Instruções: ${prescricao.instrucoes || 'N/A'}`;
        document.getElementById('alert-message').textContent = mensagem;

        // *** CORREÇÃO: Alterado de 'block' para 'flex' ***
        // Isso garante que o modal seja exibido E centralizado,
        // conforme definido no app.css (.modal-overlay { display: flex; })
        modal.style.display = 'flex';

        // Remove listeners antigos para evitar cliques duplicados
        btnConfirm.onclick = null;
        btnSkip.onclick = null;
        span.onclick = null;

        btnConfirm.onclick = () => {
            registrarDose(prescricao, dataDose, "TOMADA");
            modal.style.display = 'none';
        };
        btnSkip.onclick = () => {
            registrarDose(prescricao, dataDose, "PULADA");
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

    // (Alerta de Estoque)
    const mostrarAlertaEstoqueGlobal = (mensagem) => {
        console.warn("ALERTA DE ESTOQUE:", mensagem);
        estoqueAlertBox.style.display = 'block';
        estoqueAlertBox.innerHTML = `<strong>Alerta:</strong> ${mensagem}`;
    };

    const esconderAlertaEstoqueGlobal = () => {
        estoqueAlertBox.style.display = 'none';
    };


    // *** SUGESTÃO 3: Função auxiliar para monitoramento (LÓGICA CORRIGIDA) ***
    const calcularProximaDose = (prescricao, agora) => {
        const inicio = new Date(prescricao.dataHoraInicio);
        const fim = new Date(inicio);
        fim.setDate(fim.getDate() + prescricao.duracaoDias);

        if (agora > fim) return null; // Prescrição terminada

        let proximaDose = new Date(inicio);

        // Se a hora de início for no futuro, a próxima dose é o início
        if (proximaDose > agora) {
            // (Não faz nada, proximaDose já é o início)
        } else {
            // Se a hora de início já passou, precisamos encontrar a dose mais recente
            // que deveria ter sido tomada.
            while (proximaDose < agora) {
                // Calcula a dose seguinte
                let doseSeguinte = new Date(proximaDose);
                doseSeguinte.setHours(doseSeguinte.getHours() + prescricao.intervaloHoras);

                // Se a dose seguinte (ex: 20:00) for DEPOIS de 'agora' (ex: 14:05),
                // significa que a dose que queremos alertar é a 'proximaDose' (ex: 14:00).
                if (doseSeguinte > agora) {
                    break;
                }

                // Se não, continua avançando a 'proximaDose'
                proximaDose.setHours(proximaDose.getHours() + prescricao.intervaloHoras);
            }
        }

        // Neste ponto, proximaDose é a dose que está para acontecer ou que acabou de passar.
        // ex: inicio=14:00, agora=14:05 -> proximaDose = 14:00
        // ex: inicio=14:00, agora=13:59 -> proximaDose = 14:00
        // ex: inicio=14:00, agora=20:01, intervalo=6h -> proximaDose = 20:00

        if (proximaDose > fim) return null; // A dose encontrada está fora da duração

        return proximaDose;
    };

    // *** SUGESTÃO 3: Lógica de Monitoramento (Modificada) ***
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
            if (!proximaDose) return; // Prescrição inativa ou finalizada

            // Define um ID único para este alerta específico
            const alertaId = `${p.id}-${proximaDose.getTime()}`;

            // Calcula a diferença em minutos
            const diffMs = proximaDose.getTime() - agora.getTime();
            const diffMin = Math.round(diffMs / 60000);

            // Se a hora da dose estiver nos próximos 60 segundos (ou já passou)
            // E o alerta ainda não foi mostrado
            if (diffMin <= 1 && !alertasMostrados.has(alertaId)) {
                console.log(`ALERTA: Hora de tomar ${p.medicamento.nome}`);
                mostrarAlerta(p, proximaDose); // Mostra o modal
                alertasMostrados.add(alertaId); // Marca como mostrado
            }
        });
    };

    // *** SUGESTÃO 3: Função principal de monitoramento ***
    const iniciarMonitoramentoRobusto = () => {
        if (monitoramentoIntervalo) {
            clearInterval(monitoramentoIntervalo);
        }
        console.log(`Iniciando monitoramento (verificação a cada ${INTERVALO_VERIFICACAO / 1000}s)`);

        // Limpa os alertas antigos para o caso de recarregar
        alertasMostrados.clear();

        // Verifica imediatamente ao carregar
        verificarDoses();

        // E então agenda a verificação recorrente
        monitoramentoIntervalo = setInterval(verificarDoses, INTERVALO_VERIFICACAO);
    };


    // --- 4. Inicialização e Event Listeners ---

    document.getElementById('logout-button').addEventListener('click', () => {
        // *** SUGESTÃO 3: Limpa o intervalo ao sair ***
        if (monitoramentoIntervalo) {
            clearInterval(monitoramentoIntervalo);
        }
        localStorage.removeItem('token');
        window.location.href = 'login.html';
    });

    // Forms
    formCatalogo.addEventListener('submit', cadastrarMedicamento);
    formInventario.addEventListener('submit', adicionarInventario);
    formPrescricao.addEventListener('submit', handlePrescricaoSubmit);
    cancelarEdicaoBtn.addEventListener('click', resetarFormularioPrescricao);

    // Event Delegation para listas
    listaPrescricoes.addEventListener('click', (event) => {
        if (event.target.classList.contains('edit-btn')) {
            handleEditarPrescricao(event);
        } else if (event.target.classList.contains('delete-prescricao-btn')) {
            handleDeletarPrescricao(event);
        }
    });

    listaInventario.addEventListener('click', (event) => {
        if (event.target.classList.contains('delete-inventario-btn')) {
            handleDeletarInventario(event);
        }
    });

    // Carregamento inicial de dados (em ordem)
    const carregarTudo = async () => {
        await carregarCatalogo();
        await carregarInventario();
        await carregarPrescricoes();
        await carregarRelatorio();
        // *** SUGESTÃO 3: Chama a nova função de monitoramento ***
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


