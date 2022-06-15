databaseChangeLog = {

    changeSet(author: "rsa21 (generated)", id: "1647266691970-1") {
        modifyDataType(tableName: "binary_data_chunk", columnName:"byte_data", newDataType:"MEDIUMBLOB")
    }
}
