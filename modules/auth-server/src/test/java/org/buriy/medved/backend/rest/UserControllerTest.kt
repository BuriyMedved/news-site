package org.buriy.medved.backend.rest;

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.transaction.Transactional
import org.buriy.medved.backend.dto.UserDto
import org.buriy.medved.backend.repository.UserRepository
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*


/**
 * Test class for the {@link org.buriy.medved.backend.rest.UserController}
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerTest {
    companion object {
        private const val NEW_USER_LOGIN = "7b89b6e9-7004-4b0f-90bb-0ff1ad4dd644"

        private val newUserDto = UserDto(UUID.randomUUID(),
            NEW_USER_LOGIN,
            "test",
            "test",
            "test@test.ru",
            HashSet()
        )
    }


    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var userRepository: UserRepository

    @BeforeEach
//@Transactional не поддерживается на методе @BeforeEach
//https://docs.spring.io/spring-framework/reference/testing/testcontext-framework/tx.html
    fun setup() {

    }

    @Test
    @Throws(Exception::class)
    fun getById_given_id_when_clean_then_not_found() {
        mockMvc.perform(
            get(
                "/api/v1/auth/users/$NEW_USER_LOGIN",
                ""
            )
        )
        .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun getById() {
        val savedDto = doSave()

        val result = mockMvc.perform(
            get(
                "/api/v1/auth/users/${savedDto.id}",
                ""
            )
        )
            .andExpect(status().isOk)
            .andReturn()

        val userDto = userDtoFromMvcResult(result)

        assertEquals(savedDto.id, userDto.id)//идентификаторы не должны совпадать т.к при сохранении присваивается новый идентификатор
        assertEquals(newUserDto.name, userDto.name)
        assertEquals(newUserDto.login, userDto.login)
        assertEquals(newUserDto.email, userDto.email)
    }

    @Test
    @Throws(Exception::class)
    fun findAll() {
        val savedDto = doSave()
        val mvcResult = mockMvc.perform(get("/api/v1/auth/users"))
            .andExpect(status().isOk())
            .andReturn()
        val userDtoList = userDtoListFromMvcResult(mvcResult)

        assertEquals(userDtoList.size, 1)

        val userDto = userDtoList[0]

        assertEquals(savedDto.id, userDto.id)//идентификаторы не должны совпадать т.к при сохранении присваивается новый идентификатор
        assertEquals(newUserDto.name, userDto.name)
        assertEquals(newUserDto.login, userDto.login)
        assertEquals(newUserDto.email, userDto.email)
    }

    @Test
    @Throws(Exception::class)
    fun getByIdBulk_given_random_then_empty() {
        val ids = arrayOf(UUID.randomUUID())
        val givenData = ObjectMapper().writeValueAsString(ids)

        val expectedResponse = ObjectMapper().writeValueAsString(emptyArray<String>())

        mockMvc.perform(
            post("/api/v1/auth/users/bulk")
                .content(givenData)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andExpect(content().json(expectedResponse))
    }

    @Test
    @Throws(Exception::class)
    fun getByIdBulk() {
        val savedDto = doSave()

        val givenData = ObjectMapper().writeValueAsString(arrayOf(savedDto.id))

        val mvcResult = mockMvc.perform(
            post("/api/v1/auth/users/bulk")
                .content(givenData)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn()

        val userDtoList = userDtoListFromMvcResult(mvcResult)

        assertEquals(userDtoList.size, 1)

        val userDto = userDtoList[0]

        assertEquals(savedDto.id, userDto.id)
        assertEquals(newUserDto.name, userDto.name)
        assertEquals(newUserDto.login, userDto.login)
        assertEquals(newUserDto.email, userDto.email)
    }

    @Test
    @Throws(Exception::class)
    fun deleteById() {
        val savedDto = doSave()

        mockMvc.perform(
            delete(
                "/api/v1/auth/users/${savedDto.id}",
                ""
            )
        )
            .andExpect(status().isOk())

        val userOptional = userRepository.findByLogin(newUserDto.login)
        assertTrue(userOptional.isEmpty)
    }

    @Test
    @Throws(Exception::class)
    fun save() {
        doSave()

        val userOptional = userRepository.findByLogin(NEW_USER_LOGIN)
        val user = userOptional.get()

        assertEquals(newUserDto.name, user.name)
        assertEquals(newUserDto.login, user.login)
        assertEquals(newUserDto.email, user.email)
        assertNotEquals(newUserDto.id, user.id)
    }


    @Test
    @Throws(Exception::class)
    fun update() {
        val savedDto = doSave()
        savedDto.name = "new-name"

        val givenData = ObjectMapper().writeValueAsString(savedDto)

        val mvcResult = mockMvc.perform(
            put("/api/v1/auth/users")
                .content(givenData)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn()

        val updatedDto = userDtoFromMvcResult(mvcResult)
        
        assertEquals(savedDto.id, updatedDto.id)
        assertEquals(savedDto.name, updatedDto.name)
        assertEquals(newUserDto.login, updatedDto.login)
        assertEquals(newUserDto.email, updatedDto.email)
    }

    private fun doSave(): UserDto {
        val givenData = ObjectMapper().writeValueAsString(newUserDto)

        val result = mockMvc.perform(
            post("/api/v1/auth/users")
                .content(givenData)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk())
            .andReturn()

        return userDtoFromMvcResult(result)
    }

    private fun userDtoFromMvcResult(result: MvcResult): UserDto {
        return jacksonObjectMapper().readValue(result.response.contentAsString, UserDto::class.java)
    }

    private fun userDtoListFromMvcResult(result: MvcResult): List<UserDto> {
        return jacksonObjectMapper().readValue(result.response.contentAsString,
            TypeFactory.defaultInstance().constructCollectionType(
                MutableList::class.java,
                UserDto::class.java
            ))
    }
}
