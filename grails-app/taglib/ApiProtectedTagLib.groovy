package tm.binding.registry

import tm.binding.registry.util.TBRProperties

class ApiProtectedTagLib {

    def noClientAuthorizationRequired = {attrs, body ->

        if (!TBRProperties.getIsApiClientAuthorizationRequired()) {
            out << body()
        }
    }
}
