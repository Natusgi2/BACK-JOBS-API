package com.uap.proiv.jobs.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.server.webmvc.GraphQlHttpHandler;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GraphQlConfig {

    @Bean
    @ConditionalOnBean(GraphQlHttpHandler.class) // Evita que falle si el handler no está en el contexto
    public RouterFunction<ServerResponse> customGraphQlRouterFunction(GraphQlHttpHandler httpHandler) {
        return RouterFunctions.route()
                .POST("/test", httpHandler::handleRequest)
                .build();
    }
}