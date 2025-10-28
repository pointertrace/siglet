# Siglet

![project logo](./siglet.png)

---

[![Status](https://img.shields.io/badge/status-active-brightgreen.svg)](https://github.com/pointertrace/siglet)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## Description

The goal of this project is to adapt the idea of **Java Servlets** to **OpenTelemetry** signals.

A **siglet** is a lightweight application that implements the Siglet API and is packaged as a self-contained bundle with all its dependencies (e.g., a fat JAR or a Spring Boot uber JAR).

To run a siglet, you create a pipeline and load it into the Siglet implementation.

Complete examples can be found [here](examples/README.md).

## Project Modules

### **API**
An API that must be implemented to create a siglet application

### **Implementation**
The engine that executes pipelines and loads siglets

### **Parser**
Yaml parser

### **Assembly Plugin**
Siglet assembly config to create fat jar siglet applications

### **Examples**
Complete siglet examples