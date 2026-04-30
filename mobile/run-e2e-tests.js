const { execSync } = require('child_process');
const http = require('http');

// =============================================
// Gerador de CPF válido (algoritmo oficial)
// =============================================
function generateCPF() {
  const rnd = () => Math.floor(Math.random() * 9);
  const n = Array.from({ length: 9 }, rnd);

  let d1 = n.reduce((sum, v, i) => sum + v * (10 - i), 0);
  d1 = 11 - (d1 % 11);
  if (d1 >= 10) d1 = 0;

  let d2 = d1 * 2 + n.reduce((sum, v, i) => sum + v * (11 - i), 0);
  d2 = 11 - (d2 % 11);
  if (d2 >= 10) d2 = 0;

  return [...n, d1, d2].join('');
}

// =============================================
// Chamada HTTP simples (sem dependências)
// =============================================
function postJSON(url, data) {
  return new Promise((resolve, reject) => {
    const body = JSON.stringify(data);
    const parsed = new URL(url);
    const options = {
      hostname: parsed.hostname,
      port: parsed.port,
      path: parsed.pathname,
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(body),
      },
    };
    const req = http.request(options, (res) => {
      let responseBody = '';
      res.on('data', (chunk) => (responseBody += chunk));
      res.on('end', () => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(JSON.parse(responseBody));
        } else {
          reject(new Error(`HTTP ${res.statusCode}: ${responseBody}`));
        }
      });
    });
    req.on('error', reject);
    req.write(body);
    req.end();
  });
}

// =============================================
// Main
// =============================================
async function main() {
  const timestamp = Date.now();
  const clientEmail = `qa_client_${timestamp}@e2e.com`;
  const clientCpf = generateCPF();
  const providerEmail = `qa_provider_${timestamp}@e2e.com`;
  const providerCpf = generateCPF();

  const API_BASE = 'http://192.168.18.250:8080/api/v1';

  console.log('\n=============================================');
  console.log('🤖 PREPARANDO AMBIENTE E2E');
  console.log('=============================================');

  // ---------------------------------------------------------
  // Passo 1: Cadastrar um Prestador via API para ter dados
  // ---------------------------------------------------------
  console.log('\n📌 Passo 1: Cadastrando prestador via API...');
  try {
    const providerResult = await postJSON(`${API_BASE}/auth/register/provider`, {
      fullName: 'Prestador E2E',
      email: providerEmail,
      password: '12345678',
      cpf: providerCpf,
      category: 'Eletricista',
      bio: 'Eletricista profissional para testes E2E',
      latitude: -3.7172,
      longitude: -38.5433,
    });
    console.log(`   ✅ Prestador criado: ${providerEmail}`);
  } catch (err) {
    console.error(`   ⚠️  Erro ao criar prestador (pode já existir): ${err.message}`);
  }

  // ---------------------------------------------------------
  // Passo 2: Rodar os testes do Maestro
  // ---------------------------------------------------------
  console.log('\n📌 Passo 2: Rodando testes Maestro...');
  console.log(`   📧 Cliente E-mail: ${clientEmail}`);
  console.log(`   🪪 Cliente CPF: ${clientCpf}`);
  console.log('=============================================\n');

  try {
    execSync(
      `maestro test .maestro/e2e_full_flow.yaml -e USER_EMAIL="${clientEmail}" -e USER_CPF="${clientCpf}"`,
      { stdio: 'inherit' }
    );
    console.log('\n✅ TODOS OS TESTES PASSARAM!');
  } catch (error) {
    console.error('\n❌ Os testes falharam. Verifique os logs acima.');
    process.exit(1);
  }
}

main();
