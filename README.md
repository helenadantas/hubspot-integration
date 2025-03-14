# HubSpot Integration API

Esta API REST foi desenvolvida em Java utilizando Spring Boot para integração com a API do HubSpot. Além disso, foi utilizada autenticação via OAuth 2.0 e webhooks para notificações de eventos.

## Funcionalidades

1. Geração da Authorization URL:
   - Endpoint responsável por gerar e retornar a URL de autorização para iniciar o
   fluxo OAuth com o HubSpot.
2. Processamento do Callback OAuth:
   - Endpoint recebe o código de autorização fornecido pelo HubSpot e realiza a
   troca pelo token de acesso.
3. Criação de Contatos:
   - Endpoint que faz a criação de um Contato no CRM através da API. O endpoint
   deve respeitar as políticas de rate limit definidas pela API.
4. Recebimento de Webhook para Criação de Contatos:
   - Endpoint que escuta e processa eventos do tipo "contact.creation", enviados
   pelo webhook do HubSpot.

---

## Tecnologias Utilizadas

- Java 8;
- Spring Boot (Spring Web, Spring Security, Spring Data JPA);
- Maven;
- RestTemplate para chamadas HTTP.

---

## Como Executar o Projeto

### 1. Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

- Java 8; 
- Maven;
- Git; 
- Conta no HubSpot Developer Portal (Criar Conta).

### 2. Clonar o Repositório

- git clone https://github.com/helenadantas/hubspot-integration.git

### 3. Configuração da Aplicação no HubSpot

#### Criar Conta e Aplicação
1. Acesse [HubSpot Developer](https://developers.hubspot.com/) e crie uma conta.
2. No **Developer Portal**, vá para **Apps** e clique em **Create an app**.
3. Defina um nome e configure as permissões:
    - `contacts` → **Write**
    - `webhooks` → **Read**
4. Adicione um **Redirect URI**, que pode ser um domínio local exposto via `ngrok`.

#### Expondo a API Local com `ngrok`
1. Instale o `ngrok` (se necessário):
   ```sh
   npm install -g ngrok
   ```
2. Inicie o túnel para a porta do seu servidor:
   ```sh
   ngrok http 3000
   ```
3. Copie a URL gerada (`https://subdominio.ngrok.io`) e configure no HubSpot.

#### Configurar Webhook para Criação de Contatos
1. No HubSpot, vá para **Webhooks** e clique em **Create Subscription**.
2. Escolha o evento **contact.creation** e defina o **Webhook URL** com a URL do `ngrok`, ex:
   ```
   https://subdominio.ngrok.io/webhook/contact
   ```
3. Salve e publique a aplicação.

#### Testando a Integração
Crie um contato via API para testar:
```sh
curl --request POST \
  --url 'https://api.hubapi.com/crm/v3/objects/contacts' \
  --header 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
  --header 'Content-Type: application/json' \
  --data '{
    "properties": {
      "email": "teste@example.com",
      "firstname": "Teste",
      "lastname": "Contato"
    }
  }'
```
Se configurado corretamente, o evento será recebido no webhook.


### 4. Configurar Variáveis de Ambiente

Configure as variáveis no application.properties:
```
HUBSPOT_CLIENT_ID=SEU_CLIENT_ID 
HUBSPOT_CLIENT_SECRET=SEU_CLIENT_SECRET
HUBSPOT_REDIRECT_URI=http://localhost:8080/oauth/callback
spring.security.oauth2.client.registration.hubspot.client-id=SEU_CLIENT_ID
spring.security.oauth2.client.registration.hubspot.client-secret=SEU_CLIENT_SECRET
```

### 5. Construir e Executar a Aplicação

mvn clean install ou mvn clean install -DskipTests
mvn spring-boot:run

A aplicação estará rodando em http://localhost:8080

---

## Endpoints Disponíveis

### 1. Autenticação OAuth 2.0

- Gerar URL de autorização:

    ```GET http://localhost:8080/oauth/authorize```


- Processar callback e trocar código por token:

    ```GET http://localhost:8080/oauth/callback?code=SEU_CODIGO```

###  2. Criar Contato no HubSpot

- Requisição:

    ```
    POST http://localhost:8080/contacts/create

    Headers:
    
    Authorization: Bearer SEU_ACCESS_TOKEN
    Content-Type: application/json
    
    Body: {
      "properties": {
        "email": "teste@email.com",
        "firstname": "João",
        "lastname": "Silva",
        "phone": "11999999999"
      }
    }
  ```

### 3. Webhook para Recebimento de Contatos

- Requisição:

  ```
    POST http://localhost:8080/webhook/contact
    
    Body (exemplo enviado pelo HubSpot):
    
    [
      {
        "appId": 8801992,
        "eventId": 100,
        "subscriptionId": 3262569,
        "portalId": 49439948,
        "occurredAt": 1741232400213,
        "subscriptionType": "contact.creation",
        "attemptNumber": 0,
        "objectId": 123,
        "changeSource": "CRM",
        "changeFlag": "NEW"
      }
    ]
  ```
---

## Testando com Postman

1. Importe a API no Postman usando a URL base http://localhost:8080

2. Teste os endpoints:

   - Gere a URL de autorização;
   - Pegue o código OAuth e troque pelo token;
   - Crie um contato enviando uma requisição POST;
   - Receba webhooks ativando um servidor local para capturar eventos.

---

## Melhorias Futuras

- Implementar um cache com Redis para evitar chamadas repetitivas à API do HubSpot;
- Implementar um banco de dados;
- Adicionar testes unitários e de integração;
- Armazenar tokens e automatizar a renovação.
