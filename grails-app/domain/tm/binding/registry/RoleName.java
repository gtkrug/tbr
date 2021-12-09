package tm.binding.registry;

public enum RoleName {
    ROLE_ADMIN("TBR Administrator"),
    ROLE_ORG_ADMIN("Organization Administrator");

    private final String name;

    RoleName(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
