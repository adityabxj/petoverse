$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$modules = @(
    "api-gateway",
    "auth-service",
    "user-service",
    "pet-service",
    "vet-service",
    "record-service",
    "appointment-service"
)

$mavenUserHome = Join-Path $HOME ".m2"
if (-not (Test-Path -LiteralPath $mavenUserHome)) {
    New-Item -ItemType Directory -Path $mavenUserHome | Out-Null
}

foreach ($module in $modules) {
    $modulePath = Join-Path $repoRoot $module
    $wrapperPath = Join-Path $modulePath "mvnw.cmd"

    if (-not (Test-Path -LiteralPath $wrapperPath)) {
        throw "Missing Maven wrapper: $wrapperPath"
    }

    Write-Host "Installing dependencies for $module..."
    & $wrapperPath "-Dmaven.repo.local=$mavenUserHome\\repository" "dependency:go-offline" "-DskipTests"

    if ($LASTEXITCODE -ne 0) {
        throw "Dependency install failed for $module"
    }
}

Write-Host "All Maven dependencies are installed in $mavenUserHome\\repository"
