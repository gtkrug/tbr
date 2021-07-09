package tm.binding.registry

import java.time.Instant

class Provider {

    int          id
    String       name
    String       entityId
    String       signingCertificate
    String       encryptionCertificate
    String       saml2MetadataXml
    Instant      validUntilDate
    ProviderType providerType
    Date         lastTimeSAMLMetadataGeneratedDate

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
        saml2MetadataXml nullable: true
        validUntilDate nullable: true
        attributes nullable: true
        idpAttributes nullable: true
        protocols nullable: true
        contacts nullable: true
        nameFormats nullable: true
        trustmarks nullable: true
        tags nullable: true
        conformanceTargetTips nullable: true
        lastTimeSAMLMetadataGeneratedDate nullable: true
    }

    static hasMany = [
        endpoints: Endpoint
        , attributes: Attribute
        , idpAttributes: String
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
        saml2MetadataXml column: 'saml2_metadata_xml', type: 'text'
        endpoints cascade: "all-delete-orphan"
        attributes cascade: "all-delete-orphan"
        idpAttributes cascade: "all-delete-orphan"
        trustmarks cascade: "all-delete-orphan"
        conformanceTargetTips cascade: "all-delete-orphan"
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
                providerType : this.providerType.toString(),
                endpoints : this.endpoints,
                trustmarks : this.trustmarks,
                protocols : this.protocols,
                nameFormats : this.nameFormats,
                attributes : this.attributes,
                conformanceTargetTips : this.conformanceTargetTips,
                lastTimeSAMLMetadataGeneratedDate: lastTimeSAMLMetadataGeneratedDate.toString()
        ]
        return json
    }
}
