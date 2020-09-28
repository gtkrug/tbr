package tm.binding.registry

class Role {

    static String ROLE_REVIEWER = "ROLE_REVIEWER"
    static String ROLE_USER = "ROLE_USER"
    static String ROLE_ORG_ADMIN = "ROLE_ORG_ADMIN"
    static String ROLE_ADMIN = "ROLE_ADMIN"

    static List<String> ALL_ROLES = [ROLE_REVIEWER, ROLE_USER, ROLE_ADMIN, ROLE_ORG_ADMIN]

    Role(){}
    Role(String authority) {
        this()
        this.authority = authority
    }

    String authority

    static mapping = {
        cache true
    }

    static constraints = {
        authority blank: false, unique: true
    }
}
