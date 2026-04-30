// Gera um sufixo numérico baseado no timestamp atual
const timestamp = new Date().getTime().toString();

// Cria um e-mail único
output.email = 'qa_' + timestamp + '@e2e.com';

// Cria um CPF único de 11 dígitos (pegando os últimos 11 do timestamp)
// Se o timestamp for curto (ex: JS antigo), preenchemos com zeros
let cpf = timestamp;
while (cpf.length < 11) {
    cpf = "0" + cpf;
}
output.cpf = cpf.substring(cpf.length - 11);
