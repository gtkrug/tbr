package tm.binding.registry

import grails.gorm.transactions.Transactional
import org.gtri.fj.data.List
import org.json.JSONArray
import tm.binding.registry.util.PasswordUtil

@Transactional
class UserService {

    // TODO: Do we need this?
    def serviceMethod() {

    }

    // TODO: This is called by Registrant logic. Review its usefulness.
    def unlock(User user)  {
        log.debug("unlock -> ${user.username}")
        user.save(true)
    }

    def lock(User user)  {
        log.debug("lock -> ${user.username}")
        user.save(true)
    }

//    User.findAll().forEach({r -> registrants.add(r.toJsonMap(false))})

    def list(String... args) {
        log.info("list -> ${args[0]}")
        def users = []
        try  {
            int organizationId = Integer.parseInt(args[0]);
            if(organizationId == 0)  {
                User.findAll().forEach({user -> users.add(user.toJsonMap(false))})
            } else {
                User.findAll().forEach(user -> {
                    if (user.contact != null) {
                        if (user.contact.organization.id == organizationId) {
                            users.add(user.toJsonMap(false))
                        }
                    }
                })
            }
        }  catch (NumberFormatException nfe)  {
            User.findAll().forEach({r -> users.add(r.toJsonMap(false))})
        }
        return users
    }

    /**
     * just updating password for now
     * args[0] => user id
     * args[1] => organization id
     * @param args
     * @return
     */
    def update(String... args) {
        log.info("update -> ${args[0]}  ${args[1]}")
        User user = null
        try  {
            User.withTransaction {
                user = User.get(Integer.parseInt(args[0]))

                // organization
                Organization organization = Organization.get(Integer.parseInt(args[1]))

                if (user.contact == null) {

                    user.contact = new Contact(firstName: user.nameGiven,
                            lastName: user.nameFamily,
                            email: user.contactEmail,
                            type: ContactType.ADMINISTRATIVE,
                            organization: organization)

                } else if (organization.id != user.contact.organization.id) {
                    log.info("Organizations are different, UPDATING")
                    user.contact.organization = organization
                } else {
                    log.info("SAME Organizations")
                }

                user.save(true)
            }
        } catch (NumberFormatException nfe)  {
            log.error("Invalid User Id ${args[0]}")
        }
        return user
    }
}
