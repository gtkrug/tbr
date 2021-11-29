package tm.binding.registry

class PartnerSystemsTip {

    String partnerSystemsTipIdentifier
    String name

    static constraints = {
        partnerSystemsTipIdentifier nullable: false
        name nullable: false
    }

    static mapping = {
        table name: 'partner_system_tips'
        partnerSystemsTipIdentifier column: 'partner_system_tip_identifier'
    }
}
