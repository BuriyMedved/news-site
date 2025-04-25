package org.buriy.medved.backend.rest

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono


@RestController
class ArticlesController(private val webClient: WebClient) {
    companion object {
        val logger = LoggerFactory.getLogger(ArticlesController::class.java)
    }

    @Async
    @GetMapping(value = ["/api/articles"])
    fun getArticlesApi(
        @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") authorizedClient: OAuth2AuthorizedClient?
    ): Mono<Array<String>> {
        try {
            val bodyToMono = webClient
                .get()
                .uri("http://localhost:9080/api/v1/articles/list")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(Array<String>::class.java)
            return bodyToMono
        } catch (e: Exception) {
            logger.error("", e)
            return Mono.just(emptyArray())
        }
    }

//    @Async
//    @GetMapping(value = ["/articles"])
//    fun getArticles(
//        @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") authorizedClient: OAuth2AuthorizedClient?
//    ): Mono<String> {
//        try {
//            val userInfo = webClient
//                .get()
//                .uri("http://127.0.0.2:9000/userinfo")
//                .attributes(oauth2AuthorizedClient(authorizedClient))
//                .retrieve()
//                .bodyToMono(String::class.java)
//                .onErrorResume {
//                    println(it)
//                    it.printStackTrace()
//                    Mono.empty()
//                }
//                .doOnSuccess {
//                    println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//                    println(it)
//                    println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
//                }
////            val toFuture = doOnSuccess.toFuture()
////            val result = toFuture.get(5000, TimeUnit.MILLISECONDS)
////            println("result = ${result}")
//
//            val bodyToMono = webClient
//                .get()
//                .uri("http://localhost:9080/articles")
//                .attributes(oauth2AuthorizedClient(authorizedClient))
//                .retrieve()
//                .bodyToMono(String::class.java)
//
//            return userInfo.then(bodyToMono)
////                .subscribe { println(it) }
////            val res = bodyToMono.subscribe()
////            println("res = ${res}")
////            val get = toFuture1.get(5000, TimeUnit.MILLISECONDS)
////            println("get = ${get}")
//
////            return webClient
////                .get()
////                .uri("http://localhost:9080/articles")
////                .attributes(oauth2AuthorizedClient(authorizedClient))
////                .retrieve()
////                .bodyToMono(String::class.java)
//        } catch (e: Exception) {
//            logger.error("", e)
//            return Mono.just("")
//        }
//    }
}