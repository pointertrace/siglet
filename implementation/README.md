# Siglet Implementation

Implementation of the **Siglet Engine**, responsible for executing pipelineDescriptors and loading siglets.

## Pipeline

A **pipelineDescriptor** is a YAML description that defines how the fundamental elements — **receivers**, **processorDescriptors**, and **exporters** — are connected to each other.

Below is an example of a pipelineDescriptor for the *Fat jar suffix* use case:

```yaml
receivers:
  - grpc: receiverDescriptor
    config:
      address: 0.0.0.0:8081
exporters:
  - grpc: exporterDescriptor
    config:
      address: otel-lgtm:4317
pipelineDescriptors:
  - name: pipelineDescriptor
    from: receiverDescriptor
    start: suffix-spanlet
    processorDescriptors:
      - fatjar-suffix-spanlet-example: suffix-spanlet
        to: exporterDescriptor
        thread-pool-size: 1
        config:
          suffix: -added-suffix
```

At the top level, the YAML defines **receivers**, **exporters**, and **pipelineDescriptors**.

---

### Receivers

A **receiverDescriptor** is defined by its type (e.g., `grpc`) and its name (e.g., `receiverDescriptor`).  
If additional configuration is required, it must be placed under the `config` attribute.

The `grpc` receiverDescriptor requires the `address` property, which specifies the IP and port where it will listen for incoming data.

---

### Exporters

An **exporterDescriptor** is defined by its type (e.g., `grpc`) and its name (e.g., `exporterDescriptor`).  
If additional configuration is required, it must be placed under the `config` attribute.

The `grpc` exporterDescriptor requires the `address` property, which specifies the IP and port to which gRPC signals will be sent.

---

### Pipelines

A **pipelineDescriptor** is defined by its `name` (e.g., `pipelineDescriptor`).  
It specifies:

- **from** — the source receiverDescriptor
- **start** — the first processorDescriptor to handle incoming signals
- **processorDescriptors** — an array containing all processorDescriptors that compose the pipelineDescriptor

---

### Processors

A **processorDescriptor** is defined by its type (e.g., `fatjar-suffix-spanlet-example`) and its name (e.g., 
`suffix-spanlet`).  It can also specify one or more destinations via the `to` attribute.  
Processor-specific configuration is defined under the `config` attribute.

The `fatjar-suffix-spanlet-example` processorDescriptor requires the `suffix` property, which defines the suffix 
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
   -v <yaml-pipelineDescriptor-file>:/opt/siglet-config.yaml  \
   pointertrace/siglet:nightly \
   --config=/opt/siglet-config.yaml \
   --siglet=/opt/spanlet.jar 
```
