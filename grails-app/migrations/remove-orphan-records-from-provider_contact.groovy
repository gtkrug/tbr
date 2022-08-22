databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "remove-orphan-records-from-provider-contact-8778059-1") {

        sql("""DELETE binding_registry.provider_contact FROM binding_registry.provider_contact 
                inner join binding_registry.provider on provider.id = provider_contact.provider_contacts_id
                inner join contact on contact_id = contact.id
                where provider.organization_id != contact.organization_id""")
    }
}
