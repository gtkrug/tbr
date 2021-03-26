package tm.binding.registry

class Contact {

    String      firstName
    String      lastName
    String      email
    String      phone
    ContactType type

    static belongsTo = [
        organization: Organization
    ]

    static constraints = {
        lastName nullable: false
        firstName nullable: false
        email nullable: false
        phone nullable: true
        type nullable: false
    }

    static mapping = {
        table name: 'contact'
        firstName column: 'first_name'
        lastName column: 'last_name'
        email column: 'email'
        phone column: 'phone'
        type column: 'type'
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id: this.id,
                lastName: this.lastName,
                firstName: this.firstName,
                type: this.type,
                email: this.email,
                phone: this.phone,
                organization: this.organization
        ]
        return json;
    }//end toJsonMap
}
