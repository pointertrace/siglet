# Groovy Metric from Span Spanlet Example

This example shows how to run the siglet container with a groovy script that derives a gauge metric
from span duration.

## Prerequisites

- Docker and Docker Compose installed and running
- Bash shell
- Java and Maven to build the example JAR
- [telemetrygen](https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/main/cmd/telemetrygent) installed (used by the provided generate-trace.sh script)

## How to run

2) Start the stack (Grafana + Siglet)
- From the same directory, run:
```bash
docker compose up
```
- This will start the containers

3) Wait for services to be ready
- Keep the terminal open and wait until Grafana reports it is listening on port 3000.

4) Open Grafana
- In a browser, navigate to:
  [http://localhost:3000](http://localhost:3000)

5) Generate sample traces
- In a new terminal (with the project directory as the working directory), run:
```bash
./generate-trace.sh
```
This sends test traces to the OTLP endpoint exposed on port 8081.

6) Explore the metrics
- In Grafana, use Explore or any available dashboards to inspect the gauge metric derived from span duration.

## Stop and clean up

- To stop the stack, press Ctrl+C in the terminal where Docker Compose is running.
- To remove containers and associated resources:
```bash
bash docker-compose down
```

## Troubleshooting

- Ports already in use:
  - Ensure ports 3000 (Grafana) and 8081 (OTLP receiver) are free.
- telemetrygen not found:
  - Install [telemetrygen](https://github.com/open-telemetry/opentelemetry-collector-contrib/tree/main/cmd
    /telemetrygent) or update generate-trace.sh to use a different trace generator available on your system.