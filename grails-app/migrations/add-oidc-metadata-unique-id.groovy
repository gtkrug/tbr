databaseChangeLog = {
    changeSet(author: "rs239 (generated)", id: "1653496674663-3") {
        addColumn(tableName: "provider") {
            column(name: "oidc_unique_id", type: "longtext")
        }
    }
}
