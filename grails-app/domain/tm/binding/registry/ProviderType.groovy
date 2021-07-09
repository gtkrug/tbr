package tm.binding.registry

import org.apache.groovy.util.Maps

import java.util.concurrent.ConcurrentHashMap

enum ProviderType  {

   SAML_IDP("SAML IDP"),
   SAML_SP("SAML SP")

   private final String name
   private static final Map<String, ProviderType> ENUM_MAP;

   ProviderType(String name) {
      this.name = name
   }

   public String getName() {
      return this.name
   }

   static {
      Map<String, ProviderType> map = new ConcurrentHashMap<String, ProviderType>();
      for (ProviderType instance : ProviderType.values()) {
         map.put(instance.getName().toLowerCase(), instance);
      }
      ENUM_MAP = Collections.unmodifiableMap(map);
   }

   @Override
   public String toString() {
      return name
   }

   public static ProviderType fromString(String name) {
      return ENUM_MAP.get(name.toLowerCase())
   }
}

