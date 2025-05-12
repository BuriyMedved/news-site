package org.buriy.medved.backend.entities

import jakarta.persistence.*
import java.util.Collections

@Entity
@Table(name = "z_user")
data class User (
    @Column(name = "z_login", nullable = false, length = 2048)
    var login: String,

    @Column(name = "z_password", nullable = false, length = 2048)
    var password: String,

    @Column(name = "z_name", nullable = false, length = 2048)
    var name: String,

    @Column(name = "z_email", nullable = false, length = 2048)
    var email: String,
): BaseEntity(){
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "z_role_ptr")
    var roles: MutableList<RoleToUserCross> = Collections.emptyList()

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}