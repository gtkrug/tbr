package tm.binding.registry

import grails.plugin.springsecurity.SpringSecurityService

class User {

    transient SpringSecurityService springSecurityService;

    String username
    String password
    String name
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired
    Contact contact

    static transients = ['springSecurityService']

    static constraints = {
        username blank: false, unique: true
        name blank: false
        password blank: false, password: true
        contact nullable: false
    }

    static mapping = {
        table name: 'user'
        password column: '`pass_hash`'
        contact column: 'contact_ref', fetch: 'join'
    }

    Boolean isAdmin() {
        Set<UserRole> roles = UserRole.findAllByUser(this)
        boolean hasRole = false
        roles.each { UserRole role ->
            if( role.role.authority == Role.ROLE_ADMIN )
                hasRole = true
        }
        return hasRole
    }

    Boolean isUser() {
        Set<UserRole> roles = UserRole.findAllByUser(this)
        boolean hasRole = false
        roles.each { UserRole role ->
            if( role.role.authority == Role.ROLE_USER )
                hasRole = true
        }
        return hasRole
    }


    Boolean isOrgAdmin() {
        Set<UserRole> roles = UserRole.findAllByUser(this)
        boolean hasRole = false
        roles.each { UserRole role ->
            if( role.role.authority == Role.ROLE_ORG_ADMIN )
                hasRole = true
        }
        return hasRole
    }

    Boolean isReviewer() {
        Set<UserRole> roles = UserRole.findAllByUser(this)
        boolean hasRole = false
        roles.each { UserRole role ->
            if( role.role.authority == Role.ROLE_REVIEWER )
                hasRole = true
        }
        return hasRole
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set<Role>
    }

    String toString() {
        return username
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                username: this.username,
                enabled: this.enabled,
                contact: this.contact?.toJsonMap(true),
                admin: this.isAdmin(),
                orgAdmin: this.isOrgAdmin(),
                reviewer: this.isReviewer()
        ]
        return json;
    }//end toJsonMap

}
