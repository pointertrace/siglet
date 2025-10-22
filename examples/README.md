# Examples

These examples demonstrate **spanlets** in action, showcasing different implementation approaches.

## Spanlet
A **spanlet** is a siglet that deals with OpenTelemetry spans.

### Suffix Spanlet
This example shows a spanlet that adds a suffix to span names.

- **[Fat JAR Suffix Spanlet](fatjar-suffix-spanlet)** - Suffix spanlet packaged as a fat JAR.
- **[Spring Boot Spanlet](springboot-suffix-spanlet)** - Suffix spanlet packaged as a Spring Boot application.
- **[Groovy](groovy-suffix-spanlet)** - Suffix spanlet implemented as a Groovy script.

### Span-to-Metric Spanlet
This example shows a spanlet that creates a metric using span data.

- **[Fat JAR Spanlet](fatjar-metric-from-span-spanlet)** - Spanlet that generates a metric, packaged as a fat JAR.
- **[Spring Boot Spanlet](springboot-metric-from-span-spanlet)** - Spanlet that generates a metric, packaged as a Spring Boot application.
- **[Groovy](groovy-metric-from-span-spanlet)** - Spanlet that generates a metric, implemented as a Groovy script.