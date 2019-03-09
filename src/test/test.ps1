# $url = "https://ocio-a16aa33cbbcc.azurewebsites.net/service-portal-webhook-0.0.1-SNAPSHOT"
$url = "http://localhost:8080/service-portal-webhook"

$body = get-content .\resources\795.workitem.updated.json -Raw
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

# basic-auth boilerplate
$username = "69a1f401-690c-4b98-961c-0c337140f630"
$password = "RNeQk8VPVw8TjJT6PSFTJiKy48grEszaMmtMAVjG"

$credPair = "$($username):$($password)"
$encodedCredentials = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes($credPair))
$headers = @{ Authorization = "Basic $encodedCredentials" }

Invoke-RestMethod -Uri "$url/serviceportalv1/email/callback" -Method POST -ContentType "application/json" -Body $body -Headers $headers
# Invoke-RestMethod -Uri "https://webapp-180914134227.azurewebsites.net/service-portal-webhook-0.0.1-SNAPSHOT/serviceportalv1/email/test" -Method GET -ContentType "application/json" 
