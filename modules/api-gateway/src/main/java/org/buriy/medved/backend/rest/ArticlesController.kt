package org.buriy.medved.backend.rest

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient


@RestController
class ArticlesController(private val webClient: WebClient) {
    companion object {
        val logger = LoggerFactory.getLogger(ArticlesController::class.java)
    }
    @GetMapping(value = ["/api/articles"])
    fun getArticlesApi(
        @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") authorizedClient: OAuth2AuthorizedClient?
    ): Array<String> {
        try {
            val result: Array<String>?
            result = webClient
                .get()
                .uri("http://localhost:9080/api/v1/articles/list")
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(Array<String>::class.java)
                .block()

            return result
        } catch (e: Exception) {
            logger.error("", e)
            return emptyArray()
        }
    }

//    @GetMapping(value = ["/articles"], produces = [MediaType.TEXT_HTML_VALUE])
//    fun getArticles(
////        @RegisteredOAuth2AuthorizedClient("articles-client-authorization-code") authorizedClient: OAuth2AuthorizedClient?
//    ): String {
//        try {
//            val result:String?
//            result = webClient
//                .get()
//                .uri("http://localhost:9080/index.html")
////                .accept(MediaType.TEXT_HTML)
////                .attributes(oauth2AuthorizedClient(authorizedClient))
//                .retrieve()
//                .bodyToMono(String::class.java)
//                .block()
//            println("articles = $result")
//
//            return result
//        } catch (e: Exception) {
//            logger.error("", e)
//            return ""
//        }
//    }

//    @PostMapping(value = ["/authorized"])
//    fun authorized(){
//        println("!!!!!authorized")
//    }
}