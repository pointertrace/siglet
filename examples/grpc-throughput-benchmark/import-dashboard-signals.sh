#!/usr/bin/env bash
# Importa o dashboard do OTel Collector no Grafana
set -euo pipefail

GRAFANA_URL="${GRAFANA_URL:-http://localhost:3000}"
GRAFANA_USER="${GRAFANA_USER:-admin}"
GRAFANA_PASSWORD="${GRAFANA_PASSWORD:-admin}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DASHBOARD_FILE="$SCRIPT_DIR/grafana-dashboard-collector.json"

echo "Aguardando Grafana ($GRAFANA_URL)..."
for i in {1..60}; do
  curl -sf "$GRAFANA_URL/api/health" >/dev/null 2>&1 && echo "Grafana OK!" && break
  echo "  ... $i/60"; sleep 2
done

echo "Importando dashboard..."
DASHBOARD_JSON=$(cat "$DASHBOARD_FILE")
curl -sf -X POST "$GRAFANA_URL/api/dashboards/db" \
  -u "$GRAFANA_USER:$GRAFANA_PASSWORD" \
  -H "Content-Type: application/json" \
  -d "{\"dashboard\": $DASHBOARD_JSON, \"overwrite\": true}" | jq -r '.url // .message'

echo ""
echo "Acesse: $GRAFANA_URL/d/otelcol-spans-benchmark"

