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
     * add a registrant
     * @param args
     * args[0]: params.lname
     * args[1]: params.fname
     * args[2]: params.email
     * args[3]: params.phone
     * args[4]: params.pswd
     * args[5]: params.organizationId
     * args[6]: params.roleId
     * @return
     */
    def add(String... args) {
        log.info("add -> ${args[0]}")

        Contact contact = contactService.add(args[0], args[1], args[2], args[3], args[5], "ADMINISTRATIVE")

        User user = userService.add(args[1] + ", " + args[0], args[4], args[2], contact, args[6])

        Organization organization = Organization.get(Integer.parseInt(args[5]))

        Registrant registrant = new Registrant(
                user: user
                , organization: organization
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
            Registrant.withTransaction {
                registrant = Registrant.get(Integer.parseInt(args[0]))
                registrant.user.contact.lastName = args[1]
                registrant.user.contact.firstName = args[2]
                registrant.user.contact.email = args[3]
                registrant.user.contact.phone = args[4]

                // organization
                Organization organization = Organization.get(Integer.parseInt(args[5]))

                if (organization.id != registrant.user.contact.organization.id) {
                    log.info("Organizations are different, UPDATING")
                    registrant.user.contact.organization = organization
                } else {
                    log.info("SAME Organizations")
                }

                // role
                Long roleId = Long.parseLong(args[6])

                log.info("RoleId: ${roleId}: " + args[6])

                // get the role from the user
                Role newRole = Role.findById(roleId)
                log.info("newRole: ${newRole.authority}")

                Role oldRole = registrant.user.getAuthorities()[0]
                log.info("oldRole: ${oldRole.authority}")

                if (!newRole.authority.equals(oldRole.authority)) {
                    log.info("Different ROLES")
                    log.info("Current User/Role: ${registrant.user.id}/${oldRole.id}")

                    UserRole.remove(registrant.user, oldRole)

                    Role role = Role.findById(roleId)
                    UserRole.create(registrant.user, newRole, true)
                }

                //registrant.contact.save(true)
                registrant.save(true)
            }
        } catch (NumberFormatException nfe)  {
            log.error("Invalid Registrant Id ${args[0]}")
        }
        return registrant
    }

    /**
     * update contact info
     * @param args
     * args[0]: params.id
     * args[1]: params.lname
     * args[2]: params.fname
     * args[3]: params.email
     * args[4]: params.phone
     * @return
     */
    def updateContactInfo(String... args) {
        log.info("update -> ${args[0]}  ${args[1]}  ${args[2]}  ${args[3]}  ${args[4]}")
        Registrant registrant = null
        try  {
            Registrant.withTransaction {
                registrant = Registrant.get(Integer.parseInt(args[0]))
                registrant.user.contact.lastName = args[1]
                registrant.user.contact.firstName = args[2]
                registrant.user.contact.email = args[3]
                registrant.user.contact.phone = args[4]

                registrant.save(true)
            }
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

    def roles(String... args) {
        log.info("roles -> ${args[0]}")
        def roles = []

        if(args[0] == "ALL")  {
            Role.findAll().forEach({r -> roles.add(r.toJsonMap())})
            roles.sort( {r1, r2 ->
                r1.name <=> r2.name
            })
        } else {
            Role.findAllByAuthority(args[0]).forEach({r -> roles.add(r.toJsonMap())})
            roles.sort( {r1, r2 ->
                r1.name <=> r2.name
            })
        }
        return roles
    }

    def findByUser(User user)  {
        return Registrant.findByUser(user);
    }
}
