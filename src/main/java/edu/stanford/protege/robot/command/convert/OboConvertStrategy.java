package edu.stanford.protege.robot.command.convert;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.semanticweb.owlapi.model.IRI;

/**
 * OBO format conversion convertStrategy for ROBOT convert command.
 *
 * <p>
 * The OBO format is the legacy text format for ontologies in the Open Biological and Biomedical
 * Ontologies (OBO) community. This convertStrategy provides fine-grained control over OBO format
 * output
 * through document structure validation and cleaning options.
 *
 * <p>
 * Key features:
 *
 * <ul>
 * <li><b>Structure Validation</b> - The {@code check} parameter enforces strict OBO document
 * structure rules (default: true in ROBOT)</li>
 * <li><b>Output Cleaning</b> - The {@code cleanOboOptions} allow dropping extra annotations,
 * merging comments, and removing untranslatable axioms</li>
 * <li><b>Custom Prefixes</b> - The {@code addPrefixes} parameter injects custom namespace prefixes
 * (requires cleanOboOptions to be set)</li>
 * </ul>
 *
 * @param check
 *            enforce OBO document structure rules (optional; defaults to true in ROBOT if null)
 * @param cleanOboOptions
 *            fine-tuning keywords for OBO output (optional; only applies to OBO format)
 * @param addPrefixes
 *            custom namespace prefixes as map of prefix name to IRI (optional; requires
 *            cleanOboOptions)
 */
@JsonTypeName("OBO")
public record OboConvertStrategy(
        @Nullable Boolean check,
        @Nullable List<CleanOboOption> cleanOboOptions,
        @Nullable Map<String, IRI> addPrefixes)
        implements
            ConvertStrategy {

    public static final String FORMAT_FLAG = "--format";
    public static final String CHECK_FLAG = "--check";
    public static final String CLEAN_OBO_FLAG = "--clean-obo";
    public static final String ADD_PREFIX_FLAG = "--add-prefix";

    @Override
    public List<String> getArgs() {
        var args = ImmutableList.<String>builder();

        // Always specify OBO format
        args.add(FORMAT_FLAG);
        args.add(ConvertFormat.obo.name());

        // Add check flag if specified
        if (check != null) {
            args.add(CHECK_FLAG);
            args.add(String.valueOf(check));
        }

        // Add clean-obo options as space-separated keywords
        if (cleanOboOptions != null && !cleanOboOptions.isEmpty()) {
            args.add(CLEAN_OBO_FLAG);
            // Join all keywords with spaces
            var cleanOboKeywords = cleanOboOptions.stream()
                    .map(CleanOboOption::getKeyword)
                    .reduce((a, b) -> a + " " + b)
                    .orElse("");
            args.add(cleanOboKeywords);
        }

        // Add custom prefixes (only works with --clean-obo)
        if (addPrefixes != null && !addPrefixes.isEmpty()) {
            addPrefixes.forEach(
                    (prefix, iri) -> {
                        args.add(ADD_PREFIX_FLAG);
                        args.add(prefix + ": " + iri);
                    });
        }

        return args.build();
    }
}
