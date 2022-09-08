databaseChangeLog = {
    include file: 'tbr-1.1-baseline.groovy'
    include file: 'add-partner-systems-tips-to-organizations-and-systems.groovy'
    include file: 'add-password-reset-token.groovy'
    include file: 'remove-unused-roles.groovy'
    include file: 'add-system-certificate.groovy'
    include file: 'add-system-certificate-filename-and-url.groovy'
    include file: 'incorrect-blob-type.groovy'
    include file: 'add-oidc-metadata.groovy'
    include file: 'add-oidc-metadata-url.groovy'
    include file: 'add-oidc-metadata-unique-id.groovy'
    include file: 'add-remote-artifacts-uri-data-model.groovy'
    include file: 'remove-orphan-records-from-provider_contact.groovy'
}