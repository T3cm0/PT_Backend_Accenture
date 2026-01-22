# Backend (Spring Boot + MySQL)

Backend en Spring Boot pensado para ejecutarse con dos entornos: `test` y `prod`.
La base de datos es MySQL en Cloud SQL (GCP).

## Entornos desplegados

- **Prod**: https://backend-prod-yvzzo66h2a-uc.a.run.app
- **Test**: https://backend-test-yvzzo66h2a-uc.a.run.app

## Autor

Este codigo fue desarrollado por Sebastian Rodriguez.

## Arquitectura

- **Backend**: Spring Boot + Java 21
- **Base de datos**: MySQL 8.0 (Cloud SQL)
- **Contenedores**: Docker (imagenes separadas para test y prod)
- **Cloud**: Google Cloud Platform (Cloud SQL, Cloud Run)

## Requisitos

- JDK 21 instalado y `JAVA_HOME` configurado.
- Maven Wrapper incluido (`./mvnw` o `.\mvnw.cmd`).
- Acceso a la instancia de Cloud SQL (IP allowlist si usas IP publica).
- (Opcional) Terraform y Google Cloud SDK si quieres aprovisionar IaC.

## Configuracion de entorno

Perfiles disponibles:

- `test`: usado por defecto en local (`spring.profiles.default=test`).
- `prod`: usado para despliegue.

Variables requeridas:

- `DB_URL`
- `DB_URL_TEST` (solo para CI/CD en entorno test)
- `DB_USERNAME`
- `DB_PASSWORD`
- `SPRING_PROFILES_ACTIVE` (opcional; si no se define, usa `test`)

Puedes partir de un archivo local con variables:

- `.env.test` para desarrollo
- `.env.prod` para produccion
- `.env.example` como plantilla

Nota: los `.env*` estan ignorados en git por seguridad.

### Cloud Run + Cloud SQL (socket factory)

En Cloud Run se recomienda usar el conector con socket factory en el `DB_URL`:

- Prod:
  `jdbc:mysql:///ProdPT?cloudSqlInstance=prueba-accenture-485020:us-central1:free-trial-first-project&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`
- Test:
  `jdbc:mysql:///TestPT?cloudSqlInstance=prueba-accenture-485020:us-central1:free-trial-first-project&socketFactory=com.google.cloud.sql.mysql.SocketFactory&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

## Desarrollo local

### Windows (PowerShell)

Test:

```powershell
Get-Content .env.test | ForEach-Object {
  if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
  $name, $value = $_ -split '=', 2
  Set-Item -Path "env:$name" -Value $value
}
.\mvnw.cmd spring-boot:run
```

Prod:

```powershell
Get-Content .env.prod | ForEach-Object {
  if ($_ -match '^\s*#' -or $_ -match '^\s*$') { return }
  $name, $value = $_ -split '=', 2
  Set-Item -Path "env:$name" -Value $value
}
.\mvnw.cmd spring-boot:run
```

### macOS/Linux (bash/zsh)

Test:

```bash
set -a
source .env.test
set +a
./mvnw spring-boot:run
```

Prod:

```bash
set -a
source .env.prod
set +a
./mvnw spring-boot:run
```

## Docker

### Test

```bash
docker build -t backend-test -f Dockerfile.test .
docker run --env-file .env.test -p 8080:8080 --name backend-test backend-test
```

### Prod

```bash
docker build -t backend-prod -f Dockerfile .
docker run --env-file .env.prod -p 8081:8080 --name backend-prod backend-prod
```

Si ejecutas ambos a la vez:

- Test: `http://localhost:8080`
- Prod: `http://localhost:8081`

Detener y borrar contenedores:
```bash
docker stop backend-test backend-prod
docker rm backend-test backend-prod
```

## Conexion a Cloud SQL (IP publica)

Si te conectas desde tu PC, agrega tu IP en:
GCP Console -> Cloud SQL -> Connections -> Authorized networks.

