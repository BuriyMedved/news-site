package org.buriy.medved.backend.clients

import org.buriy.medved.backend.dto.UserDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*

@Service
class UserClientService(
    private val usersWebClient : WebClient
) {
    companion object {
        private val logger = LoggerFactory.getLogger(UserClientService::class.java)
    }


    fun findBulk(
        uuidList: List<UUID>,
        loadListener: (t: UserDto) -> Unit,
        onComplete: Runnable
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/users/bulk").build()
            }
        val ids = uuidList.map { uuid -> uuid.toString() }.toTypedArray()
        usersWebClient
            .post()
            .uri(uriBuilderFunction)
            .bodyValue(ids)
            .retrieve()
            .bodyToFlux(UserDto::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Flux.just()
            }
            .doOnComplete(onComplete)
            .subscribe(loadListener)
    }


    fun findAll(
        loadListener: (t: UserDto) -> Unit,
        onComplete: Runnable
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/users").build()
            }

        usersWebClient
            .get()
            .uri(uriBuilderFunction)
            .retrieve()
            .bodyToFlux(UserDto::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Flux.just()
            }
            .doOnComplete(onComplete)
            .subscribe(loadListener)
    }

    fun delete(
        id: UUID,
        onSuccess: (t: Void?) -> Unit
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/users/$id")
                    .build()
            }

        usersWebClient
            .delete()
            .uri(uriBuilderFunction)
            .retrieve()
            .bodyToMono(Void::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Mono.empty()
            }
            .doOnSuccess (onSuccess)
            .subscribe()
    }

    fun save(
        userDto: UserDto,
        onSuccess: (t: UserDto) -> Unit
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/users")
                    .build()
            }

        usersWebClient
            .post()
            .uri(uriBuilderFunction)
            .bodyValue(userDto)
            .retrieve()
            .bodyToMono(UserDto::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Mono.empty()
            }
            .doOnSuccess (onSuccess)
            .subscribe()
    }

    fun update(
        userDto: UserDto,
        onSuccess: (t: UserDto) -> Unit
    ) {
        val uriBuilderFunction: (t: UriBuilder) -> URI =
            { builder ->
                builder.path("/users")
                    .build()
            }

        usersWebClient
            .put()
            .uri(uriBuilderFunction)
            .bodyValue(userDto)
            .retrieve()
            .bodyToMono(UserDto::class.java)
            .onErrorResume { e ->
                logger.debug("Не удалось подключиться", e)
                Mono.empty()
            }
            .doOnSuccess (onSuccess)
            .subscribe()
    }
}