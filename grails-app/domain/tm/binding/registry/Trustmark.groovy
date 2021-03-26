package tm.binding.registry

class Trustmark {

    String name
    String url
    String status
    boolean provisional
    String assessorComments

    static belongsTo = [
        provider: Provider
    ]

    static constraints = {
        name nullable: false
        url nullable: false
        status nullable: false
        provisional nullable: false
        assessorComments nullable: true
    }

    static mapping = {
        table name: 'trustmark'
    }
}
