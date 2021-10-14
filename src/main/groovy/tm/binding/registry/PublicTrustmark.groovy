package tm.binding.registry

/**
 * POJO class to container just the info we want to put on the wire
 */
class PublicTrustmark {
    String name
    String url
    String trustmarkDefinitionURL
    String status
    boolean provisional
    String assessorComments

    PublicTrustmark(String name, String url, String trustmarkDefinitionURL,
                    String status, boolean provisional, String assessorComments)  {
        this.name = name
        this.url = url
        this.trustmarkDefinitionURL = trustmarkDefinitionURL
        this.status = status
        this.provisional = provisional
        this.assessorComments = assessorComments
    }
}

