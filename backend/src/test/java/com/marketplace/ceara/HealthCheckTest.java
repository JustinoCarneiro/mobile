package com.marketplace.ceara;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HealthCheckTest — Validação de Ambiente
 *
 * Propósito: confirmar que o ambiente de testes está funcional e que
 * os recursos-chave do Java 21 estão disponíveis, conforme definido em:
 *   0_test_strategy.md  §1 (Back-end) e §3 (Critérios de Blindagem)
 *
 * Ferramentas: JUnit 5 (incluso via spring-boot-starter-test)
 * Execução:    mvn test -pl backend -Dtest=HealthCheckTest
 */
@DisplayName("Health Check — Ambiente de Testes")
class HealthCheckTest {

    // ------------------------------------------------------------------ //
    //  1. Sanidade básica                                                  //
    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("JUnit 5 está ativo e executando corretamente")
    void junitEstaFuncional() {
        assertTrue(true, "Se este teste falhar, o JUnit não está configurado.");
    }

    // ------------------------------------------------------------------ //
    //  2. Java 21 — Records                                                //
    // ------------------------------------------------------------------ //

    /** Record simples usado apenas neste teste. */
    record Produto(String nome, double preco) {}

    @Test
    @DisplayName("Java 21 — Records estão disponíveis")
    void recordsEstaoDisponiveis() {
        Produto p = new Produto("Produto A", 29.99);
        assertEquals("Produto A", p.nome());
        assertEquals(29.99, p.preco(), 0.001);
    }

    // ------------------------------------------------------------------ //
    //  3. Java 21 — Virtual Threads (Loom)                                 //
    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("Java 21 — Virtual Threads estão habilitadas")
    void virtualThreadsEstaoHabilitadas() throws InterruptedException {
        AtomicBoolean executou = new AtomicBoolean(false);

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                // Confirma que a thread atual é uma Virtual Thread
                assertTrue(Thread.currentThread().isVirtual(),
                        "A thread deveria ser uma Virtual Thread (Java Loom).");
                executou.set(true);
            });
        }

        // Aguarda a conclusão (o bloco try-with-resources já faz o shutdown)
        assertTrue(executou.get(),
                "A Virtual Thread não executou a tarefa dentro do prazo.");
    }

    // ------------------------------------------------------------------ //
    //  4. Java 21 — Pattern Matching (instanceof)                          //
    // ------------------------------------------------------------------ //

    @Test
    @DisplayName("Java 21 — Pattern Matching (instanceof) está disponível")
    void patternMatchingEstaDisponivel() {
        Object valor = "Marketplace Ceará";

        if (valor instanceof String s) {
            assertTrue(s.contains("Ceará"),
                    "Pattern Matching deveria extrair a String corretamente.");
        } else {
            fail("Pattern Matching instanceof falhou.");
        }
    }
}