## Infraestructura (Terraform)

Se incluye IaC basico para Cloud SQL en `infra/terraform`. La idea es que la infraestructura
quede descrita en codigo y la nube se ajuste a lo que esta en los `.tf`.

Este modulo administra:

- Instancia Cloud SQL MySQL 8.0
- Bases `TestPT` y `ProdPT`
- Usuario `app_user`

1) Copia el ejemplo de variables:
```bash
cd infra/terraform
cp terraform.tfvars.example terraform.tfvars
```

2) Ajusta `terraform.tfvars` con tu `project_id`, `instance_name` y password.

3) Autenticacion para Terraform (una sola vez):
```bash
gcloud init
gcloud auth application-default login
gcloud config set project prueba-accenture-485020
```

4) Inicializa y planifica:
```bash
terraform init
terraform plan
```

Si quieres aplicar exactamente el plan mostrado:
```bash
terraform plan -out=plan.tfplan
terraform apply plan.tfplan
```

5) Si la instancia ya existe, importa los recursos para evitar recrearlos:
```bash
terraform import 'google_sql_database_instance.main[0]' \
  'projects/prueba-accenture-485020/instances/free-trial-first-project'
terraform import 'google_sql_database.databases["TestPT"]' \
  'projects/prueba-accenture-485020/instances/free-trial-first-project/databases/TestPT'
terraform import 'google_sql_database.databases["ProdPT"]' \
  'projects/prueba-accenture-485020/instances/free-trial-first-project/databases/ProdPT'
terraform import 'google_sql_user.app_user' \
  'prueba-accenture-485020/free-trial-first-project/%/app_user'
```

Notas:
- Si `terraform plan` muestra cambios que no quieres, ajusta `terraform.tfvars` para que coincida con la configuracion real.
- Si todo esta alineado, `terraform plan` debe mostrar: `No changes. Your infrastructure matches the configuration.`
- Las bases `TestPT` y `ProdPT` se crean por defecto (ver `db_names`).
- `app_user_password` no se rota automaticamente (se ignoran cambios de password).
- `terraform.tfstate` y `.tfvars` no se suben a git (ya estan ignorados).

## CI/CD (GitHub Actions)

Workflows en `.github/workflows`:

- `backend.yml`: build, test, push a Artifact Registry y deploy a Cloud Run.
- `test-wif.yml`: prueba de autenticacion WIF.

Variables de repositorio (Settings -> Variables):

- `GCP_PROJECT_ID`
- `GCP_REGION`
- `ARTIFACT_REGISTRY_REPO` (opcional, default `backend`)
- `GCP_CLOUDSQL_INSTANCE` (ej: `prueba-accenture-485020:us-central1:free-trial-first-project`)
- `GCP_RUN_SA` (opcional, service account para Cloud Run)

Secrets de repositorio (Settings -> Secrets):

- `GCP_WIF_PROVIDER`
- `GCP_SA_EMAIL`
- `DB_URL`
- `DB_URL_TEST`
- `DB_USERNAME`
- `DB_PASSWORD`

Flujo actual:

- build + tests + push de imagen
- despliegue automatico a **testing**
- despliegue a **production** solo tras aprobacion en GitHub Environments

## API (CRUD)

### Franquicias
- Crear: `POST /api/franchises`
- Listar: `GET /api/franchises`
- Detalle: `GET /api/franchises/{id}`
- Actualizar nombre: `PUT /api/franchises/{id}`
- Eliminar (soft delete + cascada): `DELETE /api/franchises/{id}`

### Sucursales
- Crear en franquicia: `POST /api/franchises/{id}/branches`
- Listar por franquicia: `GET /api/franchises/{id}/branches`
- Detalle: `GET /api/branches/{id}`
- Actualizar nombre: `PUT /api/branches/{id}`
- Eliminar (soft delete + cascada): `DELETE /api/branches/{id}`

