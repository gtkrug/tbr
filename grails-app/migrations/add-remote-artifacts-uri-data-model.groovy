databaseChangeLog = {

    changeSet(author: "rsa21 (generated)", id: "1650645412863-2") {
        createTable(tableName: "trustmark_assessment_tool_uri") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trustmark_assessment_tool_uriPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "status_success_timestamp", type: "datetime")

            column(name: "query_success_timestamp", type: "datetime")

            column(name: "assessment_repository_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "uri", type: "LONGTEXT")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1651846738152-3") {
        addForeignKeyConstraint(baseColumnNames: "assessment_repository_id", baseTableName: "trustmark_assessment_tool_uri", constraintName: "FK11k8h3om7c73ihaeipjmw6905", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "assessment_repo", validate: "true")
    }

    changeSet(author: "rsa21 (generated)", id: "1651849005275-2") {
        createTable(tableName: "trust_policy_authoring_tool_uri") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trust_policy_authoring_tool_uriPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "status_success_timestamp", type: "datetime")

            column(name: "query_success_timestamp", type: "datetime")

            column(name: "uri", type: "LONGTEXT")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-2") {
        createTable(tableName: "trust_interop_profile_uri") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trust_interop_profile_uriPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "content", type: "LONGTEXT")

            column(name: "hash", type: "LONGTEXT")

            column(name: "uri", type: "LONGTEXT")

            column(name: "retrieval_timestamp", type: "datetime")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-3") {
        createTable(tableName: "trust_interop_profile_uri_trust_interop_profile_uri") {
            column(name: "trust_interop_profile_uri_tip_uris_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trust_interop_profile_uri_id", type: "BIGINT")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-4") {
        createTable(tableName: "trust_interop_profile_uri_trustmark_definition_uri") {
            column(name: "trust_interop_profile_uri_td_uris_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_definition_uri_id", type: "BIGINT")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-7") {
        createTable(tableName: "trustmark_definition_uri") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trustmark_definition_uriPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "content", type: "LONGTEXT")

            column(name: "hash", type: "LONGTEXT")

            column(name: "uri", type: "LONGTEXT")

            column(name: "retrieval_timestamp", type: "datetime")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-8") {
        createTable(tableName: "trustmark_status_report_uri") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trustmark_status_report_uriPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "content", type: "LONGTEXT")

            column(name: "hash", type: "LONGTEXT")

            column(name: "uri", type: "LONGTEXT")

            column(name: "retrieval_timestamp", type: "datetime")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-9") {
        createTable(tableName: "trustmark_uri") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trustmark_uriPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_recipient_identifier_url", type: "LONGTEXT")

            column(name: "assessment_repository_url", type: "LONGTEXT")

            column(name: "content", type: "LONGTEXT")

            column(name: "hash", type: "LONGTEXT")

            column(name: "uri", type: "LONGTEXT")

            column(name: "retrieval_timestamp", type: "datetime")
        }
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-11") {
        addForeignKeyConstraint(baseColumnNames: "trustmark_definition_uri_id", baseTableName: "trust_interop_profile_uri_trustmark_definition_uri", constraintName: "FK84qd23k34e3owtu46vrh3gd12", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trustmark_definition_uri", validate: "true")
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-12") {
        addForeignKeyConstraint(baseColumnNames: "trust_interop_profile_uri_td_uris_id", baseTableName: "trust_interop_profile_uri_trustmark_definition_uri", constraintName: "FKfxsq0wpdkdbf997hp0jkteoib", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trust_interop_profile_uri", validate: "true")
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-13") {
        addForeignKeyConstraint(baseColumnNames: "trust_interop_profile_uri_id", baseTableName: "trust_interop_profile_uri_trust_interop_profile_uri", constraintName: "FKjmkekxk9c96kp70oaso4f2o5p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trust_interop_profile_uri", validate: "true")
    }

    changeSet(author: "rsa21 (generated)", id: "1652232794792-14") {
        addForeignKeyConstraint(baseColumnNames: "trust_interop_profile_uri_tip_uris_id", baseTableName: "trust_interop_profile_uri_trust_interop_profile_uri", constraintName: "FKo8ujmbu1hcswcfp7r52ufq1jq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trust_interop_profile_uri", validate: "true")
    }
}
