databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1638818378058-1") {
        createTable(tableName: "password_reset_token") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "password_reset_tokenPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "expire_date_time", type: "datetime")

            column(name: "user_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "token", type: "VARCHAR(36)")

            column(name: "request_date_time", type: "datetime")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1638818378058-2") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "password_reset_token", constraintName: "FK5lwtbncug84d4ero33v3cfxvl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", validate: "true")
    }
}
