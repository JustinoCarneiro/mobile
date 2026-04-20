package com.marketplace.ceara.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Configuração de execução assíncrona.
 *
 * Cria um TaskExecutor baseado em Virtual Threads (Java 21), conforme exigido
 * pelo requisito de non-blocking Background Check.
 *
 * O executor expõe um ThreadFactory com nome descritivo para facilitar diagnósticos
 * em ferramentas de observabilidade (ex: thread dump, JFR).
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        var factory = new CustomizableThreadFactory("vthread-background-check-");
        return Executors.newThreadPerTaskExecutor(
                Thread.ofVirtual().name("vthread-background-check-", 0).factory()
        );
    }
}
