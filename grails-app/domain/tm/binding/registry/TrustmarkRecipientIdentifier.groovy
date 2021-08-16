package tm.binding.registry

class TrustmarkRecipientIdentifier {

    String trustmarkRecipientIdentifierUrl

    static constraints = {
        trustmarkRecipientIdentifierUrl nullable: false
    }

    static mapping = {
        table name: 'trustmark_recipient_identifier'
        trustmarkRecipientIdentifierUrl column: 'trustmark_recipient_identifier_url'
    }
}
