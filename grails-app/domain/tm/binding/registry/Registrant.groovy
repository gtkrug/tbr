package tm.binding.registry

class Registrant {

    User         user
    Contact      contact
    boolean      active

    static belongsTo = [
        organization: Organization
    ]

    static constraints = {
        user nullable: false
        contact nullable: false
    }

    static mapping = {
        table name: 'registrant'
        contact column: 'contact_ref', fetch: 'join'
        user column: 'user_ref', fetch: 'join'
        active column: 'active'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                user: this.user?.toJsonMap(true),
                contact: this.contact?.toJsonMap(true),
                organization: this.organization?.toJsonMap(true),
                active: this.active
        ]
        return json
    }
}