### Productos
- Crear en sucursal: `POST /api/branches/{branchId}/products`
- Listar por sucursal: `GET /api/branches/{branchId}/products`
- Detalle: `GET /api/products/{id}`
- Actualizar nombre/stock: `PUT /api/products/{id}`
- Eliminar (soft delete): `DELETE /api/products/{id}`

### Reporte
- Producto con mayor stock por sucursal (de una franquicia):
  `GET /api/franchises/{id}/top-stock-products`

## Pruebas

```bash
./mvnw test
```

## Referencias

Las propiedades de cada entorno estan en:

- `src/main/resources/application-test.properties`
- `src/main/resources/application-prod.properties`

## Pruebas en entornos desplegados (para reclutadores)

Se valido el flujo completo: crear franquicia/sucursal/producto, actualizar, consultar top-stock y eliminar (soft delete).

### Test

```powershell
$base = "https://backend-test-yvzzo66h2a-uc.a.run.app"

$fr = Invoke-RestMethod -Method Post "$base/api/franchises" -ContentType "application/json" -Body '{"name":"Franquicia Test"}'
$frId = $fr.id

Invoke-RestMethod "$base/api/franchises"

$br = Invoke-RestMethod -Method Post "$base/api/franchises/$frId/branches" -ContentType "application/json" -Body '{"name":"Sucursal Test"}'
$brId = $br.id

Invoke-RestMethod "$base/api/franchises/$frId/branches"

$pr = Invoke-RestMethod -Method Post "$base/api/branches/$brId/products" -ContentType "application/json" -Body '{"name":"Producto Test","stock":10}'
$prId = $pr.id

Invoke-RestMethod "$base/api/branches/$brId/products"
Invoke-RestMethod "$base/api/products/$prId"

Invoke-RestMethod -Method Put "$base/api/products/$prId" -ContentType "application/json" -Body '{"name":"Producto Test","stock":25}'
Invoke-RestMethod -Method Put "$base/api/branches/$brId" -ContentType "application/json" -Body '{"name":"Sucursal Test v2"}'
Invoke-RestMethod -Method Put "$base/api/franchises/$frId" -ContentType "application/json" -Body '{"name":"Franquicia Test v2"}'

Invoke-RestMethod "$base/api/franchises/$frId"
Invoke-RestMethod "$base/api/franchises/$frId/top-stock-products"

Invoke-WebRequest -Method Delete "$base/api/products/$prId" | Select-Object StatusCode
Invoke-WebRequest -Method Delete "$base/api/branches/$brId" | Select-Object StatusCode
Invoke-WebRequest -Method Delete "$base/api/franchises/$frId" | Select-Object StatusCode

Invoke-RestMethod "$base/api/franchises"
```

```bash
base="https://backend-test-yvzzo66h2a-uc.a.run.app"

fr=$(curl -s -X POST "$base/api/franchises" \
  -H "Content-Type: application/json" \
  -d '{"name":"Franquicia Test"}')
frId=$(echo "$fr" | jq -r '.id')

curl -s "$base/api/franchises"

br=$(curl -s -X POST "$base/api/franchises/$frId/branches" \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Test"}')
brId=$(echo "$br" | jq -r '.id')

curl -s "$base/api/franchises/$frId/branches"

pr=$(curl -s -X POST "$base/api/branches/$brId/products" \
  -H "Content-Type: application/json" \
  -d '{"name":"Producto Test","stock":10}')
prId=$(echo "$pr" | jq -r '.id')

curl -s "$base/api/branches/$brId/products"
curl -s "$base/api/products/$prId"

curl -s -X PUT "$base/api/products/$prId" \
  -H "Content-Type: application/json" \
  -d '{"name":"Producto Test","stock":25}'
curl -s -X PUT "$base/api/branches/$brId" \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Test v2"}'
curl -s -X PUT "$base/api/franchises/$frId" \
  -H "Content-Type: application/json" \
  -d '{"name":"Franquicia Test v2"}'

curl -s "$base/api/franchises/$frId"
curl -s "$base/api/franchises/$frId/top-stock-products"

curl -i -X DELETE "$base/api/products/$prId"
curl -i -X DELETE "$base/api/branches/$brId"
curl -i -X DELETE "$base/api/franchises/$frId"

curl -s "$base/api/franchises"
```

