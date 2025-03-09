package org.buriy.medved.backend.clients

import org.buriy.medved.backend.dto.TagDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import java.net.URI

@Service
class TagsClientService {
    companion object {
        private val logger = LoggerFactory.getLogger(TagsClientService::class.java)
    }

    private val baseURL = "http://localhost:8082"
    private val client = WebClient.create(baseURL)
    val imageURL = "$baseURL/api/v1/tagImage?tagId="

    fun loadTags(
        function: (t: TagDto) -> Unit,
        onComplete: Runnable
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/api/v1/tags")
                    .build()
            }
        
        client
            .get()
            .uri(uriBuilderFunction)
            .retrieve()
            .bodyToFlux(TagDto::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Flux.just()
            }
            .doOnComplete(onComplete)
            .subscribe(function)
    }
}