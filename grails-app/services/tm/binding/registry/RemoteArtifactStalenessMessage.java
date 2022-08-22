package tm.binding.registry;

public class RemoteArtifactStalenessMessage {
    String artifactName;
    String stalenessMessage;
    String artifactUri;

    public RemoteArtifactStalenessMessage(String artifactName,
                                          String stalenessMessage,
                                          String artifactUri) {
        this.artifactName = artifactName;
        this.stalenessMessage = stalenessMessage;
        this.artifactUri = artifactUri;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public String getArtifactUri() {
        return artifactUri;
    }

    public String getStalenessMessage() {
        return stalenessMessage;
    }
}
