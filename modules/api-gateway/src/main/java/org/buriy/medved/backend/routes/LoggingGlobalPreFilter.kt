package org.buriy.medved.backend.routes

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels
import java.nio.charset.StandardCharsets


class LoggingGlobalPreFilter: GlobalFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        println(exchange.request.headers)
        println(exchange.request.queryParams)

        val decorator: ServerWebExchangeDecorator =
            object : ServerWebExchangeDecorator(exchange) {
                override fun getRequest(): ServerHttpRequest {
                    return RequestLoggingDecorator(exchange.getRequest())
                }
            }
        return chain.filter(decorator)
    }

    override fun getOrder(): Int {
        return Ordered.HIGHEST_PRECEDENCE
    }

    class RequestLoggingDecorator(delegate: ServerHttpRequest?) : ServerHttpRequestDecorator(delegate) {
        override fun getBody(): Flux<DataBuffer> {
            val baos = ByteArrayOutputStream()
            return super.getBody().doOnNext { dataBuffer: DataBuffer ->
                try {
                    Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                    val body = String(baos.toByteArray(), StandardCharsets.UTF_8)
                    println("Request: payload=${ body}")
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        baos.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}