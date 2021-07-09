package tm.binding.registry

import grails.gorm.transactions.Transactional

@Transactional
class ContactService {

    def serviceMethod() {

    }
    def add(String... args) {
        log.info("add -> ${args[0]}")

        Contact contact = new Contact(
                lastName: args[0]
                , firstName: args[1]
                , email: args[2]
                , phone: args[3]
                , type: ContactType.valueOf(args[5])
        )

        contact.organization = Organization.get(Integer.parseInt(args[4]))
        contact.save(true)
        return contact
    }

    /**
     * attempt to find by id first, then email
     * @param args
     * @return
     */
    def get(String... args) {
        log.info("get -> ${args[0]}")

        Contact contact = null

        try  {
            contact = Contact.get(Integer.parseInt(args[0]))
        } catch (NumberFormatException nfe) {
            contact = Contact.findByEmailLike("%" + args[1] + "%")
        }
        return contact
    }

    /**
     * get a contact, used to check for existing contacts
     * @param contact
     * @return
     */
    def get(Contact contact) {
        log.info("get -> ${contact}")

        return Contact.find(contact)
    }

    /**
     * update the contact attributes
     * @param args
     * @return
     */
    def update(String... args) {
        log.info("update -> ${args[0]}")

        Contact contact = null
        try  {
            contact = Contact.get(Integer.parseInt(args[0]))
            contact.lastName = args[1]
            contact.firstName = args[2]
            contact.email = args[3]
            contact.phone = args[4]
            contact.type = ContactType.valueOf(args[5])
            contact.save(true)
        } catch (NumberFormatException nfe) {
            contact = Contact.findByEmailLike("%" + args[0] + "%")
            contact.lastName = args[1]
            contact.firstName = args[2]
            contact.phone = args[3]
            contact.type = ContactType.valueOf(args[5])
            contact.save(true)
        }
        return contact
    }

    def delete(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")
        Contact contact = new Contact()

        // if provider is provided
        if (args[1]) {
            Provider provider = Provider.get(Integer.parseInt(args[1]))
            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        contact = Contact.get(Integer.parseInt(s))
                        provider.removeFromContacts(contact)
                        contact.delete()
                    }
                })
                provider.save(true)
            } catch (NumberFormatException nfe) {
                log.error("Invalid Contact Id!")
            }
        } else {

            try {
                ids.forEach({ s ->
                    if (s.length() > 0) {
                        contact = Contact.get(Integer.parseInt(s))

                        // get all providers that share this contact
                        def providers = Provider.withCriteria(uniqueResult: false) {
                            contacts {
                                inList("id", [contact.id])
                            }
                        }

                        // remove the contact from all providers found in previous search
                        providers.each { provider ->
                            provider.removeFromContacts(contact)
                            provider.save(true)
                        }

                        // now delete contact
                        contact.delete()
                    }
                })

            } catch (NumberFormatException nfe) {
                log.error("Invalid Contact Id!")
            }
        }
        return contact
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")
        def contacts = []

        try  {
            int orgId = Integer.parseInt(args[0])
            if (orgId == 0)  {
                Contact.findAll().forEach({c -> contacts.add(c.toJsonMap())})
            }  else  {
                Contact.findAllByOrganization(Organization.get(orgId)).forEach({c -> contacts.add(c.toJsonMap())})
            }
        } catch (NumberFormatException nfe)  {
            Contact.findAll().forEach({c -> contacts.add(c.toJsonMap())})
        }
        return contacts
    }

    def types() {
        log.info("types")
        def types = ContactType.values()
        return types
    }
}
