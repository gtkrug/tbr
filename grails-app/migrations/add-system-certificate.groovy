databaseChangeLog = {

    changeSet(author: "rsa21 (generated)", id: "1646240547064-1") {
        addColumn(tableName: "provider") {
            column(name: "system_cert", type: "longtext")
        }
    }
}
