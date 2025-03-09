package org.buriy.medved

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.DriverManager
import java.sql.SQLException


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
@PropertySource("classpath:application.yml")
//@PropertySource("classpath:application.properties")
class DataSourceConfiguration {
    companion object {
        private val logger = LoggerFactory.getLogger(DataSourceConfiguration::class.java)
    }

    @Value("\${spring.datasource.url}")
    private lateinit var dataSourceUrl: String

    @Value("\${spring.datasource.username}")
    private lateinit var username: String

    @Value("\${spring.datasource.password}")
    private lateinit var password: String


    private fun getDatabaseName(url: String): String {
        return url.substringAfterLast("/")
    }

    private fun databaseExists(dbName: String): Boolean {

        val baseUrl = dataSourceUrl.substringBeforeLast("/") + "/postgres"
        val query = "SELECT 1 FROM pg_database WHERE datname = ?"
        return try {
            DriverManager.getConnection(baseUrl, username, password).use { connection ->
                connection.prepareStatement(query).use { preparedStatement ->
                    preparedStatement.setString(1, dbName)
                    preparedStatement.executeQuery().use { resultSet -> resultSet.next() }
                }
            }
        } catch (e: SQLException) {
            logger.error("Ошибка при проверке базы данных: ${e.message}")
            false
        }
    }

    private fun createDatabase(dbName: String) {

        val baseUrl = dataSourceUrl.substringBeforeLast("/") + "/postgres"
        try {
            DriverManager.getConnection(baseUrl, username, password).use { connection ->
                connection.createStatement().use { statement ->
                    statement.executeUpdate("CREATE DATABASE \"$dbName\"")
                    logger.info("База данных $dbName успешно создана.")
                }
            }
        } catch (e: SQLException) {
            logger.error("Ошибка при создании базы данных: ${e.message}")
        }
    }

    @PostConstruct
    fun initialize() {
        val dbName = getDatabaseName(dataSourceUrl)

        if (!databaseExists(dbName)) {
            logger.info("База данных $dbName отсутствует. Создание...")
            createDatabase(dbName)
        } else {
            logger.info("База данных $dbName уже существует.")
        }
    }
}