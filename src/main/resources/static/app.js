const state = {
  apiBaseUrl: localStorage.getItem('apiBaseUrl') || window.location.origin,
  lastJson: null,
};

const $ = (id) => document.getElementById(id);

const endpoints = {
  health: '/actuator/health/readiness',
  processText: '/api/text/process',
  intentStatus: '/api/intent/status',
  classifyIntent: '/api/intent/classify',
  createSession: '/api/dialogue/sessions',
  listSessions: '/api/dialogue/sessions',
  history: (id) => `/api/dialogue/sessions/${id}/history`,
  sendMessage: (id) => `/api/dialogue/sessions/${id}/messages`,
  llmStatus: '/api/llm/status',
  llmGenerate: '/api/llm/generate',
  performance: '/api/performance/nlp',
};

function apiUrl(path) {
  const base = state.apiBaseUrl.trim().replace(/\/$/, '');
  if (!base) return path;
  return `${base}${path}`;
}

async function request(path, options = {}) {
  const headers = {
    'Accept': 'application/json',
    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
    ...(options.headers || {}),
  };

  const response = await fetch(apiUrl(path), {
    ...options,
    headers,
  });

  const text = await response.text();
  let data;
  try {
    data = text ? JSON.parse(text) : null;
  } catch (error) {
    data = { raw: text };
  }

  if (!response.ok) {
    const message = data?.message || response.statusText || 'Request failed';
    throw new Error(`${response.status}: ${message}`);
  }

  state.lastJson = data;
  renderRaw(data);
  return data;
}

function renderRaw(data) {
  $('rawOutput').textContent = JSON.stringify(data, null, 2);
}

function renderError(containerId, error) {
  $(containerId).innerHTML = `<p class="error-text">${escapeHtml(error.message || String(error))}</p>`;
  renderRaw({ error: error.message || String(error) });
}

function metric(label, value) {
  const template = $('metricTemplate');
  const node = template.content.cloneNode(true);
  node.querySelector('.metric-label').textContent = label;
  node.querySelector('.metric-value').textContent = formatValue(value);
  return node;
}

function renderMetrics(containerId, items) {
  const container = $(containerId);
  container.innerHTML = '';
  items.forEach(([label, value]) => container.appendChild(metric(label, value)));
}

