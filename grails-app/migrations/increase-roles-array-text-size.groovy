databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1676826932910-1") {
        modifyDataType(tableName: "user", columnName:"role_array_json", newDataType:"varchar(2048)")
    }
}
