package tm.binding.registry

class Trustmark {

    String name
    String url
    String trustmarkDefinitionURL
    String status
    boolean provisional
    String assessorComments
    Integer conformanceTargetTipId

    static belongsTo = [
        provider: Provider
    ]

    static constraints = {
        name nullable: false
        url nullable: false
        trustmarkDefinitionURL nullable: false
        status nullable: false
        provisional nullable: false
        assessorComments nullable: true
    }

    static mapping = {
        table name: 'trustmark'
    }
}
