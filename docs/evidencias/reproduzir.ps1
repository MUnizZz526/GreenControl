param(
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$OutputDir = (Join-Path $PSScriptRoot 'curl')
)
$ErrorActionPreference = 'Stop'
# Somente ambiente de homologação: deixa um snapshot de relatório persistido.
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
$results = [System.Collections.Generic.List[object]]::new()
$utf8 = [System.Text.UTF8Encoding]::new($false)
function Invoke-Evidence([string]$Name, [string]$Method, [string]$Path, [string]$Body, [int]$Expected) {
    $responseFile = Join-Path $OutputDir "$Name-response.txt"
    $headerFile = Join-Path $OutputDir "$Name-headers.txt"
    $curlArgs = @('--silent', '--show-error', '--request', $Method, '--dump-header', $headerFile,
        '--output', $responseFile, '--write-out', '%{http_code}', "$BaseUrl$Path")
    if ($Body) {
        $requestFile = Join-Path $OutputDir "$Name-request.json"
        [System.IO.File]::WriteAllText($requestFile, $Body, $utf8)
        $curlArgs += @('--header', 'Content-Type: application/json', '--data-binary', "@$requestFile")
    }
    $httpCode = & curl.exe @curlArgs
    if ($LASTEXITCODE -ne 0 -or [int]$httpCode -ne $Expected) {
        throw "$Method $Path retornou $httpCode; esperado $Expected. Consulte $responseFile."
    }
    $results.Add([pscustomobject]@{ metodo=$Method; caminho=$Path; status=[int]$httpCode; resposta="$Name-response.txt" })
    return (Get-Content -LiteralPath $responseFile -Raw)
}
$body = '{"quilometroInicial":0,"quilometroFinal":10,"nivelVegetacao":65,"tipoClima":"Seco"}'
$created = Invoke-Evidence '01-post-trecho' 'POST' '/api/trechos' $body 201 | ConvertFrom-Json
$trechoId = $created.id
$null = Invoke-Evidence '02-get-trecho' 'GET' "/api/trechos/$trechoId" '' 200
$null = Invoke-Evidence '03-put-trecho' 'PUT' "/api/trechos/$trechoId" ($body.Replace('65','30')) 200
$null = Invoke-Evidence '04-post-relatorio' 'POST' '/api/relatorios' '' 201
$null = Invoke-Evidence '05-get-historico' 'GET' '/api/relatorios' '' 200
$today = Get-Date -Format 'yyyy-MM-dd'
$null = Invoke-Evidence '06-get-periodo' 'GET' "/api/relatorios/periodo?inicio=$today&fim=$today" '' 200
$null = Invoke-Evidence '07-delete-trecho' 'DELETE' "/api/trechos/$trechoId" '' 204
$null = Invoke-Evidence '08-invalido' 'POST' '/api/trechos' ($body.Replace('65','-1')) 400
$null = Invoke-Evidence '09-inexistente' 'GET' "/api/trechos/$trechoId" '' 404
[System.IO.File]::WriteAllText((Join-Path $OutputDir 'resultado.json'), ($results | ConvertTo-Json), $utf8)
Write-Output "Nove requisições aprovadas. Respostas em $OutputDir"
