package tm.binding.registry

class Provider {

    String       name
    String       entityId
    String       signingCertificate
    String       encryptionCertificate
    ProviderType providerType

    static belongsTo = [
        organization: Organization
    ]

    static constraints = {
        name nullable: false
        providerType nullable: false
        entityId nullable: false
        endpoints nullable: true
        signingCertificate nullable: true
        encryptionCertificate nullable: true
        attributes nullable: true
        protocols nullable: true
        contacts nullable: true
        nameFormats nullable: true
        trustmarks nullable: true
        tags nullable: true
        conformanceTargetTips nullable: true
    }

    static hasMany = [
        endpoints: Endpoint
        , attributes: Attribute
        , protocols: String
        , contacts: Contact
        , nameFormats: String
        , trustmarks: Trustmark
        , tags: String
        , conformanceTargetTips: ConformanceTargetTip
    ]

    static mapping = {
        table name: 'provider'
        entityId column: 'entity_id'
        providerType column: 'provider_type'
        signingCertificate column: 'sign_cert', type: 'text'
        encryptionCertificate column: 'encrypt_cert', type: 'text'
    }

    def findAttribute(String name)  {
        String value = null
        attributes.forEach({ a ->
            if(a.name == name)
                value = a.value
        })
        return value
    }

    Map toJsonMap(boolean shallow = true) {
        def json = [
                id : this.id,
                name : this.name,
                entityId : this.entityId,
                organization: this.organization,
                signingCertificate : this.signingCertificate,
                encryptionCertificate : this.encryptionCertificate,
                providerType : this.providerType,
                endpoints : this.endpoints,
                trustmarks : this.trustmarks,
                protocols : this.protocols,
                nameFormats : this.nameFormats,
                attributes : this.attributes,
                conformanceTargetTips : this.conformanceTargetTips
        ]
        return json
    }
}
