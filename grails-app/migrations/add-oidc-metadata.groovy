databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1649191275948-2") {
        addColumn(tableName: "provider") {
            column(name: "openid_connect_metadata", type: "longtext")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1649191275948-4") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "first_name", tableName: "contact")
    }

    changeSet(author: "rsa21 (generated)", id: "1649191275948-5") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "last_name", tableName: "contact")
    }

    changeSet(author: "rsa21 (generated)", id: "1649191275948-6") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "type", tableName: "contact")
    }

    changeSet(author: "rsa21 (generated)", id: "1649191275948-7") {
        dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "email", tableName: "contact")
    }
}
