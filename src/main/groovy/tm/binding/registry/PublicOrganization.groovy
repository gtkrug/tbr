package tm.binding.registry

/**
 * POJO class to container just the info we want to put on the wire
 */
class PublicOrganization {
    String name
    String displayName
    String siteUrl
    String description
    String trustmarksApiUrl
    List<String> trustmarkRecipientIdentifiers = new ArrayList<String>()

    PublicOrganization(String name, String displayName, String siteUrl,
                       String description, String trustmarksApiUrl,
                       List<String> trustmarkRecipientIdentifiers)  {
        this.name = name
        this.displayName = displayName
        this.siteUrl = siteUrl
        this.description = description
        this.trustmarksApiUrl = trustmarksApiUrl

        trustmarkRecipientIdentifiers.each { tri ->
            this.trustmarkRecipientIdentifiers.add(tri)
        }
    }
}

