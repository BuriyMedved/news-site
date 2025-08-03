package org.buriy.medved

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement


/**
 * Spring конфигурация приложения
 */
@Configuration
@ComponentScan(
    "org.buriy.medved.backend.repository",
    "org.buriy.medved.backend.service",
    "org.buriy.medved.backend.clients",
)
@EnableTransactionManagement
class DataSourceConfiguration {
//    companion object {
//        private val logger = LoggerFactory.getLogger(DataSourceConfiguration::class.java)
//    }
}