### Prod

```powershell
$base = "https://backend-prod-yvzzo66h2a-uc.a.run.app"

$fr = Invoke-RestMethod -Method Post "$base/api/franchises" -ContentType "application/json" -Body '{"name":"Franquicia Prod"}'
$frId = $fr.id

Invoke-RestMethod "$base/api/franchises"

$br = Invoke-RestMethod -Method Post "$base/api/franchises/$frId/branches" -ContentType "application/json" -Body '{"name":"Sucursal Prod"}'
$brId = $br.id

Invoke-RestMethod "$base/api/franchises/$frId/branches"

$pr = Invoke-RestMethod -Method Post "$base/api/branches/$brId/products" -ContentType "application/json" -Body '{"name":"Producto Prod","stock":10}'
$prId = $pr.id

Invoke-RestMethod "$base/api/branches/$brId/products"
Invoke-RestMethod "$base/api/products/$prId"

Invoke-RestMethod -Method Put "$base/api/products/$prId" -ContentType "application/json" -Body '{"name":"Producto Prod","stock":25}'
Invoke-RestMethod -Method Put "$base/api/branches/$brId" -ContentType "application/json" -Body '{"name":"Sucursal Prod v2"}'
Invoke-RestMethod -Method Put "$base/api/franchises/$frId" -ContentType "application/json" -Body '{"name":"Franquicia Prod v2"}'

Invoke-RestMethod "$base/api/franchises/$frId"
Invoke-RestMethod "$base/api/franchises/$frId/top-stock-products"

Invoke-WebRequest -Method Delete "$base/api/products/$prId" | Select-Object StatusCode
Invoke-WebRequest -Method Delete "$base/api/branches/$brId" | Select-Object StatusCode
Invoke-WebRequest -Method Delete "$base/api/franchises/$frId" | Select-Object StatusCode

Invoke-RestMethod "$base/api/franchises"
```

```bash
base="https://backend-prod-yvzzo66h2a-uc.a.run.app"

fr=$(curl -s -X POST "$base/api/franchises" \
  -H "Content-Type: application/json" \
  -d '{"name":"Franquicia Prod"}')
frId=$(echo "$fr" | jq -r '.id')

curl -s "$base/api/franchises"

br=$(curl -s -X POST "$base/api/franchises/$frId/branches" \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Prod"}')
brId=$(echo "$br" | jq -r '.id')

curl -s "$base/api/franchises/$frId/branches"

pr=$(curl -s -X POST "$base/api/branches/$brId/products" \
  -H "Content-Type: application/json" \
  -d '{"name":"Producto Prod","stock":10}')
prId=$(echo "$pr" | jq -r '.id')

curl -s "$base/api/branches/$brId/products"
curl -s "$base/api/products/$prId"

curl -s -X PUT "$base/api/products/$prId" \
  -H "Content-Type: application/json" \
  -d '{"name":"Producto Prod","stock":25}'
curl -s -X PUT "$base/api/branches/$brId" \
  -H "Content-Type: application/json" \
  -d '{"name":"Sucursal Prod v2"}'
curl -s -X PUT "$base/api/franchises/$frId" \
  -H "Content-Type: application/json" \
  -d '{"name":"Franquicia Prod v2"}'

curl -s "$base/api/franchises/$frId"
curl -s "$base/api/franchises/$frId/top-stock-products"

curl -i -X DELETE "$base/api/products/$prId"
curl -i -X DELETE "$base/api/branches/$brId"
curl -i -X DELETE "$base/api/franchises/$frId"

curl -s "$base/api/franchises"
```
