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

    PublicOrganization(String name, String displayName, String siteUrl,
                       String description, String trustmarksApiUrl)  {
        this.name = name
        this.displayName = displayName
        this.siteUrl = siteUrl
        this.description = description
        this.trustmarksApiUrl = trustmarksApiUrl
    }
}

