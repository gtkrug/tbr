databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1677029597617-1") {
        dropNotNullConstraint(columnDataType: "bigint", columnName: "contact_ref", tableName: "user")
    }

    changeSet(author: "rs239 (generated)", id: "1677029597617-2") {
        dropNotNullConstraint(columnDataType: "varchar(2048)", columnName: "role_array_json", tableName: "user", validate: "true")
    }
}
