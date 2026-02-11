package edu.stanford.protege.robot.command.export;

/**
 * Entity selection options for ROBOT export command.
 *
 * <p>
 * Controls which types of entities are included in the export based on their anonymity status.
 */
public enum EntitySelect {

    /**
     * Include all entities (named and anonymous). Default selection that exports both named entities
     * and anonymous expressions.
     */
    ANY,

    /**
     * Include only named entities. Filters out anonymous expressions, exporting only entities with
     * explicit IRIs.
     */
    NAMED,

    /**
     * Include only anonymous entities. Exports only anonymous expressions, excluding named entities
     * with IRIs.
     */
    ANON,

    /**
     * Alias for ANON - include only anonymous entities. Alternative spelling for anonymous entity
     * selection.
     */
    ANONYMOUS,
    ;
}
