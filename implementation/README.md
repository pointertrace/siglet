# Siglet Implementation

A Siglet engine implementation that executes pipelines and loads siglets

## Pipeline
A pipeline is a yaml description of how the fundamental elements (receivers, processors and exporters) are conected to 
each other.

Lets take a look at the pipeline for the groovy suffix example:
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
    - spanlet-groovy-action: suffix-spanlet
      to: exporter
      thread-pool-size: 1
      config:
        action: signal.name = signal.name + "-added-suffix-groovy"
```

At the first level there are the definitions of the **receivers**, **exporters** and **pipelines**.   

## **receivers**
A receiver is defined by its type (**grpc**) and its name (**receiver**). If the receiver needs more configuration,
it must be bellow **config** receiver attribute. The **grpc** receiver has **address** required property to define IP 
and 
port to be listener.

## **exporters**
An exporter is defined by its type (**grpc**) and its name (**exporter**). If the exporter needs more 
configuration, it must be bellow **config** exporter attribute. The **grpc** exporter has **address** required 
property to 
define IP and port to send grpc signals.

## **pipelines**
A pipeline is defined by its name (**pipeline**), defines its source through **from** (receiver) and to which 
processors will receive the signals through **start** (suffix-spanlet). The property **processors** is an array of 
all processors that compose the pipeline

## **processors**
A processor is defined by its type (**spanlet-groovy-action**) and its name (**suffix-spanlet**). The processor can 
also define one or more destinations through **to** attribute. The processor-specific configuration is defined at 
**config** attribute. The **spanlet-groovy-action** has **action** required property to define the groovy script 
that is executed for each span signal the reaches the processor.


