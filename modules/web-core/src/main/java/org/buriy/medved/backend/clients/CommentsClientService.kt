package org.buriy.medved.backend.clients

import org.buriy.medved.backend.dto.ArticleDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URI

@Service
class CommentsClientService {
    companion object {
        private val logger = LoggerFactory.getLogger(CommentsClientService::class.java)
    }

    private val client = WebClient.create("http://localhost:8081")

    fun loadCommentsCounter(
        articleDto: ArticleDto,
        function: (t: Long) -> Unit
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/api/v1/commentsByArticleCount")
                    .queryParam("articleId", articleDto.id.toString())
                    .build()
            }

        client
            .get()
            .uri(uriBuilderFunction)
            .retrieve()
            .bodyToMono(Long::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Mono.just(0)
            }
            .subscribe(function)
    }
}