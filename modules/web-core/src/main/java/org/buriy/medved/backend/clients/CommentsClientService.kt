package org.buriy.medved.backend.clients

import org.buriy.medved.backend.dto.ArticleDto
import org.buriy.medved.backend.dto.CommentDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@Service
class CommentsClientService(
    private val commentsWebClient :WebClient
) {
    companion object {
        private val logger = LoggerFactory.getLogger(CommentsClientService::class.java)
    }

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

        commentsWebClient
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

    fun loadComments(
        articleDto: ArticleDto,
        loadListener: (t: CommentDto) -> Unit,
        onComplete: Runnable
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/api/v1/commentsByArticle")
                    .queryParam("articleId", articleDto.id.toString())
                    .build()
            }

        commentsWebClient
            .get()
            .uri(uriBuilderFunction)
            .retrieve()
            .bodyToFlux(CommentDto::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Flux.just()
            }
            .doOnComplete(onComplete)
            .subscribe(loadListener)
    }

    fun save(
        commentDto: CommentDto,
        onSuccess: (t: Void) -> Unit
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/api/v1/save")
                    .build()
            }

        commentsWebClient
            .post()
            .uri(uriBuilderFunction)
            .bodyValue(commentDto)
            .retrieve()
            .bodyToMono(Void::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Mono.empty()
            }
            .doOnSuccess (onSuccess)
            .subscribe()
    }
}