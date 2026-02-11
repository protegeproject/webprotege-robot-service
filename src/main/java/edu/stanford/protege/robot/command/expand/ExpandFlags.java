package edu.stanford.protege.robot.command.expand;

/**
 * Boolean flags for ROBOT expand command.
 */
public enum ExpandFlags {

    /**
     * This flag enables annotating expansion axioms with provenance information.
     */
    ANNOTATE_EXPANSION_AXIOMS("--annotate-expansion-axioms"),
    ;

    private final String flagName;

    ExpandFlags(String flagName) {
        this.flagName = flagName;
    }

    /**
     * Returns the ROBOT CLI flag.
     *
     * @return the flag string.
     */
    public String getFlagName() {
        return flagName;
    }
}
