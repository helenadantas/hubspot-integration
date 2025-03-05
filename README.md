# HubSpot Integration API

Este projeto é uma API REST desenvolvida em Java com Spring Boot para integração com a API do HubSpot, implementando autenticação via OAuth 2.0 e webhooks para notificações de eventos.

## Funcionalidades

Autenticação OAuth 2.0 com HubSpot (Authorization Code Flow).

Geração e gerenciamento de tokens de acesso.

Criação de contatos no HubSpot via API.

Recebimento de notificações do HubSpot via webhooks.

Estrutura modular e escalável.

## Tecnologias Utilizadas

Java 8

Spring Boot (Spring Web, Spring Security, Spring Data JPA)

Maven

RestTemplate para chamadas HTTP

Postman (para testes da API)

Banco de Dados (Opcional): PostgreSQL ou MySQL

## Como Executar o Projeto

### 1. Pré-requisitos

Antes de rodar o projeto, você precisa ter instalado:

Java 8 ou superior

Maven (mvn -v para verificar)

Git (git --version para verificar)

Conta no HubSpot Developer Portal (Criar Conta)

### 2. Clonar o Repositório

git clone https://github.com/helenadantas/hubspot-integration.git
cd hubspot-integration

### 3. Configurar Variáveis de Ambiente

Crie um arquivo .env ou configure as variáveis no application.properties:

HUBSPOT_CLIENT_ID=SEU_CLIENT_ID
HUBSPOT_CLIENT_SECRET=SEU_CLIENT_SECRET
HUBSPOT_REDIRECT_URI=http://localhost:8080/oauth/callback

### 4. Construir e Executar a Aplicação

mvn clean install
mvn spring-boot:run

A aplicação estará rodando em http://localhost:8080

## Endpoints Disponíveis

### 1. Autenticação OAuth 2.0

- Gerar URL de autorização:

    GET http://localhost:8080/oauth/authorize

- Processar callback e trocar código por token:

    GET http://localhost:8080/oauth/callback?code=SEU_CODIGO

###  2. Criar Contato no HubSpot

- Requisição:

    POST http://localhost:8080/contacts/create
    
    Headers:
    
    Authorization: Bearer SEU_ACCESS_TOKEN
    Content-Type: application/json
    
    Body:
    
    {
    "properties": {
    "email": "teste@email.com",
    "firstname": "João",
    "lastname": "Silva",
    "phone": "11999999999"
    }
    }

### 3. Webhook para Recebimento de Contatos

- Requisição:

    POST http://localhost:8080/webhook/contact
    
    Body (exemplo enviado pelo HubSpot):
    
    {
    "event": "contact.creation",
    "contactId": "123456",
    "email": "teste@email.com"
    }

## Testando com Postman

1. Importe a API no Postman usando a URL base http://localhost:8080

2. Teste os endpoints:

   - Gere a URL de autorização

   - Pegue o código OAuth e troque pelo token

   - Crie um contato enviando uma requisição POST

   - Receba webhooks ativando um servidor local para capturar eventos

## Melhorias Futuras

- Implementar um cache com Redis para evitar chamadas repetitivas à API do HubSpot.

- Automatizar a renovação de access_token quando expirar.

- Adicionar testes unitários e de integração.
