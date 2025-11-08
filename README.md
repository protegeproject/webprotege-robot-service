# WebProtégé ROBOT Service

Spring Boot microservice that wraps [ROBOT](https://robot.obolibrary.org/) (an OBO ontology tool) for WebProtégé integration. Provides Java abstractions for programmatic ontology processing with support for command chaining.

## Supported ROBOT Commands

| Command | Status | Description |
|---------|--------|-------------|
| **Annotate** | ✅ Implemented | Add metadata to ontologies (title, description, license, version IRI) |
| **Extract** | ✅ Implemented | Create focused modules using SLME, MIREOT, or Subset methods |
| **Collapse** | ✅ Implemented | Streamline class hierarchies by removing intermediate classes |
| **Convert** | ✅ Implemented | Transform ontologies between formats (JSON, OBO, OWL, Turtle, etc.) |
| **Expand** | ✅ Implemented | Convert shortcut annotation properties (macros) into OWL axioms |
| **Remove** | ✅ Implemented | Eliminate selected axioms from ontologies |
| **Diff** | 🚧 Not Yet Implemented | Compare ontology versions |
| **Export** | 🚧 Not Yet Implemented | Export ontology in various formats |
| **Filter** | 🚧 Not Yet Implemented | Remove axioms/terms based on criteria |
| **Materialize** | 🚧 Not Yet Implemented | Materialize class expressions |
| **Measure** | 🚧 Not Yet Implemented | Compute ontology metrics |
| **Merge** | 🚧 Not Yet Implemented | Combine multiple ontologies |
| **Reduce** | 🚧 Not Yet Implemented | Remove redundant axioms |
| **Relax** | 🚧 Not Yet Implemented | Convert strict axioms to approximate equivalents |
| **Repair** | 🚧 Not Yet Implemented | Fix common ontology issues |
| **Unmerge** | 🚧 Not Yet Implemented | Reverse a merge operation |

## Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.8+

### Installation

```bash
git clone https://github.com/protegeproject/webprotege-robot-service.git
cd webprotege-robot-service
mvn clean install
```

### Usage Example

```java
@Service
public class OntologyProcessingService {

  @Autowired
  private RobotCommandExecutor executor;

  public void processOntology() throws Exception {
    // Prepare ROBOT commands
    var annotateCmd = ...
    var extractCmd = ...

    // Execute chain: annotate then extract
    var result = executor.executeChain(
        Path.of("input.owl"),
        List.of(annotateCmd, extractCmd),
        Path.of("output.owl")
    );
  }
}
```

## Command Examples

### Annotate Command

```java
var command = new RobotAnnotateCommand(
    IRI.create("http://example.org/ontology"),
    IRI.create("http://example.org/ontology/v1.0"),
    List.of(
        new PlainAnnotation("rdfs:label", "Example Ontology"),
        new LanguageAnnotation("dc:title", "Exemple", "fr"),
        new LinkAnnotation("dc:source", "http://example.org/source")
    ),
    AnnotateFlags.INTERPOLATE
);
```

### Extract Command (SLME)

```java
var strategy = new SlmeExtractStrategy(
    SlmeExtractMethod.BOT,  // BOT, TOP, or STAR
    List.of("GO:0008150", "GO:0003674")
);
var command = new RobotExtractCommand(
    strategy,
    ExtractIntermediates.minimal,
    HandlingImports.include,
    true  // copy ontology annotations
);
```

### Extract Command (MIREOT)

```java
var strategy = new MireotExtractStrategy(
    List.of("GO:0008150"),  // upper terms
    List.of("GO:0009987"),  // lower terms
    List.of()               // branch-from terms
);
var command = new RobotExtractCommand(strategy, null, null, true);
```

### Collapse Command

```java
// Collapse with custom threshold
var command = new RobotCollapseCommand(
    5,  // minimum subclass count to preserve intermediate classes
    List.of()
);

// Collapse with precious terms (protected from removal)
var command = new RobotCollapseCommand(
    3,
    List.of("GO:0008150", "GO:0003674")  // terms to protect
);
```

### Convert Command

```java
// Simple format conversion to JSON
var command = new RobotConvertCommand(new JsonConvertStrategy());

// Convert to OWL (RDF/XML)
var command = new RobotConvertCommand(new OwlConvertStrategy());

// OBO with validation disabled, cleaning options, and custom prefixes
var strategy = new OboConvertStrategy(
    false,  // disable document structure validation
    List.of(CleanOboOption.drop_extra_labels, CleanOboOption.merge_comments),
    Map.of("FOO", IRI.create("http://example.org/foo#"),
           "BAR", IRI.create("http://example.org/bar#"))
);
var command = new RobotConvertCommand(strategy);
```

### Expand Command

```java
// Expand all macros with source annotations
var command = new RobotExpandCommand(
    List.of(),   // expand all terms
    List.of(),   // don't exclude any
    true         // annotate expansion axioms with dct:source
);

// Exclude specific terms from expansion
var command = new RobotExpandCommand(
    List.of(),                             // expand all
    List.of("GO:0005575", "GO:0008150"),  // except these terms
    false                                  // don't annotate
);
```

### Remove Command

```java
// Remove class hierarchy
var command = new RobotRemoveCommand(
    null,                            // baseIri
    List.of("UBERON:0000062"),       // terms to remove
    List.of(),                       // excludeTerms
    List.of(),                       // includeTerms
    List.of("self", "descendants"),  // selectors
    null,                            // axioms
    List.of(),                       // dropAxiomAnnotations
    RemoveFlags.SIGNATURE            // flags
);

// Remove deprecated classes using pattern selector
var command = new RobotRemoveCommand(
    null,                            // baseIri
    null,                            // terms
    List.of(),                       // excludeTerms
    List.of(),                       // includeTerms
    List.of("owl:deprecated='true'^^xsd:boolean"),  // selectors (pattern selector)
    null,                            // axioms
    List.of()                        // dropAxiomAnnotations
);
```

## Development

### Build

```bash
mvn clean install
```

### Run Tests

```bash
mvn test
```

### Code Formatting

The project uses [Spotless](https://github.com/diffplug/spotless) with Google Java Style (2-space indentation):

```bash
mvn spotless:apply  # Auto-format code
mvn spotless:check  # Check formatting
```

### Static Analysis

```bash
mvn spotbugs:check  # Run SpotBugs analysis
```

## License

This project is licensed under the BSD 2-Clause License - see the [LICENSE](LICENSE) file for details.