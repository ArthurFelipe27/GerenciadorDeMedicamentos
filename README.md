# 💊 Sistema de Gerenciamento de Medicamentos (Spring Boot + JWT + JS)
Este é um projeto de aplicação web para gerenciamento pessoal de medicamentos, desenvolvido com Spring Boot (Backend API REST) e JavaScript puro (Frontend).

A aplicação permite que usuários se cadastrem, façam login (com autenticação via JWT) e gerenciem seus medicamentos, inventário pessoal e prescrições. O sistema inclui um monitoramento ativo que dispara alertas (pop-ups) no navegador quando é hora de tomar uma dose.

## ✨ Funcionalidades Principais  
🔐 Autenticação de Usuários: Sistema completo de Registro e Login com Spring Security e JSON Web Tokens (JWT).   
📦 Catálogo Global de Medicamentos: Permite adicionar definições-base de medicamentos (nome, dosagem, laboratório).  
🏠 Inventário Pessoal: Permite ao usuário adicionar medicamentos que possui em casa, controlando:  
  - Quantidade em estoque.
  - Data de validade.
  - Alerta de estoque baixo.

🔔 Prescrições e Lembretes: O usuário pode criar prescrições detalhadas (o quê, quanto, quando, por quanto tempo) com base nos itens do seu inventário.  
⏰ Alertas de Dose em Tempo Real: Um monitoramento (a cada 30s) verifica as prescrições ativas e dispara um pop-up na tela quando é o momento de tomar uma dose.  
📊 Relatório de Adesão: O sistema registra o histórico de doses "Tomadas" ou "Puladas", permitindo ao usuário ver sua adesão ao tratamento.  
📉 Alertas Visuais: A interface destaca itens com estoque baixo ou vencidos.  
📱 Design Responsivo: Interface moderna e adaptável construída com CSS puro e ícones Lucide.  

## 🚀 Tecnologias Utilizadas  
### 🧩 Backend 
☕ Java 17  
⚙️ Spring Boot 3  
🌐 Spring Web — Controladores e endpoints RESTful.  
🗄️ Spring Data JPA — Persistência de dados.  
🔐 Spring Security 6 — Autenticação e autorização.  
JWT (Java Web Token) — Geração e validação de tokens para uma API stateless.  

### 🎨 Frontend
HTML5  
CSS3 — Estilização personalizada (sem frameworks como Bootstrap).  
Vanilla JavaScript (ES6+) — Consumo da API, manipulação do DOM e lógica de alertas.  
Lucide Icons — Ícones modernos.  

### 🗃️ Banco de Dados
🐬 MySQL — Banco de dados relacional.  
🔄 Hibernate (JPA) — Mapeamento objeto-relacional (ORM). 

### 🔧 Build
🧰 Maven — Gerenciamento de dependências e build.  

## 📋 Pré-requisitos
Antes de iniciar, certifique-se de ter instalado:  
- Java Development Kit (JDK) 17 ou superior.
- Maven (ou utilize o Maven Wrapper incluído: mvnw).
- Servidor MySQL 8 (ou compatível) em execução.  

 ## ⚙️ Como Executar o Projeto  
1. Clone o repositório
````
git clone https://github.com/ArthurFelipe27/gerenciadordemedicamentos.git
cd gerenciadordemedicamentos
````
2. Configure o banco de dados  
Certifique-se de que o servidor MySQL está ativo e crie o banco:  
````
CREATE DATABASE medicamentos_db;
````
Em seguida, configure o arquivo **src/main/resources/application.properties**:  
````
spring.application.name=controle-medicamentos
# URL de conexão do MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/medicamentos_db

# Seu usuário e senha do MySQL
spring.datasource.username=root
spring.datasource.password=root

# Configurações do Hibernate (JPA)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Chave secreta para o JWT (MUDE PARA UM VALOR SEGURO)
api.security.token.secret=sua-chave-secreta-muito-longa-e-aleatoria-aqui
````
3. Execute a aplicação (Backend)
Usando o Maven Wrapper:
````
# Linux / macOS
./mvnw spring-boot:run

# Windows
./mvnw.cmd spring-boot:run
````
 O servidor Spring Boot iniciará e ficará disponível na porta 8080.  
4. Acesse a aplicação (Frontend) 
Abra o navegador e acesse:  
````
http://localhost:8080
````
O Spring Boot servirá o login.html automaticamente. Crie sua conta e comece a usar!

