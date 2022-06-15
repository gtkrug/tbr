databaseChangeLog = {
    changeSet(author: "rs239 (generated)", id: "1653493424670-2") {
        addColumn(tableName: "provider") {
            column(name: "oidc_metadata_url", type: "longtext")
        }
    }
}