function formatValue(value) {
  if (Array.isArray(value)) return value.join(', ');
  if (value && typeof value === 'object') return JSON.stringify(value);
  if (value === null || value === undefined || value === '') return '—';
  return String(value);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function updateConnectionStatus(status, message) {
  const container = $('connectionStatus');
  const className = status === 'success' ? 'success' : status === 'error' ? 'error' : 'idle';
  container.innerHTML = `<span class="status-dot ${className}"></span><span>${escapeHtml(message)}</span>`;
}

async function checkHealth() {
  try {
    const data = await request(endpoints.health);
    updateConnectionStatus('success', `Сервіс доступний: ${data.status || 'UP'}`);
  } catch (error) {
    updateConnectionStatus('error', `Сервіс недоступний: ${error.message}`);
  }
}

async function processText() {
  try {
    const text = $('textInput').value;
    const data = await request(endpoints.processText, {
      method: 'POST',
      body: JSON.stringify({ text }),
    });
    renderMetrics('textProcessingResult', [
      ['Normalized text', data.normalizedText],
      ['Tokens', data.tokens],
      ['Sentences', data.sentences],
      ['Tokens', data.tokens],
      ['Token count', data.tokens?.length],
      ['Detected intent', data.intent?.intent],
      ['Intent confidence', data.intent?.confidence],
    ]);
  } catch (error) {
    renderError('textProcessingResult', error);
  }
}

async function loadIntentStatus() {
  try {
    const data = await request(endpoints.intentStatus);
    renderMetrics('intentResult', [
      ['Model exists', data.modelExists ?? data.modelAvailable],
      ['Model path', data.modelPath],
      ['Categories', data.categories],
      ['Message', data.message],
    ]);
  } catch (error) {
    renderError('intentResult', error);
  }
}

async function classifyIntent() {
  try {
    const text = $('intentInput').value;
    const data = await request(endpoints.classifyIntent, {
      method: 'POST',
      body: JSON.stringify({ text }),
    });
    renderMetrics('intentResult', [
      ['Detected intent', data.intent || data.detectedIntent],
      ['Best category', data.bestCategory],
      ['Confidence', data.confidence],
      ['Probabilities', data.probabilities],
      ['Normalized text', data.normalizedText],
    ]);
  } catch (error) {
    renderError('intentResult', error);
  }
}

async function createSession() {
  try {
    const title = $('sessionTitle').value;
    const data = await request(endpoints.createSession, {
      method: 'POST',
      body: JSON.stringify({ title }),
    });
    $('sessionId').value = data.id || data.sessionId;
    renderDialogueCards([{ sender: 'SYSTEM', text: `Створено сесію: ${data.title || title}`, detectedIntent: `ID=${data.id || data.sessionId}` }]);
  } catch (error) {
    renderDialogueError(error);
  }
}

async function loadSessions() {
  try {
    const data = await request(endpoints.listSessions);
    const sessions = Array.isArray(data) ? data : data.content || [];
    renderDialogueCards(sessions.map(session => ({
      sender: 'SESSION',
      text: `${session.title || 'Без назви'} — ${session.createdAt || ''}`,
      detectedIntent: `ID=${session.id || session.sessionId}`,
    })));
  } catch (error) {
    renderDialogueError(error);
  }
}

async function sendMessage() {
  try {
    const sessionId = $('sessionId').value;
    if (!sessionId) throw new Error('Спочатку створіть або введіть Session ID.');
    const text = $('dialogueMessage').value;
    const data = await request(endpoints.sendMessage(sessionId), {
      method: 'POST',
      body: JSON.stringify({ text }),
    });
    renderDialogueCards([
      { sender: 'USER', text: data.userMessage || text, detectedIntent: data.intent?.intent || data.detectedIntent || data.intent },
      { sender: 'ASSISTANT', text: data.assistantReply || data.responseText || data.response || 'Відповідь отримано.', detectedIntent: data.intent?.intent || data.detectedIntent || data.intent },
    ]);
  } catch (error) {
    renderDialogueError(error);
  }
}

async function loadHistory() {
  try {
    const sessionId = $('sessionId').value;
    if (!sessionId) throw new Error('Введіть Session ID для завантаження історії.');
    const data = await request(endpoints.history(sessionId));
    const messages = Array.isArray(data) ? data : data.messages || [];
    renderDialogueCards(messages.map(message => ({
      sender: message.sender || message.role,
      text: message.text || message.content || message.message,
      detectedIntent: message.detectedIntent || message.intent,
    })));
  } catch (error) {
    renderDialogueError(error);
  }
}

function renderDialogueCards(messages) {
  const container = $('dialogueResult');
  container.innerHTML = '';
  messages.forEach(message => {
    const type = String(message.sender || '').toUpperCase().includes('USER') ? 'user' : 'system';
    const div = document.createElement('div');
    div.className = `message ${type}`;
    div.innerHTML = `
      <strong>${escapeHtml(message.sender || 'MESSAGE')}</strong>
      <p>${escapeHtml(message.text || '')}</p>
      ${message.detectedIntent ? `<span class="badge">${escapeHtml(message.detectedIntent)}</span>` : ''}
    `;
    container.appendChild(div);
  });
}

function renderDialogueError(error) {
  $('dialogueResult').innerHTML = `<p class="error-text">${escapeHtml(error.message || String(error))}</p>`;
  renderRaw({ error: error.message || String(error) });
}

async function loadLlmStatus() {
  try {
    const data = await request(endpoints.llmStatus);
    renderMetrics('llmResult', [
      ['Enabled', data.enabled],
      ['Provider', data.provider],
      ['Model', data.model],
      ['API key configured', data.apiKeyConfigured],
      ['Status message', data.message],
    ]);
  } catch (error) {
    renderError('llmResult', error);
  }
}

async function generateLlm() {
  try {
    const prompt = $('llmPrompt').value;
    const data = await request(endpoints.llmGenerate, {
      method: 'POST',
      body: JSON.stringify({ prompt }),
    });
    renderMetrics('llmResult', [
      ['Provider', data.provider],
      ['Model', data.model],
      ['External API used', data.externalApiUsed],
      ['Status', data.status],
      ['Duration ms', data.durationMs],
      ['Response', data.responseText],
    ]);
  } catch (error) {
    renderError('llmResult', error);
  }
}

async function runPerformance() {
  try {
    const iterations = Number($('performanceIterations').value || 100);
    const text = $('performanceText').value;
    const data = await request(endpoints.performance, {
      method: 'POST',
      body: JSON.stringify({ text, iterations }),
    });
    renderMetrics('performanceResult', [
      ['Iterations', data.iterations],
      ['Total duration ms', data.totalDurationMs],
      ['Average duration ms', data.averageDurationMs],
      ['Min duration ms', data.minDurationMs],
      ['Max duration ms', data.maxDurationMs],
      ['Throughput / sec', data.throughputPerSecond],
      ['Successful', data.successfulIterations],
      ['Failed', data.failedIterations],
      ['Intent distribution', data.predictedIntentDistribution],
    ]);
  } catch (error) {
    renderError('performanceResult', error);
  }
}

function saveSettings() {
  state.apiBaseUrl = $('apiBaseUrl').value.trim();
  localStorage.setItem('apiBaseUrl', state.apiBaseUrl);
  updateConnectionStatus('idle', 'Base URL збережено. Виконайте перевірку сервісу.');
}

function bindEvents() {
  $('apiBaseUrl').value = state.apiBaseUrl;
  $('saveSettingsBtn').addEventListener('click', saveSettings);
  $('checkHealthBtn').addEventListener('click', checkHealth);
  $('processTextBtn').addEventListener('click', processText);
  $('intentStatusBtn').addEventListener('click', loadIntentStatus);
  $('classifyIntentBtn').addEventListener('click', classifyIntent);
  $('createSessionBtn').addEventListener('click', createSession);
  $('sendMessageBtn').addEventListener('click', sendMessage);
  $('loadHistoryBtn').addEventListener('click', loadHistory);
  $('loadSessionsBtn').addEventListener('click', loadSessions);
  $('llmStatusBtn').addEventListener('click', loadLlmStatus);
  $('generateLlmBtn').addEventListener('click', generateLlm);
  $('performanceBtn').addEventListener('click', runPerformance);
  $('clearOutputBtn').addEventListener('click', () => {
    state.lastJson = null;
    $('rawOutput').textContent = 'Немає даних.';
  });
}

bindEvents();
