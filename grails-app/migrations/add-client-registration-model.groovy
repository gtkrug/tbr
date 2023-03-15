databaseChangeLog = {

    changeSet(author: "rs239 (generated)", id: "1677529243894-1") {
        createTable(tableName: "oauth2_client_registration") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "oauth2_client_registrationPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "response_failure_message", type: "VARCHAR(1000)")

            column(name: "response_jwk_set_uri", type: "VARCHAR(255)")

            column(name: "request_client_name", type: "VARCHAR(1000)")

            column(name: "response_client_id_issued_at", type: "datetime")

            column(name: "response_client_secret", type: "VARCHAR(1000)")

            column(name: "response_client_id", type: "VARCHAR(1000)")

            column(name: "response_client_secret_expires_at", type: "datetime")

            column(name: "response_token_uri", type: "VARCHAR(255)")

            column(name: "response_registration_client_uri", type: "VARCHAR(1000)")

            column(name: "response_token_endpoint_auth_method", type: "VARCHAR(255)")

            column(name: "response_user_info_uri", type: "VARCHAR(255)")

            column(name: "request_client_uri", type: "VARCHAR(1000)")

            column(name: "response_issuer_uri", type: "VARCHAR(255)")

            column(name: "response_redirect_uri_list", type: "VARCHAR(255)")

            column(name: "response_grant_type_list", type: "VARCHAR(255)")

            column(name: "request_initial_access_token", type: "VARCHAR(1000)") {
                constraints(nullable: "false")
            }

            column(name: "response_registration_access_token", type: "VARCHAR(1000)")

            column(name: "response_scope_list", type: "VARCHAR(255)")
        }
    }
}
