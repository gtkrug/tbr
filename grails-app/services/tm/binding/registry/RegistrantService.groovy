package tm.binding.registry

import grails.gorm.transactions.Transactional

@Transactional
class RegistrantService {

    UserService userService

    ContactService contactService

    final String ROLE_USER = 'ROLE_USER'

    /**
     * activate a registant
     * @param args
     * @return
     */
    def activate(String... args) {
        log.info("activate -> ${args[0]}")

        List<String> ids = args[0].split(":")
        Registrant registrant = new Registrant()

        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    registrant = Registrant.get(Integer.parseInt(s))
                    userService.unlock(registrant.user)
                }
            })
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Registrant Id!")
        }

        return registrant
    }

    /**
     * activate a registant
     * @param args
     * @return
     */
    def deactivate(String... args) {
        log.info("deactivate -> ${args[0]}")

        List<String> ids = args[0].split(":")
        Registrant registrant = new Registrant()

        try  {
            ids.forEach({s ->
                if(s.length() > 0) {
                    registrant = Registrant.get(Integer.parseInt(s))
                    userService.lock(registrant.user)
                }
            })
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Registrant Id!")
        }

        return registrant
    }

    /**
     * add a registant
     * @param args
     * @return
     */
    def add(String... args) {
        log.info("add -> ${args[0]}")

        User user = userService.add(args[1] + ", " + args[0], args[4], args[2])

        Organization organization = Organization.get(Integer.parseInt(args[5]))

        Contact contact = contactService.add(args[0], args[1], args[2], args[3], args[5], "ADMINISTRATIVE")

        Registrant registrant = new Registrant(
                user: user
                , organization: organization
                , contact: contact
        )

        registrant.save(true)

        return registrant
    }

    /**
     * get the registrant based on id or email
     * @param args
     * @return
     */
    def get(String... args) {
        log.info("get -> ${args[0]}")
        try  {
            return Registrant.get(Integer.parseInt(args[0]))
        } catch (NumberFormatException nfe)  {
            return Registrant.findByUser(User.findByUsername(args[0]))
        }
    }

    /**
     * just updating password for now
     * @param args
     * @return
     */
    def update(String... args) {
        log.info("update -> ${args[0]}  ${args[1]}  ${args[2]}  ${args[3]}  ${args[4]}")
        Registrant registrant = null
        try  {
            registrant = Registrant.get(Integer.parseInt(args[0]))
            registrant.contact.lastName = args[1]
            registrant.contact.firstName = args[2]
            registrant.contact.email = args[3]
            registrant.contact.phone = args[4]
            registrant.contact.save(true)
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Registrant Id ${args[0]}")
        }
        return registrant
    }

    /**
     * updating password
     * @param args
     * @return
     */
    def pswd(String... args) {
        log.info("update -> ${args[0]}")
        Registrant registrant = null
        try  {
            registrant = Registrant.get(Integer.parseInt(args[0]))
            if (registrant.user.password == args[1])  {
                registrant.user.password = args[2]
                registrant.user.save(true)
            }
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Registrant Id ${args[0]}")
        }
        return registrant
    }

    /**
     * remove registrants based on id
     * @param args
     * @return
     */
    def delete(String... args) {
        log.info("delete -> ${args[0]}")

        List<String> ids = args[0].split(":")
        Registrant registrant = new Registrant()
        try  {
            ids.forEach({s ->
                if(s.length() > 0)  {
                    registrant = Registrant.get(Integer.parseInt(s))
                    userService.lock(registrant.user)
                    registrant.delete()
                }
            })
            return registrant

        } catch (NumberFormatException nfe)  {
            log.error("Invalid Registrant Id!")
        }
    }

    def list(String... args) {
        log.info("list -> ${args[0]}")
        def registrants = []
        try  {
            int organizationId = Integer.parseInt(args[0]);
            if(organizationId == 0)  {
                Registrant.findAll().forEach({r -> registrants.add(r.toJsonMap(false))})
            } else {
              Registrant.findAllByOrganization(Organization.get(organizationId)).forEach({r -> registrants.add(r.toJsonMap(false))})
            }
        }  catch (NumberFormatException nfe)  {
            Registrant.findAll().forEach({r -> registrants.add(r.toJsonMap(false))})
        }
        return registrants
    }

    def findByUser(User user)  {
        return Registrant.findByUser(user);
    }
}
