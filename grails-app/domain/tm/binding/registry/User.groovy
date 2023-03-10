package tm.binding.registry

import org.apache.commons.lang.StringUtils
import org.gtri.fj.data.Option
import org.json.JSONArray
import java.util.stream.Collectors

import static org.gtri.fj.data.Option.fromNull

class User {


    String username
    String nameFamily
    String nameGiven
    String contactEmail
    String roleArrayJson

    String name // ???

    Contact contact

    static constraints = {
        username blank: false, unique: true
        name blank: false
        contact nullable: true
        roleArrayJson nullable: true, maxSize: 2048
    }

    static mapping = {
        table name: 'user'
        contact column: 'contact_ref', fetch: 'join'
    }

    static final Option<User> findByUsernameHelper(final String username) {
        fromNull(findByUsername(username))
    }

    User saveAndFlushHelper() {
        User.withTransaction {
            save(flush: true, failOnError: true)
        }
    }

    Boolean isAdmin() {

        if (StringUtils.isNotEmpty(this.roleArrayJson)) {
            JSONArray rolesJsonArray = new JSONArray(this.roleArrayJson);

            return rolesJsonArray.toList()
                    .stream()
                    .filter(role -> Role.fromValue((String) role).isPresent())
                    .map(role -> Role.fromValue((String) role).get())
                    .anyMatch(role -> Role.ROLE_ADMIN == role)
        }

        return false
    }

    Boolean isOrgAdmin() {

        if (StringUtils.isNotEmpty(this.roleArrayJson)) {
            JSONArray rolesJsonArray = new JSONArray(this.roleArrayJson);

            return rolesJsonArray.toList()
                    .stream()
                    .filter(role -> Role.fromValue((String) role).isPresent())
                    .map(role -> Role.fromValue((String) role).get())
                    .anyMatch(role -> Role.ROLE_ORG_ADMIN == role)
        }

        return false
    }

    Boolean hasNoTbrRoles() {

        if (StringUtils.isNotEmpty(this.roleArrayJson)) {
            JSONArray rolesJsonArray = new JSONArray(this.roleArrayJson);
            Set<Role> roles = rolesJsonArray.toList()
                    .stream()
                    .filter(role -> Role.fromValue((String) role).isPresent())
                    .map(role -> Role.fromValue((String) role).get())
                    .collect(Collectors.toSet());

            return roles.isEmpty();
        }

        return true
    }

    Set<Role> getAuthorities() {
        JSONArray rolesJsonArray = new JSONArray(roleArrayJson);

        Set<Role> rolesSet = rolesJsonArray.toList()
                .stream()
                .map(role -> Role.fromValue((String)role).get())
                .collect(Collectors.toSet());

        return rolesSet
    }

    String toString() {
        return username
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                username: this.username,
                nameFamily: this.nameFamily,
                nameGiven: this.nameGiven,
                contactEmail: this.contactEmail,
                contact: this.contact?.toJsonMap(true),
                roles: this.roleArrayJson,
                admin: this.isAdmin(),
                orgAdmin: this.isOrgAdmin()
        ]
        return json;
    }//end toJsonMap

}
