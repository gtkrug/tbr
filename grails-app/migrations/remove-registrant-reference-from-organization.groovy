databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1677517114028-1") {
        dropForeignKeyConstraint(baseTableName: "registrant", constraintName: "FKdu78kwq9c6ywx5xv9xn2s188d")
    }

    changeSet(author: "rs239 (generated)", id: "1677517114028-2") {
        dropForeignKeyConstraint(baseTableName: "registrant", constraintName: "FKe5rycpgejyl5gshg5deqkxo87")
    }

    changeSet(author: "rs239 (generated)", id: "1677517114028-3") {
        dropTable(tableName: "registrant")
    }
}
