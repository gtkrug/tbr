databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1635960263293-1") {
        createTable(tableName: "assessment_repo") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "assessment_repoPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "repo_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-2") {
        createTable(tableName: "assessment_repo_trustmark") {
            column(name: "assessment_repository_trustmark_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-3") {
        createTable(tableName: "attribute") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "attributePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "CLOB") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-4") {
        createTable(tableName: "binary_data") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "binary_dataPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "file_system_path", type: "CLOB")

            column(name: "chunk_count", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-5") {
        createTable(tableName: "binary_data_chunk") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "binary_data_chunkPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "byte_data", type: "BLOB") {
                constraints(nullable: "false")
            }

            column(name: "sequence_number", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "binary_data_ref", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-6") {
        createTable(tableName: "binary_object") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "binary_objectPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "file_size", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "datetime")

            column(name: "mime_type", type: "VARCHAR(128)") {
                constraints(nullable: "false")
            }

            column(name: "md5sum", type: "VARCHAR(512)")

            column(name: "original_filename", type: "VARCHAR(256)")

            column(name: "original_extension", type: "VARCHAR(32)")

            column(name: "created_by", type: "VARCHAR(128)")

            column(name: "content_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-7") {
        createTable(tableName: "conformance_target_tips") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "conformance_target_tipsPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "conformance_target_tip_identifier", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "provider_id", type: "INT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-8") {
        createTable(tableName: "contact") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "contactPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "phone", type: "VARCHAR(255)")

            column(name: "first_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "last_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "email", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-9") {
        createTable(tableName: "documents") {
            column(autoIncrement: "true", name: "id", type: "INT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "documentsPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "public_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "date_created", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "binary_object_ref", type: "BIGINT")

            column(name: "public_document", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "file_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "CLOB")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-10") {
        createTable(tableName: "endpoint") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "endpointPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "published", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "provider_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "binding", type: "VARCHAR(255)")

            column(name: "url", type: "VARCHAR(255)")

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-11") {
        createTable(tableName: "endpoint_attribute") {
            column(name: "endpoint_attributes_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "attribute_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-12") {
        createTable(tableName: "mail_parameter") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "mail_parameterPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "last_updated", type: "datetime")

            column(name: "field_name", type: "VARCHAR(254)") {
                constraints(nullable: "false")
            }

            column(name: "field_value", type: "CLOB")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-13") {
        createTable(tableName: "organization") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "organizationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "site_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "CLOB")

            column(name: "display_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-14") {
        createTable(tableName: "organization_trustmark") {
            column(name: "organization_trustmarks_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-15") {
        createTable(tableName: "organization_trustmark_recipient_identifier") {
            column(name: "organization_trustmark_recipient_identifiers_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_recipient_identifier_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-16") {
        createTable(tableName: "provider") {
            column(autoIncrement: "true", name: "id", type: "INT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "providerPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "provider_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "saml2_metadata_xml", type: "CLOB")

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "encrypt_cert", type: "CLOB")

            column(name: "entity_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "valid_until_date", type: "datetime")

            column(name: "saml2_metadata_url", type: "CLOB")

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "last_timesamlmetadata_generated_date", type: "datetime")

            column(name: "sign_cert", type: "CLOB")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-17") {
        createTable(tableName: "provider_attribute") {
            column(name: "provider_attributes_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "attribute_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-18") {
        createTable(tableName: "provider_contact") {
            column(name: "provider_contacts_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "contact_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-19") {
        createTable(tableName: "provider_idp_attributes") {
            column(name: "provider_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "idp_attributes_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-20") {
        createTable(tableName: "provider_name_formats") {
            column(name: "provider_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "name_formats_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-21") {
        createTable(tableName: "provider_protocols") {
            column(name: "provider_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "protocols_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-22") {
        createTable(tableName: "provider_tags") {
            column(name: "provider_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "tags_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-23") {
        createTable(tableName: "provider_trustmark") {
            column(name: "provider_trustmarks_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-24") {
        createTable(tableName: "provider_trustmark_recipient_identifier") {
            column(name: "provider_trustmark_recipient_identifiers_id", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_recipient_identifier_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-25") {
        createTable(tableName: "registrant") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "registrantPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "active", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "user_ref", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-26") {
        createTable(tableName: "role") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "rolePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "authority", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-27") {
        createTable(tableName: "signing_certificates") {
            column(autoIncrement: "true", name: "id", type: "INT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "signing_certificatesPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "state_or_province_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "distinguished_name", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "revoking_user_id", type: "BIGINT")

            column(name: "date_created", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "organization_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "country_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "common_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "thumb_print", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "thumb_print_with_colons", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "revoked_timestamp", type: "datetime")

            column(name: "revoked_reason", type: "CLOB")

            column(name: "key_length", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "valid_period", type: "INT") {
                constraints(nullable: "false")
            }

            column(name: "x509_certificate", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "default_certificate", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "email_address", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "locality_name", type: "VARCHAR(255)")

            column(name: "certificate_public_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "organizational_unit_name", type: "VARCHAR(255)")

            column(name: "filename", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "private_key", type: "CLOB") {
                constraints(nullable: "false")
            }

            column(name: "expiration_date", type: "datetime") {
                constraints(nullable: "false")
            }

            column(name: "serial_number", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-28") {
        createTable(tableName: "tm_assessment_repo") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_assessment_repoPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "repo_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-29") {
        createTable(tableName: "tm_assessment_repo_tm_trustmark") {
            column(name: "tmassessment_repository_trustmark_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "tmtrustmark_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-30") {
        createTable(tableName: "tm_attribute") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_attributePK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "CLOB") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-31") {
        createTable(tableName: "tm_binding_registry") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_binding_registryPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "CLOB")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-32") {
        createTable(tableName: "tm_conformance_target_tips") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_conformance_target_tipsPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "require_compliance", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "conformance_target_tip_identifier", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "provider_id", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-33") {
        createTable(tableName: "tm_contact") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_contactPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "phone", type: "VARCHAR(255)")

            column(name: "first_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)")

            column(name: "last_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "email", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-34") {
        createTable(tableName: "tm_endpoint") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_endpointPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "published", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "url", type: "VARCHAR(255)")

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "type", type: "VARCHAR(255)")

            column(name: "provider_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "binding", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-35") {
        createTable(tableName: "tm_endpoint_tm_attribute") {
            column(name: "tmendpoint_attributes_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "tmattribute_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-36") {
        createTable(tableName: "tm_group") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_groupPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-37") {
        createTable(tableName: "tm_organization") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_organizationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "site_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_provider", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "description", type: "CLOB")

            column(name: "display_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-38") {
        createTable(tableName: "tm_provider") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_providerPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "provider_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "encrypt_cert", type: "CLOB")

            column(name: "entity_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "fully_compliant", type: "BOOLEAN")

            column(name: "sign_cert", type: "CLOB")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-39") {
        createTable(tableName: "tm_provider_name_formats") {
            column(name: "tmprovider_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name_formats_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-40") {
        createTable(tableName: "tm_provider_protocols") {
            column(name: "tmprovider_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "protocols_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-41") {
        createTable(tableName: "tm_provider_tags") {
            column(name: "tmprovider_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "tags_string", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-42") {
        createTable(tableName: "tm_provider_tm_attribute") {
            column(name: "tmprovider_attributes_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "tmattribute_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-43") {
        createTable(tableName: "tm_provider_tm_contact") {
            column(name: "tmprovider_contacts_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "tmcontact_id", type: "BIGINT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-44") {
        createTable(tableName: "tm_registrant") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_registrantPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "organization_id", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "active", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "contact_ref", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-45") {
        createTable(tableName: "tm_trustmark") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "tm_trustmarkPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "assessor_comments", type: "VARCHAR(255)")

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "provisional", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "provider_id", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-46") {
        createTable(tableName: "trustmark") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trustmarkPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_definitionurl", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "assessor_comments", type: "VARCHAR(255)")

            column(name: "url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "status", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "provisional", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "conformance_target_tip_id", type: "INT")
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-47") {
        createTable(tableName: "trustmark_recipient_identifier") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "trustmark_recipient_identifierPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "trustmark_recipient_identifier_url", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-48") {
        createTable(tableName: "user") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "userPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "password_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "account_expired", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "contact_ref", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "username", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "account_locked", type: "BOOLEAN") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pass_hash", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "enabled", type: "BOOLEAN") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-49") {
        createTable(tableName: "user_role") {
            column(name: "role_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "user_rolePK")
            }

            column(name: "user_id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "user_rolePK")
            }
        }
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-50") {
        addUniqueConstraint(columnNames: "authority", constraintName: "UC_ROLEAUTHORITY_COL", tableName: "role")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-51") {
        addUniqueConstraint(columnNames: "username", constraintName: "UC_USERUSERNAME_COL", tableName: "user")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-52") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "contact", constraintName: "FK1krf34n4ihu4xj6ayg9yh78l9", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-53") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "tm_registrant", constraintName: "FK2laa97y1f29nwo4j2o93pa200", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-54") {
        addForeignKeyConstraint(baseColumnNames: "organization_trustmarks_id", baseTableName: "organization_trustmark", constraintName: "FK332s8ddch8jc6f1bvejm1sciq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-55") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "tm_endpoint", constraintName: "FK3gc9rpkmb6jo1iqv7xpp8q6lk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-56") {
        addForeignKeyConstraint(baseColumnNames: "binary_object_ref", baseTableName: "documents", constraintName: "FK42xbhejhth1gbgixk6c1eiu3t", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "binary_object", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-57") {
        addForeignKeyConstraint(baseColumnNames: "tmattribute_id", baseTableName: "tm_provider_tm_attribute", constraintName: "FK47p0s4pg76ycclps3moktk898", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_attribute", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-58") {
        addForeignKeyConstraint(baseColumnNames: "trustmark_recipient_identifier_id", baseTableName: "provider_trustmark_recipient_identifier", constraintName: "FK4oxrws6iot8lycekobw1wn53e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trustmark_recipient_identifier", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-59") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "tm_provider", constraintName: "FK6m03f81g84mqe8unmwrso2ur4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-60") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "provider", constraintName: "FK6s1bta81qmiuvhf7ibg9sms55", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-61") {
        addForeignKeyConstraint(baseColumnNames: "trustmark_id", baseTableName: "provider_trustmark", constraintName: "FK7eimqj6hqjdy3sspg08lpea9p", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trustmark", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-62") {
        addForeignKeyConstraint(baseColumnNames: "assessment_repository_trustmark_id", baseTableName: "assessment_repo_trustmark", constraintName: "FK7p79514a64w7qwr67k23g7uxf", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "assessment_repo", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-63") {
        addForeignKeyConstraint(baseColumnNames: "user_id", baseTableName: "user_role", constraintName: "FK859n2jvi8ivhui0rl0esws6o", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-64") {
        addForeignKeyConstraint(baseColumnNames: "provider_trustmark_recipient_identifiers_id", baseTableName: "provider_trustmark_recipient_identifier", constraintName: "FK8i107ipsxpd01unjgbl2xb1v7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-65") {
        addForeignKeyConstraint(baseColumnNames: "tmprovider_id", baseTableName: "tm_provider_protocols", constraintName: "FK8lowci9h5273c1mwbkbns1xdj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-66") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "provider_tags", constraintName: "FK8oaik8s5tvxwrrrmlseuno0p0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-67") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "provider_idp_attributes", constraintName: "FK8qgas4naqxcd6ws512w82uc2i", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-68") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "conformance_target_tips", constraintName: "FKa3c1rb3jrwqgtyqhxbi2imy7m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-69") {
        addForeignKeyConstraint(baseColumnNames: "role_id", baseTableName: "user_role", constraintName: "FKa68196081fvovjhkek5m97n3y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "role", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-70") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "provider_protocols", constraintName: "FKafxxob85vd7fiaki902w6fvd5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-71") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "assessment_repo", constraintName: "FKak4xasx4ndargk1drts0s1t7r", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-72") {
        addForeignKeyConstraint(baseColumnNames: "tmendpoint_attributes_id", baseTableName: "tm_endpoint_tm_attribute", constraintName: "FKbb099gokd0cn5072t4j58tvd4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_endpoint", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-73") {
        addForeignKeyConstraint(baseColumnNames: "binary_data_ref", baseTableName: "binary_data_chunk", constraintName: "FKbn1jc71b2ht8myiyus1y7mfx7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "binary_data", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-74") {
        addForeignKeyConstraint(baseColumnNames: "tmcontact_id", baseTableName: "tm_provider_tm_contact", constraintName: "FKbrqbrlxprhkw4gc891yqfy7k0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_contact", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-75") {
        addForeignKeyConstraint(baseColumnNames: "tmprovider_contacts_id", baseTableName: "tm_provider_tm_contact", constraintName: "FKbta5s0n13p3ewlnc16es96aud", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-76") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "tm_assessment_repo", constraintName: "FKcho7h85muvoxs9r6pyvjjbmkx", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-77") {
        addForeignKeyConstraint(baseColumnNames: "tmtrustmark_id", baseTableName: "tm_assessment_repo_tm_trustmark", constraintName: "FKdjfu01f09594e5wgpsxpw4nyn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_trustmark", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-78") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "registrant", constraintName: "FKdu78kwq9c6ywx5xv9xn2s188d", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-79") {
        addForeignKeyConstraint(baseColumnNames: "user_ref", baseTableName: "registrant", constraintName: "FKe5rycpgejyl5gshg5deqkxo87", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-80") {
        addForeignKeyConstraint(baseColumnNames: "tmprovider_id", baseTableName: "tm_provider_name_formats", constraintName: "FKen80dmbmg6ejbkny4so91mpeh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-81") {
        addForeignKeyConstraint(baseColumnNames: "tmattribute_id", baseTableName: "tm_endpoint_tm_attribute", constraintName: "FKf2n7ecaagg1f5ebojjnh55ba3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_attribute", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-82") {
        addForeignKeyConstraint(baseColumnNames: "provider_attributes_id", baseTableName: "provider_attribute", constraintName: "FKfrf9dams4bqhxkfx9huvj8bh8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-83") {
        addForeignKeyConstraint(baseColumnNames: "content_id", baseTableName: "binary_object", constraintName: "FKgyfpgt7icaigynpbus0ooyb7y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "binary_data", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-84") {
        addForeignKeyConstraint(baseColumnNames: "attribute_id", baseTableName: "endpoint_attribute", constraintName: "FKh4b1tyjog9t8hak1ba2muqo8l", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "attribute", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-85") {
        addForeignKeyConstraint(baseColumnNames: "provider_trustmarks_id", baseTableName: "provider_trustmark", constraintName: "FKhflo2vwnev3kkvtg3gkbromx4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-86") {
        addForeignKeyConstraint(baseColumnNames: "tmprovider_attributes_id", baseTableName: "tm_provider_tm_attribute", constraintName: "FKhqqpp9181eqgqe53prtqihjxy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-87") {
        addForeignKeyConstraint(baseColumnNames: "endpoint_attributes_id", baseTableName: "endpoint_attribute", constraintName: "FKi4gdy366dmlyd20nuca80c40e", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "endpoint", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-88") {
        addForeignKeyConstraint(baseColumnNames: "contact_id", baseTableName: "provider_contact", constraintName: "FKj5g3lyvpgc7wh0q9401r7p46n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "contact", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-89") {
        addForeignKeyConstraint(baseColumnNames: "trustmark_id", baseTableName: "organization_trustmark", constraintName: "FKjumk58g444dhjghf84a3r4eyl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trustmark", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-90") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "tm_trustmark", constraintName: "FKksp4iifllevjyvr0kmsb68srt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-91") {
        addForeignKeyConstraint(baseColumnNames: "tmprovider_id", baseTableName: "tm_provider_tags", constraintName: "FKll4dx9q5m784cf8ttkoraee04", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-92") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "provider_name_formats", constraintName: "FKln3qnt3duf7gqco8epieotq26", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-93") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "tm_binding_registry", constraintName: "FKlwl3kkuhounevi982kq7d8ehb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-94") {
        addForeignKeyConstraint(baseColumnNames: "revoking_user_id", baseTableName: "signing_certificates", constraintName: "FKmlyoptsb28slkf8v8hmb0vxn1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "user", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-95") {
        addForeignKeyConstraint(baseColumnNames: "tmassessment_repository_trustmark_id", baseTableName: "tm_assessment_repo_tm_trustmark", constraintName: "FKmsy8q852p0jdvtx6jjtuftqq5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_assessment_repo", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-96") {
        addForeignKeyConstraint(baseColumnNames: "trustmark_id", baseTableName: "assessment_repo_trustmark", constraintName: "FKmxew1014yfcba5r5nr6ttaks4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trustmark", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-97") {
        addForeignKeyConstraint(baseColumnNames: "organization_id", baseTableName: "tm_contact", constraintName: "FKn7ttrytp8ro3r45b3q437yt0i", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-98") {
        addForeignKeyConstraint(baseColumnNames: "contact_ref", baseTableName: "user", constraintName: "FKn85wtl6auk7u3pfor3brkn114", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "contact", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-99") {
        addForeignKeyConstraint(baseColumnNames: "organization_trustmark_recipient_identifiers_id", baseTableName: "organization_trustmark_recipient_identifier", constraintName: "FKo67r83h71736go59jx56ho0g1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "organization", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-100") {
        addForeignKeyConstraint(baseColumnNames: "attribute_id", baseTableName: "provider_attribute", constraintName: "FKoir9l0vk4xp9roepaqjojmoce", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "attribute", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-101") {
        addForeignKeyConstraint(baseColumnNames: "contact_ref", baseTableName: "tm_registrant", constraintName: "FKoww3i123q29mj0xx40uvvracm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_contact", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-102") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "tm_conformance_target_tips", constraintName: "FKoxtrima47h9sg1srlhdrfao6y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tm_provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-103") {
        addForeignKeyConstraint(baseColumnNames: "provider_contacts_id", baseTableName: "provider_contact", constraintName: "FKqhtddgiik6q523c14pkd6yk64", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-104") {
        addForeignKeyConstraint(baseColumnNames: "provider_id", baseTableName: "endpoint", constraintName: "FKre71t9bwk89j0iiugiypg8rl1", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "provider", validate: "true")
    }

    changeSet(author: "rs239 (generated)", id: "1635960263293-105") {
        addForeignKeyConstraint(baseColumnNames: "trustmark_recipient_identifier_id", baseTableName: "organization_trustmark_recipient_identifier", constraintName: "FKtquh6o8u8l5d4br6qmy95oe1x", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "trustmark_recipient_identifier", validate: "true")
    }
}
