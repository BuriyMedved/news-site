package org.buriy.medved.backend.routes

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class LoggingGlobalPreFilter: GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        println("LoggingGlobalPreFilter filter")
//        println(exchange.request.headers)
        return chain.filter(exchange)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }
}