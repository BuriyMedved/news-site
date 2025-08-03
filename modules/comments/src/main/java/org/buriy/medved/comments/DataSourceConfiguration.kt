package org.buriy.medved.comments

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement


/**
 * Spring конфигурация приложения
 */
@Configuration
@ComponentScan(
    "org.buriy.medved.comments.backend.repository",
    "org.buriy.medved.comments.backend.service",
    "org.buriy.medved.comments.backend.rest.v1",
)
@EnableTransactionManagement
class DataSourceConfiguration {
//    companion object {
//        private val logger = LoggerFactory.getLogger(DataSourceConfiguration::class.java)
//    }
}