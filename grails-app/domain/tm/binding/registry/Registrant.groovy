package tm.binding.registry

class Registrant {

    User         user
    boolean      active

    static belongsTo = [
        organization: Organization
    ]

    static constraints = {
        user nullable: false
    }

    static mapping = {
        table name: 'registrant'
        user column: 'user_ref', fetch: 'join'
        active column: 'active'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                user: this.user?.toJsonMap(true),
                organization: this.organization?.toJsonMap(true),
                active: this.active
        ]
        return json
    }
}
