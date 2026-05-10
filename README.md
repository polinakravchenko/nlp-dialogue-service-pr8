# NLP Dialogue Service — Practical Work 8

Тема: **масштабування NLP-застосунку. Інтеграція з LLM та тестування NLP-сервісу.**

Цей проєкт є продовженням окремого REST-сервісу з практичної роботи №7. У практичній роботі №8 сервіс доповнено контейнеризацією, Docker Compose, Kubernetes manifest-файлами, інтеграцією з GPT через OpenAI Responses API, unit-тестами JUnit/Mockito та endpoint-ом для оцінки продуктивності NLP-компонентів.

## Реалізовані завдання

- контейнеризація сервісу через `Dockerfile`;
- створення `docker-compose.yml` для запуску сервісу разом з PostgreSQL;
- створення Kubernetes `deployment.yaml`, `configmap.yaml`, `secret.example.yaml`;
- інтеграція GPT через зовнішній API;
- тестування NLP-компонентів через JUnit та Mockito;
- оцінка продуктивності і стабільності NLP pipeline.

## Основні REST endpoint-и

### OpenNLP text processing

```http
POST /api/text/process
```

```json
{
  "text": "Потрібно класифікувати документ і визначити намір користувача."
}
```

### Intent classification

```http
GET  /api/intent/status
POST /api/intent/train
POST /api/intent/classify
```

### Dialogue model

```http
POST /api/dialogue/sessions
POST /api/dialogue/sessions/{sessionId}/messages
GET  /api/dialogue/sessions/{sessionId}/history
```

### GPT / LLM integration

```http
GET  /api/llm/status
POST /api/llm/generate
```

Example:

```json
{
  "systemPrompt": "You are an NLP assistant for a Spring Boot service.",
  "prompt": "Explain the detected intent for: Потрібно знайти сутності в тексті про Київ і The Fintech Lab."
}
```

The service reads the API key from environment variable:

```bash
OPENAI_API_KEY=your-api-key
```

The model can be configured through:

```bash
OPENAI_MODEL=gpt-5.4-mini
```

If the API key is missing, the endpoint returns a local fallback response. This allows the practical work to be demonstrated without exposing a real API key.

### Performance evaluation

```http
POST /api/performance/nlp
```

```json
{
  "text": "Потрібно класифікувати документ і виконати пошук інформації.",
  "iterations": 100
}
```

The response includes:

- total iterations;
- successful and failed iterations;
- total duration;
- average duration;
- min/max duration;
- throughput per second;
- predicted intent distribution.

## Local run

```bash
mvn spring-boot:run
```

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:nlp_dialogue_db
```

## Run with Docker Compose

From the `docker` directory:

```bash
cd docker
docker compose up --build
```

The application will be available at:

```text
http://localhost:8080
```

PostgreSQL will be available at:

```text
localhost:5432
```

## Run with PostgreSQL profile locally

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Kubernetes manifests

Files are stored in:

```text
k8s/
```

Apply example:

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.example.yaml
kubectl apply -f k8s/deployment.yaml
```

The deployment contains:

- 2 replicas;
- readiness and liveness probes;
- CPU and memory requests/limits;
- configuration via ConfigMap and Secret.

## Tests

Run:

```bash
mvn test
```

Implemented test examples:

- `OpenNlpTextProcessingServiceTest` — normalization and tokenization;
- `DialogueResponseGeneratorTest` — dialogue response generation;
- `LlmGatewayServiceTest` — Mockito-based test for local fallback behavior when API key is absent.

## Project structure

```text
src/main/java/com/example/nlpdialogue
 ├── controller
 ├── dto
 ├── entity
 ├── exception
 ├── repository
 └── service
     ├── llm
     └── performance
```

## Notes for report

For the practical report, the screenshots should demonstrate:

1. Dockerfile and Docker Compose configuration.
2. Successful container startup.
3. Kubernetes deployment manifest.
4. LLM status endpoint.
5. GPT request through `/api/llm/generate`.
6. JUnit/Mockito tests.
7. Performance endpoint response.
8. Actuator health endpoint.

## Self Work — Integrated Web Interface

Тема самостійної роботи: **розробка вебінтерфейсу для взаємодії з NLP-сервісом**.

У цій версії frontend інтегровано безпосередньо в Spring Boot backend через стандартний каталог:

```text
src/main/resources/static/
 ├── index.html
 ├── styles.css
 └── app.js
```

Після запуску backend застосунку UI доступний за адресою:

```text
http://localhost:8080/
```

Такий варіант зручний для навчальної роботи, тому що backend, REST API, NLP-компоненти, LLM endpoint-и, performance endpoint та вебінтерфейс запускаються як один застосунок. Додатковий frontend-сервер або окремий Nginx не потрібні.

Інтерфейс дозволяє:

- перевірити доступність сервісу через `/actuator/health/readiness`;
- виконати OpenNLP-обробку тексту через `/api/text/process`;
- класифікувати намір користувача через `/api/intent/classify`;
- створити діалогову сесію;
- надіслати повідомлення у діалог;
- завантажити історію діалогу з БД;
- перевірити статус LLM-інтеграції;
- виконати генерацію відповіді через `/api/llm/generate`;
- запустити performance test через `/api/performance/nlp`;
- переглянути останню JSON-відповідь backend-сервісу.

Опис самостійної роботи для звіту знаходиться у файлі:

```text
docs/self-work-report.md
```
