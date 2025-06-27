# Microsserviços Spring Boot - Order, Payment e Notification

Projeto de demonstração de arquitetura de microsserviços utilizando Spring Boot, RabbitMQ e Redis. Implementa serviços independentes para processamento de pedidos, pagamentos e notificações.

---

## Serviços

### 📝 Order Service

- **Descrição**: Gerencia criação e consulta de pedidos.
- Banco de dados: H2 (memória)
- Integração:
    - Publica mensagens no RabbitMQ para pagamento.

Endpoints principais:

- POST /api/orders
- GET /api/orders/{id}


---

### 💳 Payment Service

- **Descrição**: Processa pagamentos de pedidos.
- Integração:
    - Consome mensagens de RabbitMQ (exchange `order.exchange`) para processar pagamento.
    - Envia mensagem ao Notification Service via RabbitMQ após o processamento.

---

### 🔔 Notification Service

- **Descrição**: Gerencia envio e armazenamento de notificações.
- Integração:
    - Recebe mensagens de RabbitMQ.
    - Armazena notificações no Redis.
- Banco de dados: Redis (em memória)

Endpoints principais:

- GET /api/notifications


---

## Tecnologias

✅ **Spring Boot 3.2.x**

✅ **Spring Data JPA**

✅ **Spring Web**

✅ **RabbitMQ**

✅ **Spring Data Redis**

✅ **Spring Actuator**

✅ **Testcontainers** (para testes de integração)

---

## Arquitetura

- Microsserviços totalmente independentes.
- Comunicação assíncrona via RabbitMQ:
    - Exchange: `order.exchange`
    - Routing Keys:
        - `order.payment` (Order → Payment)
        - `payment.notification` (Payment → Notification)
- Redis para persistência rápida das notificações.

---

## RabbitMQ

### Exchanges e Queues

| Exchange         | Queue                  | Routing Key              |
| ---------------- | ---------------------- | ------------------------ |
| `order.exchange` | `order.payment.queue`  | `order.payment`          |
| `order.exchange` | `payment.notification.queue` | `payment.notification` |

---

## Subir RabbitMQ e Redis localmente

Use o seguinte docker-compose para ambiente local:

```yaml
version: '3.9'

services:

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - 5672:5672
      - 15672:15672
    environment:
      RABBITMQ_DEFAULT_USER: guest
      RABBITMQ_DEFAULT_PASS: guest

  redis:
    image: redis:7
    ports:
      - 6379:6379


```
## Executando a Aplicação
- Rode RabbitMQ e Redis localmente via Docker.

- Execute os microsserviços:

- Order Service

- Payment Service

- Notification Service

A aplicação vai criar e consumir as filas automaticamente.

## Testes
- Unitários: JUnit 5 + Mockito

- Integração: Testcontainers para RabbitMQ e Redis.

## HTTP Status Codes
- 200 OK → Requisição bem-sucedida.

- 201 Created → Entidade criada.

- 400 Bad Request → Dados inválidos.

- 404 Not Found → Entidade não encontrada.

- 500 Internal Server Error → Erro interno no servidor.


## Observabilidade
Ativos endpoints do Spring Actuator em cada serviço:

- /actuator/health

- /actuator/info

- /actuator/metrics