## 🧪 Como Testar os Alertas de Dose  
Para verificar se o sistema de alertas está funcionando sem precisar esperar horas, siga estes passos:    
1. Vá até a seção "3. Minhas Prescrições" no painel.  
2. Preencha o formulário para criar uma nova prescrição (selecione um item do inventário, etc.).  
3. No campo "Data e Hora de Início", defina um horário que já passou (por exemplo, 1 hora atrás ou ontem).  
4. Defina um "Intervalo" (ex: 6 horas) e "Duração" (ex: 5 dias).  
5. Clique em "Adicionar Prescrição".  
**Por que isso funciona?**  
O monitoramento (a função iniciarMonitoramentoRobusto no app.js) roda a cada 30 segundos. Quando ele rodar, ele vai:
Buscar essa nova prescrição "ativa".  
- Calcular a "próxima dose" (calcularProximaDose).
- Ele verá que a data de início foi no passado e que a próxima dose devida também está no passado (ou seja, proximaDose <= agora).
- Como a dose está "atrasada" e ainda não foi mostrada (!alertasMostrados.has(alertaId)), ele vai disparar a função mostrarAlerta imediatamente.  
O pop-up deve aparecer logo após a próxima verificação de 30 segundos do sistema.  

## 📂 Estrutura de Pastas
controle-medicamentos/  
├── .mvn/  
│   └── wrapper/  
├── src/  
│   ├── main/  
│   │   ├── java/br/com/sistema/controle_medicamentos/  
│   │   │   ├── config/           # SecurityConfig, SecurityFilter  
│   │   │   ├── controller/       # Controladores REST (Auth, Medicamento, etc.)  
│   │   │   ├── dto/              # Data Transfer Objects  
│   │   │   ├── exception/        # Handlers globais de exceção  
│   │   │   ├── model/            # Entidades JPA (Usuario, Prescricao, etc.)  
│   │   │   ├── repository/       # Interfaces Spring Data JPA  
│   │   │   ├── service/          # Lógica de negócio (TokenService)  
│   │   │   └── ControleMedicamentosApplication.java # Classe principal  
│   │   └── resources/  
│   │       ├── static/           # Frontend (HTML, CSS, JS)  
│   │       │   ├── img/  
│   │       │   ├── app.css  
│   │       │   ├── app.js  
│   │       │   ├── global.css  
│   │       │   ├── index.html  
│   │       │   ├── login.css  
│   │       │   └── login.js  
│   │       └── application.properties # Configurações da aplicação  
│   └── test/java/...             # Testes  
├── pom.xml                       # Dependências Maven  
├── mvnw / mvnw.cmd               # Maven Wrapper  
└── README.md  

## 🛣️ Endpoints da API (Rotas)
Todos os endpoints  (exceto ``/api/auth/**``)  exigem um Token JWT no Header **Authorization: Bearer <token>**.
| Método | Endpoint| Descrição|
|--------|---------| ---------|
| POST | /api/auth/login | Autentica um usuário e retorna um token JWT. |
| POST | /api/auth/register | Registra um novo usuário.|
| GET | / **e** /*.html | Serve os arquivos estáticos do frontend. |
| POST | /api/medicamentos | Adiciona um novo medicamento ao catálogo global. |
| GET | /api/medicamentos | Lista todos os medicamentos do catálogo. |
| POST | /api/inventario | Adiciona um item ao inventário pessoal do usuário. |
| GET | /api/inventario | Lista todos os itens do inventário do usuário. |
| PUT | /api/inventario/{id} | Atualiza um item do inventário. |
| DELETE | /api/inventario/{id} | Remove um item do inventário. |
| POST | /api/prescricoes | Cria uma nova prescrição para o usuário. |
| GET | /api/prescricoes | Lista todas as prescrições do usuário. |
| GET | /api/prescricoes/ativas | Lista apenas as prescrições ativas no momento. |
| PUT | /api/prescricoes/{id} | Atualiza uma prescrição existente. |
| DELETE | /api/prescricoes/{id} | Deleta uma prescrição. |
| POST | /api/doses | Registra uma dose (Tomada ou Pulada) e atualiza o estoque. |
| GET | /api/doses | Lista o histórico de doses (Relatório de Adesão). | 

## 📸 Demonstração
<img width="720" height="360" alt="Tela_Cadastro" src="https://github.com/user-attachments/assets/c8aa578a-426a-45a8-951f-6bb0f0dd123c" />
<img width="720" height="360" alt="Tela_Alert" src="https://github.com/user-attachments/assets/3fc01201-cda8-49d9-b10e-2b026014c235" />
<img width="720" height="360" alt="Tela_Principal" src="https://github.com/user-attachments/assets/dcb597bb-d74a-4b5a-886a-525c125d0703" />

### 🧑‍💻 Autor

Arthur Felipe   
📧 arthurfelipedasilvamatosdev@gmail.com  
🌐 https://github.com/ArthurFelipe27  

## 📄 Licença
Este projeto está licenciado sob a Licença MIT.

