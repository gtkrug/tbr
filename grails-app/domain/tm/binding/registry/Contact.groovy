package tm.binding.registry

import org.apache.commons.lang.StringUtils

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
        lastName nullable: true
        firstName nullable: true
        email nullable: true
        phone nullable: true
        type nullable: true
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
                lastName: StringUtils.isNotEmpty(this.lastName) ? this.lastName : "",
                firstName: StringUtils.isNotEmpty(this.firstName) ? this.firstName : "",
                type: this.type,
                email: StringUtils.isNotEmpty(this.email) ? this.email : "",
                phone: StringUtils.isNotEmpty(this.phone) ? this.phone : "",
                organization: this.organization
        ]
        return json;
    }//end toJsonMap
}
