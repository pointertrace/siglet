# API

API to be implemented to create a Siglet

## Concepts


### High-Level Pipeline Concepts
A pipelineDescriptor is composed of **Receivers**, **Processors** and **Exporters**

#### Receiver
A receiverDescriptor is responsible to get signals from external sources and bring them to be processed in a pipelineDescriptor  

#### Processor
A Processor acts on each received signal. Currently, only **Spanlets** are implemented. **Spanlets** are able to 
read or change spans inside a pipelineDescriptor  

#### Exporter
An Exporter is responsible to get processed signals out of the pipelineDescriptor to an external destination  
