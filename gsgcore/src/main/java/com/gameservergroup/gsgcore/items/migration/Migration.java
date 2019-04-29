package com.gameservergroup.gsgcore.items.migration;

public class Migration {

    private final MigrationType migrationType;
    private final String[] arguments;

    public Migration(MigrationType migrationType, String[] arguments) {
        this.migrationType = migrationType;
        this.arguments = arguments;
    }

    public Migration(MigrationType migrationType, String argument) {
        this(migrationType, new String[]{argument});
    }

    public MigrationType getMigrationType() {
        return migrationType;
    }

    public String getArgument() {
        return arguments[0];
    }

    public String[] getArguments() {
        return arguments;
    }
}
