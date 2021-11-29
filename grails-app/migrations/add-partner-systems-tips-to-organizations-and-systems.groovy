databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1636303261020-39") {
        createTable(tableName: "organization_partner_system_tips") {
            column(name: "organization_partner_systems_tips_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "partner_systems_tip_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-40") {
        createTable(tableName: "partner_system_tips") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "partner_system_tipsPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "partner_system_tip_identifier", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-41") {
        createTable(tableName: "provider_partner_system_tips") {
            column(name: "provider_partner_systems_tips_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "partner_systems_tip_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-42") {
        addForeignKeyConstraint(baseColumnNames: "partner_systems_tip_id", baseTableName: "provider_partner_system_tips", constraintName: "FK2tv72xbe8fy5dx4fwja3odhwf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "partner_system_tips", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-43") {
        addForeignKeyConstraint(baseColumnNames: "provider_partner_systems_tips_id", baseTableName: "provider_partner_system_tips", constraintName: "FK5ge77yd7jeh1iq2744vdndmc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-44") {
        addForeignKeyConstraint(baseColumnNames: "partner_systems_tip_id", baseTableName: "organization_partner_system_tips", constraintName: "FKbn5d12mn2ajvddug5uq7q191a", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "partner_system_tips", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-45") {
        addForeignKeyConstraint(baseColumnNames: "organization_partner_systems_tips_id", baseTableName: "organization_partner_system_tips", constraintName: "FKme54vmqvia4d1diljprw2b6r3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-1") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "assessor_comments", tableName: "tm_trustmark")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-2") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "assessor_comments", tableName: "trustmark")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-3") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "binding", tableName: "endpoint")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-4") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "binding", tableName: "tm_endpoint")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-5") {
        dropDefaultValue(columnDataType: "varchar(128)", columnName: "created_by", tableName: "binary_object")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-6") {
        dropDefaultValue(columnDataType: "clob", columnName: "description", tableName: "documents")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-7") {
        dropDefaultValue(columnDataType: "clob", columnName: "description", tableName: "organization")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-8") {
        dropDefaultValue(columnDataType: "clob", columnName: "description", tableName: "tm_binding_registry")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-9") {
        dropDefaultValue(columnDataType: "clob", columnName: "description", tableName: "tm_organization")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-10") {
        dropDefaultValue(columnDataType: "clob", columnName: "encrypt_cert", tableName: "provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-11") {
        dropDefaultValue(columnDataType: "clob", columnName: "encrypt_cert", tableName: "tm_provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-12") {
        dropDefaultValue(columnDataType: "clob", columnName: "field_value", tableName: "mail_parameter")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-13") {
        dropDefaultValue(columnDataType: "clob", columnName: "file_system_path", tableName: "binary_data")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-14") {
        dropDefaultValue(columnDataType: "boolean", columnName: "fully_compliant", tableName: "tm_provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-15") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "idp_attributes_string", tableName: "provider_idp_attributes")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-16") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "locality_name", tableName: "signing_certificates")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-17") {
        dropDefaultValue(columnDataType: "varchar(512)", columnName: "md5sum", tableName: "binary_object")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-18") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "name_formats_string", tableName: "provider_name_formats")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-19") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "name_formats_string", tableName: "tm_provider_name_formats")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-20") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "organizational_unit_name", tableName: "signing_certificates")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-21") {
        dropDefaultValue(columnDataType: "varchar(32)", columnName: "original_extension", tableName: "binary_object")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-22") {
        dropDefaultValue(columnDataType: "varchar(256)", columnName: "original_filename", tableName: "binary_object")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-23") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "phone", tableName: "contact")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-24") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "phone", tableName: "tm_contact")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-25") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "protocols_string", tableName: "provider_protocols")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-26") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "protocols_string", tableName: "tm_provider_protocols")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-27") {
        dropDefaultValue(columnDataType: "clob", columnName: "revoked_reason", tableName: "signing_certificates")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-28") {
        dropDefaultValue(columnDataType: "clob", columnName: "saml2_metadata_url", tableName: "provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-29") {
        dropDefaultValue(columnDataType: "clob", columnName: "saml2_metadata_xml", tableName: "provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-30") {
        dropDefaultValue(columnDataType: "clob", columnName: "sign_cert", tableName: "provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-31") {
        dropDefaultValue(columnDataType: "clob", columnName: "sign_cert", tableName: "tm_provider")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-32") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "tags_string", tableName: "provider_tags")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-33") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "tags_string", tableName: "tm_provider_tags")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-34") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "type", tableName: "endpoint")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-35") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "type", tableName: "tm_contact")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-36") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "type", tableName: "tm_endpoint")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-37") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "url", tableName: "endpoint")
    }

    changeSet(author: "rs239 (generated)", id: "1636303261020-38") {
        dropDefaultValue(columnDataType: "varchar(255)", columnName: "url", tableName: "tm_endpoint")
    }
}
