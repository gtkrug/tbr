package tm.binding.registry

class ConformanceTargetTip {

    String conformanceTargetTipIdentifier
    String name

    static belongsTo = [
        provider: Provider
    ]

    static constraints = {
        conformanceTargetTipIdentifier nullable: false
        name nullable: false
    }

    static mapping = {
        table name: 'conformance_target_tips'
        conformanceTargetTipIdentifier column: 'conformance_target_tip_identifier'
    }
}
