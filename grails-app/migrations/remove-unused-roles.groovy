databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1638818378059-1") {

        sql("""DELETE FROM user_role WHERE user_role.role_id = 
            (SELECT id FROM role WHERE role.authority = 'ROLE_USER')""")
    }

    changeSet(author: "rs239 (generated)", id: "1638818378059-2") {

        sql("""DELETE FROM role WHERE role.authority = 'ROLE_USER'""")
    }

    changeSet(author: "rs239 (generated)", id: "1638818378059-3") {

        sql("""DELETE FROM user_role WHERE user_role.role_id = 
            (SELECT id FROM role WHERE role.authority = 'ROLE_REVIEWER')""")
    }

    changeSet(author: "rs239 (generated)", id: "1638818378059-4") {

        sql("""DELETE FROM role WHERE role.authority = 'ROLE_REVIEWER'""")
    }
}
