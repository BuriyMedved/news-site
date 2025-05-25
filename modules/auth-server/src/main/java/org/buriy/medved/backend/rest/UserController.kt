package org.buriy.medved.backend.rest

import org.buriy.medved.backend.dto.UserDto
import org.buriy.medved.backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
class UserController(
    private val userService: UserService
) : BaseApiController() {

    companion object {
        private val logger = LoggerFactory.getLogger(UserController::class.java)
    }

    @GetMapping("/users/{id}")
    fun getById(@PathVariable id: String) : ResponseEntity<UserDto> {
        val optional = userService.findById(UUID.fromString(id))
        if (optional.isPresent) {
            return ResponseEntity.ok(optional.get())
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping("/users/bulk")
    fun getByIdBulk(@RequestBody ids: Array<String>) : List<UserDto> {
        val uuidList = listOf(*ids).map { idString ->  UUID.fromString(idString) }
        return userService.findUserByIdIsIn(uuidList)
    }

    @GetMapping("/users")
    fun findAll() : List<UserDto> {
        val dtoList = userService.findAll()
        if(logger.isDebugEnabled) {
            logger.debug("Получено ${dtoList.size} пользователей")
        }
        return dtoList
    }


    @DeleteMapping("/users/{id}")
    fun deleteById(@PathVariable id: String) : ResponseEntity<Void> {
        userService.delete(UUID.fromString(id))
        return ResponseEntity.ok().build()
    }

    @PostMapping("/users")
    fun save(@RequestBody userDto: UserDto) : ResponseEntity<UserDto> {
        val dto = userService.save(userDto)
        return ResponseEntity.ok(dto)
    }

    @PutMapping("/users")
    fun update(@RequestBody userDto: UserDto) : ResponseEntity<UserDto> {
        val optional = userService.update(userDto)

        if(optional.isEmpty){
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }

        return ResponseEntity.ok(optional.get())
    }
}