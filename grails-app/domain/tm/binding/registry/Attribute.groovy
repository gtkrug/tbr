package tm.binding.registry

class Attribute {

    String name
    String value

    static constraints = {
        name nullable: false
        value nullable: false
    }

    static mapping = {
        table name: 'attribute'
        name column: 'name'
        value type: 'text'
    }
}
