package tm.binding.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Collects all staleness messages during the binding process
public class RemoteArtifactStaleness {
    List<RemoteArtifactStalenessMessage> trustmarkMessages = new ArrayList<>();
    List<RemoteArtifactStalenessMessage> trustmarkStatusReportMessages = new ArrayList<>();
    List<RemoteArtifactStalenessMessage> trustInteroperabilityProfileMessages = new ArrayList<>();
    List<RemoteArtifactStalenessMessage> trustmarkDefinitionMessages = new ArrayList<>();

    Map<String, RemoteArtifactStalenessMessage> serverMessagesMap = new HashMap<>();

    public RemoteArtifactStaleness() {
    }

    public boolean hasMessages() {
        int messageCount = trustmarkMessages.size() +
                trustmarkStatusReportMessages.size() +
                trustInteroperabilityProfileMessages.size() +
                trustmarkDefinitionMessages.size() +
                serverMessagesMap.size();

        if (messageCount > 0) {
            return true;
        }

        return false;
    }

    public void addTrustmarkStalenessMessage(RemoteArtifactStalenessMessage message) {
        trustmarkMessages.add(message);
    }

    public void addTrustmarkStatusReportnStalenessMessage(RemoteArtifactStalenessMessage message) {
        trustmarkStatusReportMessages.add(message);
    }

    public void addTrustInteroperabilityStalenessMessage(RemoteArtifactStalenessMessage message) {
        trustInteroperabilityProfileMessages.add(message);
    }

    public void addTrustmarkDefinitionStalenessMessage(RemoteArtifactStalenessMessage message) {
        trustmarkDefinitionMessages.add(message);
    }

    public void addServerStalenessMessage(RemoteArtifactStalenessMessage message) {
        if(!serverMessagesMap.containsKey(message.artifactUri)) {
            serverMessagesMap.put(message.artifactUri, message);
        }
    }

    public List<RemoteArtifactStalenessMessage> getServerMessages() {
        List<RemoteArtifactStalenessMessage> messages =
                serverMessagesMap.values()
                                 .stream()
                                 .collect(Collectors.toList());

        return messages;
    }


    List<RemoteArtifactStalenessMessage> getArtifactMessages() {
        List<RemoteArtifactStalenessMessage> messages = new ArrayList<>();

        messages.addAll(trustmarkMessages);
        messages.addAll(trustmarkStatusReportMessages);
        messages.addAll(trustInteroperabilityProfileMessages);
        messages.addAll(trustmarkDefinitionMessages);

        return messages;
    }

}
