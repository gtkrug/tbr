databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1638818378060-1") {
        dropForeignKeyConstraint(baseTableName: "password_reset_token", constraintName: "FK5lwtbncug84d4ero33v3cfxvl")
    }

    changeSet(author: "rs239 (generated)", id: "1638818378060-2") {
        dropTable(tableName: "password_reset_token")
    }

    changeSet(author: "rs239 (generated)", id: "1638818378060-3") {

        sql("""DELETE FROM user""")
    }
}
