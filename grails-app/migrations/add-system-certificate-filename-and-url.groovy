databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1646751756960-1") {
        addColumn(tableName: "provider") {
            column(name: "system_cert_filename", type: "longtext")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1646751756960-2") {
        addColumn(tableName: "provider") {
            column(name: "system_cert_url", type: "longtext")
        }
    }
}
