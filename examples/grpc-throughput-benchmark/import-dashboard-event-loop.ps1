# =============================================================================
#  Import Grafana Dashboard - Telemetrygen Only (PowerShell)
# =============================================================================

param(
    [string]$GrafanaUrl = "http://localhost:3000",
    [string]$GrafanaUser = "admin",
    [string]$GrafanaPassword = "admin",
    [string]$DashboardFile = ".\grafana-dashboard-signals.json"
)

$ErrorActionPreference = "Stop"

Write-Host "Importando dashboard Telemetrygen para Grafana..."
Write-Host "  URL: $GrafanaUrl"
Write-Host "  Dashboard: $DashboardFile"
Write-Host ""

# Aguardar Grafana ficar pronto
Write-Host "Aguardando Grafana ficar disponivel..." -ForegroundColor Yellow
$maxAttempts = 60
$attempt = 0
while ($attempt -lt $maxAttempts) {
    $attempt++
    try {
        $response = Invoke-WebRequest -Uri "$GrafanaUrl/api/health" -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host "Grafana disponivel!" -ForegroundColor Green
            break
        }
    }
    catch {
        Write-Host "  ... tentativa $attempt/$maxAttempts" -ForegroundColor Gray
        Start-Sleep -Seconds 1
    }
}

# Carregar dashboard JSON
if (-not (Test-Path $DashboardFile)) {
    Write-Host "Arquivo nao encontrado: $DashboardFile" -ForegroundColor Red
    exit 1
}

$dashboardJson = Get-Content $DashboardFile -Raw | ConvertFrom-Json

# Preparar credenciais
$auth = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("$GrafanaUser`:$GrafanaPassword"))
$headers = @{
    "Authorization" = "Basic $auth"
    "Content-Type"  = "application/json"
}

# Verificar/criar datasource Prometheus
Write-Host "Verificando datasource Prometheus..." -ForegroundColor Yellow
try {
    $ds = Invoke-WebRequest -Uri "$GrafanaUrl/api/datasources/name/Prometheus" `
        -Headers $headers `
        -ErrorAction SilentlyContinue | ConvertFrom-Json
    Write-Host "Datasource Prometheus encontrado" -ForegroundColor Green
}
catch {
    Write-Host "Criando datasource Prometheus..." -ForegroundColor Yellow
    $dsPayload = @{
        name     = "Prometheus"
        type     = "prometheus"
        url      = "http://localhost:9090"
        access   = "proxy"
        isDefault = $true
    } | ConvertTo-Json

    try {
        Invoke-WebRequest -Uri "$GrafanaUrl/api/datasources" `
            -Method Post `
            -Headers $headers `
            -Body $dsPayload `
            -ErrorAction SilentlyContinue | Out-Null
        Write-Host "Datasource criado com sucesso" -ForegroundColor Green
    }
    catch {
        Write-Host "Datasource pode ja existir (ou autenticacao falhou)" -ForegroundColor Gray
    }
}

# Importar dashboard
Write-Host "Importando dashboard..." -ForegroundColor Yellow
$payload = @{
    dashboard = $dashboardJson
    overwrite = $true
} | ConvertTo-Json -Depth 10

try {
    $result = Invoke-WebRequest -Uri "$GrafanaUrl/api/dashboards/db" `
        -Method Post `
        -Headers $headers `
        -Body $payload | ConvertFrom-Json

    Write-Host ""
    Write-Host "Dashboard importado com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Acesse: $GrafanaUrl/d/telemetrygen-signals" -ForegroundColor Cyan
}
catch {
    Write-Host "Erro ao importar dashboard:" -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

