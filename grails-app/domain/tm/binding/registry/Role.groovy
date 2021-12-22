package tm.binding.registry

class Role {

    static String ROLE_ORG_ADMIN = "ROLE_ORG_ADMIN"
    static String ROLE_ADMIN = "ROLE_ADMIN"

    static List<String> ALL_ROLES = [ROLE_ADMIN, ROLE_ORG_ADMIN]

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

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                name: this.authority,
                label: RoleName.valueOf(this.authority).getName()
        ]

        return json;
    }
}
