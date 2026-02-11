package edu.stanford.protege.robot.command.common;

/**
 * Boolean flags for ROBOT filter and remove commands.
 *
 * <p>
 * These flags control various aspects of axiom filtering and removal behavior, including signature
 * matching, trimming, structure preservation, and punning handling.
 *
 * @see <a href="https://robot.obolibrary.org/filter">ROBOT Filter Documentation</a>
 * @see <a href="https://robot.obolibrary.org/remove">ROBOT Remove Documentation</a>
 */
public enum CommandFlags {

    /**
     * Match named objects only when filtering or removing axioms.
     *
     * <p>
     * Generates {@code --signature true}. When specified, only axioms containing named entities
     * (with IRIs) are considered, excluding anonymous entities.
     */
    SIGNATURE("--signature"),

    /**
     * Match axiom only if ALL objects in the axiom match the target set.
     *
     * <p>
     * Generates {@code --trim false}. When specified, an axiom is only matched if every object in it
     * matches the target set. Useful when filtering or removing imports to preserve dangling
     * references.
     */
    NO_TRIM("--trim"),

    /**
     * Do not maintain hierarchy structure when filtering or removing classes.
     *
     * <p>
     * Generates {@code --preserve-structure false}. When specified, hierarchical connections are not
     * preserved when classes are filtered or removed, potentially creating disconnected subgraphs.
     */
    NO_PRESERVE_STRUCTURE("--preserve-structure"),

    /**
     * Enable handling of punned entities.
     *
     * <p>
     * Generates {@code --allow-punning true}. Punning occurs when the same IRI is used for different
     * entity types (e.g., as both a class and a property). When specified, punned entities are
     * processed instead of being ignored.
     */
    ALLOW_PUNNING("--allow-punning"),
    ;

    private final String flagName;

    CommandFlags(String flagName) {
        this.flagName = flagName;
    }

    /**
     * Returns the ROBOT CLI flag name.
     *
     * @return the flag string (e.g., {@code "--signature"})
     */
    public String getFlagName() {
        return flagName;
    }
}
