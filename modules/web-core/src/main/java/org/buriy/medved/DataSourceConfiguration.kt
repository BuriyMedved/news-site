package org.buriy.medved

import jakarta.annotation.PostConstruct
import jakarta.persistence.EntityManagerFactory
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.sql.DriverManager
import java.sql.SQLException
import javax.sql.DataSource


/**
 * Spring конфигурация приложения
 */
@Configuration
@ComponentScan(
    "org.buriy.medved.repository",
    "org.buriy.medved.service",
)
@EnableTransactionManagement
@PropertySource("classpath:application.yml")
//@PropertySource("classpath:application.properties")
class DataSourceConfiguration {
    companion object {
        private val logger = LogManager.getLogger(DataSourceConfiguration::class.java)
    }

    @Value("\${spring.datasource.url}")
    private lateinit var dataSourceUrl: String

    @Value("\${spring.datasource.username}")
    private lateinit var username: String

    @Value("\${spring.datasource.password}")
    private lateinit var password: String

    @Value("\${spring.jpa.hibernate.ddl-auto}")
    private lateinit var ddlAuto: String


//    @Bean
//    open fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
//        return LocalContainerEntityManagerFactoryBean().apply {
//            this.dataSource = dataSource
//            jpaVendorAdapter = HibernateJpaVendorAdapter()
//            setPackagesToScan(
//                    "org.buriy.medved.entities"
//            )
////            jpaPropertyMap = mapOf(
////                "hibernate.hbm2ddl.auto" to "update",
////            )
//            println("ddlAuto = " + ddlAuto)
//        }
//    }
//
//    @Bean
//    open fun transactionManager(entityManagerFactory: EntityManagerFactory) = JpaTransactionManager(entityManagerFactory)

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