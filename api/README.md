# API

API to be implemented to create a Siglet

## Concepts


### High-Level Pipeline Concepts
A pipeline is composed of **Receivers**, **Processors** and **Exporters**

#### Receiver
A receiver is responsible to get signals from external sources and bring them to be processed in a pipeline  

#### Processor
A Processor acts on each received signal. Currently, only **Spanlets** are implemented. **Spanlets** are able to 
read or change spans inside a pipeline  

#### Exporter
An Exporter is responsible to get processed signals out of the pipeline to an external destination  
