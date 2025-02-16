package org.buriy.medved

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManagerFactory
import org.apache.logging.log4j.LogManager
import org.buriy.medved.config.YamlConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource


/**
 * Spring конфигурация приложения
 */
//@EnableVaadin
@Configuration
//@ComponentScan(
//    "ru.atomskills.common",
//)
//@EnableJpaRepositories(
//    "ru.atomskills.administration.server.repository",
//)
@EnableTransactionManagement

@PropertySource("classpath:application.yml")
//@PropertySource("classpath:application.properties")
//@EnableConfigurationProperties(YamlConfig::class)
open class DataSourceConfiguration {
    companion object {
        private val logger = LogManager.getLogger(DataSourceConfiguration::class.java)
    }

    @Value("\${spring.datasource.url}")
    private lateinit var dataSourceUrl: String

    @Value("\${spring.datasource.username}")
    private lateinit var username: String

    @Value("\${spring.datasource.password}")
    private lateinit var password: String


    @Bean
    open fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        return LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = dataSource
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            setPackagesToScan("ru.atomskills.administration.server.domain",
                    "ru.atomskills.report.server.domain",
                    "ru.atomskills.passage_time_report.domain",
                    "ru.atomskills.tasks.domain",
                    "ru.atomskills.external.entity",
                    "ru.atomskills.tasks.comment.entity")
        }
    }

    @Bean
    open fun transactionManager(entityManagerFactory: EntityManagerFactory) = JpaTransactionManager(entityManagerFactory)

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