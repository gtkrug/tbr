package tm.binding.registry

class PublicSigningCertificate {
    String url
    boolean active

    PublicSigningCertificate(String url, boolean active)  {
        this.url = url
        this.active = active
    }
}