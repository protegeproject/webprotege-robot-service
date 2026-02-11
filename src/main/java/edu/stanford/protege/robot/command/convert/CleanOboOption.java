package edu.stanford.protege.robot.command.convert;

/**
 * Clean-OBO options for fine-tuning OBO format output.
 *
 * <p>
 * These keywords control how ROBOT processes OWL axioms when converting to OBO format. They
 * determine which elements to drop, merge, or preserve during conversion.
 *
 * <p>
 * Clean-OBO options only apply when the target format is {@link ConvertFormat#obo}. Use multiple
 * options to combine behaviors.
 *
 * @see <a href="https://robot.obolibrary.org/convert#obo-format">ROBOT OBO Format Documentation</a>
 */
public enum CleanOboOption {

    /**
     * Remove additional labels beyond the primary rdfs:label.
     */
    drop_extra_labels("drop-extra-labels"),

    /**
     * Remove additional definitions beyond the primary IAO:0000115 definition.
     */
    drop_extra_definitions("drop-extra-definitions"),

    /**
     * Remove additional comments beyond the primary rdfs:comment.
     */
    drop_extra_comments("drop-extra-comments"),

    /**
     * Merge multiple comments into a single comment annotation.
     */
    merge_comments("merge-comments"),

    /**
     * Remove axioms that cannot be faithfully represented in OBO format.
     */
    drop_untranslatable_axioms("drop-untranslatable-axioms"),

    /**
     * Remove General Class Inclusion (GCI) axioms.
     */
    drop_gci_axioms("drop-gci-axioms"),

    /**
     * Alias for strict OBO conversion.
     *
     * <p>
     * Expands to: drop-extra-labels, drop-extra-definitions, drop-extra-comments,
     * drop-untranslatable-axioms, drop-gci-axioms.
     *
     * <p>
     * Use this for the most conservative OBO output that strictly adheres to format constraints.
     */
    strict("strict"),

    /**
     * Alias for simple OBO conversion.
     *
     * <p>
     * Expands to: drop-extra-labels, drop-extra-definitions, drop-extra-comments,
     * drop-untranslatable-axioms, merge-comments.
     *
     * <p>
     * Use this for a balanced approach that preserves comments while ensuring OBO validity.
     */
    simple("simple"),
    ;

    private final String keyword;

    CleanOboOption(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the ROBOT clean-obo keyword.
     *
     * @return the keyword string (e.g., {@code "drop-extra-labels"})
     */
    public String getKeyword() {
        return keyword;
    }
}
