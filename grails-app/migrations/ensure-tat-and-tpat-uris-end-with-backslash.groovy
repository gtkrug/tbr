databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "ensure-tat-uris-end-with-backslash-9778059-1") {

        sql("""UPDATE trustmark_assessment_tool_uri SET uri = CONCAT(uri, '/') WHERE RIGHT(uri, 1) <> '/'""")
    }

    changeSet(author: "rs239 (generated)", id: "ensure-tpat-uris-end-with-backslash-9778059-2") {

        sql("""UPDATE trust_policy_authoring_tool_uri SET uri = CONCAT(uri, '/') WHERE RIGHT(uri, 1) <> '/'""")
    }
}
