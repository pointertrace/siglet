# Siglet Implementation

Implementation of the **Siglet Engine**, responsible for executing pipelines and loading siglets.

## Pipeline

A **pipeline** is a YAML description that defines how the fundamental elements — **receivers**, **processors**, and **exporters** — are connected to each other.

Below is an example of a pipeline for the *Fat jar suffix* use case:

```yaml
receivers:
  - grpc: receiver
    config:
      address: 0.0.0.0:8081
exporters:
  - grpc: exporter
    config:
      address: otel-lgtm:4317
pipelines:
  - name: pipeline
    from: receiver
    start: suffix-spanlet
    processors:
      - fatjar-suffix-spanlet-example: suffix-spanlet
        to: exporter
        thread-pool-size: 1
        config:
          suffix: -added-suffix
```

At the top level, the YAML defines **receivers**, **exporters**, and **pipelines**.

---

### Receivers

A **receiver** is defined by its type (e.g., `grpc`) and its name (e.g., `receiver`).  
If additional configuration is required, it must be placed under the `config` attribute.

The `grpc` receiver requires the `address` property, which specifies the IP and port where it will listen for incoming data.

---

### Exporters

An **exporter** is defined by its type (e.g., `grpc`) and its name (e.g., `exporter`).  
If additional configuration is required, it must be placed under the `config` attribute.

The `grpc` exporter requires the `address` property, which specifies the IP and port to which gRPC signals will be sent.

---

### Pipelines

A **pipeline** is defined by its `name` (e.g., `pipeline`).  
It specifies:

- **from** — the source receiver
- **start** — the first processor to handle incoming signals
- **processors** — an array containing all processors that compose the pipeline

---

### Processors

A **processor** is defined by its type (e.g., `fatjar-suffix-spanlet-example`) and its name (e.g., 
`suffix-spanlet`).  It can also specify one or more destinations via the `to` attribute.  
Processor-specific configuration is defined under the `config` attribute.

The `fatjar-suffix-spanlet-example` processor requires the `suffix` property, which defines the suffix 
that will be added to each span that is processed.

## Engine Execution
You can execute this in 2 ways: executing in a JVM ou using siglet docker container.

### Executing in a JVM
```bash
java -jar siglet.jar --config=<yaml-pipeine-file> --siglet=<fat-jar-suffix-spanlet-example-jar-file>
```

### Executing using the siglet docker container
```bash
 docker run -it \
   -v <fat-jar-suffix-spanlet-example-jar-file>:/opt/spanlet.jar \
   -v <yaml-pipeline-file>:/opt/siglet-config.yaml  \
   pointertrace/siglet:nightly \
   --config=/opt/siglet-config.yaml \
   --siglet=/opt/spanlet.jar 
```
