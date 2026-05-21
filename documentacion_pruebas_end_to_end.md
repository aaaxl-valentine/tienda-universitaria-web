# Documentación Pruebas End to End (E2E)

Este documento contiene todos los comandos `curl` necesarios para ejecutar las pruebas end-to-end de la Tienda Universitaria. Puedes copiar y pegar cada comando directamente en tu terminal.

---

## Requisitos Previos

- **API en ejecución**: Asegúrate de que el servidor está corriendo en `http://localhost:8080/api`
- **curl instalado**: Verifica con `curl --version`
- **jq instalado** (opcional pero recomendado): `sudo apt-get install jq` o `brew install jq`
- **Variables de ambiente**: Define estas variables para reutilizarlas
  ```bash
  export BASE_URL="http://localhost:8080/api"
  export RUN_ID=$RANDOM
  export TMP_DIR="/tmp/e2e_$RUN_ID"
  mkdir -p "$TMP_DIR"
  ```

---

## Paso 1: Verificar que la API está disponible

```bash
curl -s http://localhost:8080/api/categories
```

**Esperado**: Respuesta JSON (200 OK)

---

## Paso 2: Crear Categoría

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name":"E2E_CAT_001",
    "description":"Categoria E2E"
  }'
```

**Respuesta esperada**:
```json
{
  "id": 1,
  "name": "E2E_CAT_001",
  "description": "Categoria E2E",
  "createdAt": "2026-05-20T18:54:41.881-05:00"
}
```

**Guarda el `id` como `CATEGORY_ID`**: `CATEGORY_ID=1`

---

## Paso 3: Crear Producto con Stock Normal

Reemplaza `CATEGORY_ID` con el valor obtenido en el paso 2.

```bash
curl -X POST "http://localhost:8080/api/products/with-inventory?initialStock=30&minimumStock=5" \
  -H "Content-Type: application/json" \
  -d '{
    "sku":"E2E-SKU-OK-001",
    "name":"Producto OK",
    "description":"Flujo feliz",
    "price":100.00,
    "active":true,
    "categoryId":CATEGORY_ID
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `PRODUCT_OK_ID`

---

## Paso 4: Crear Producto con Stock Bajo

```bash
curl -X POST "http://localhost:8080/api/products/with-inventory?initialStock=1&minimumStock=1" \
  -H "Content-Type: application/json" \
  -d '{
    "sku":"E2E-SKU-LOW-001",
    "name":"Producto LOW",
    "description":"Rechazo pago",
    "price":50.00,
    "active":true,
    "categoryId":CATEGORY_ID
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `PRODUCT_LOW_ID`

---

## Paso 5: Crear Cliente

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"E2E",
    "lastName":"Runner",
    "email":"e2e_test@example.com",
    "status":"ACTIVE"
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `CUSTOMER_ID`

---

## Paso 6: Crear Dirección para el Cliente

Reemplaza `CUSTOMER_ID` con el valor obtenido en el paso 5.

```bash
curl -X POST "http://localhost:8080/api/customers/CUSTOMER_ID/addresses" \
  -H "Content-Type: application/json" \
  -d '{
    "street":"Calle E2E",
    "city":"Santa Marta",
    "department":"Magdalena",
    "postalCode":"470001",
    "customerId":CUSTOMER_ID,
    "isDefault":true
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `ADDRESS_ID`

---

## Paso 7: Flujo Feliz - Crear Pedido con Stock Suficiente

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "status":"CREATED",
    "customerId":CUSTOMER_ID,
    "addressId":ADDRESS_ID,
    "items":[
      {
        "productId":PRODUCT_OK_ID,
        "quantity":2
      }
    ]
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `ORDER_OK_ID`

---

## Paso 8: Procesar Pago del Pedido OK

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_OK_ID/pay" \
  -H "Content-Type: application/json"
```

**Esperado**: HTTP 200 OK

---

## Paso 9: Enviar Pedido OK

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_OK_ID/ship" \
  -H "Content-Type: application/json"
```

**Esperado**: HTTP 200 OK

---

## Paso 10: Entregar Pedido OK

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_OK_ID/deliver" \
  -H "Content-Type: application/json"
```

**Esperado**: HTTP 200 OK

---

## Paso 11: Consultar Historial de Pedido OK

```bash
curl -X GET "http://localhost:8080/api/orders/ORDER_OK_ID/history"
```

**Esperado**: HTTP 200 OK con historial de cambios

---

## Paso 12: Rechazo de Pago - Crear Pedido con Stock Insuficiente

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "status":"CREATED",
    "customerId":CUSTOMER_ID,
    "addressId":ADDRESS_ID,
    "items":[
      {
        "productId":PRODUCT_LOW_ID,
        "quantity":3
      }
    ]
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `ORDER_LOW_ID`

---

## Paso 13: Intentar Pagar Pedido con Stock Insuficiente

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_LOW_ID/pay" \
  -H "Content-Type: application/json"
```

**Esperado**: 
- **HTTP 400 Bad Request** (Rechazo por stock insuficiente)
- Mensaje de error indicando falta de inventario

---

## Paso 14: Cancelación de Pedido Pagado - Crear Pedido

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "status":"CREATED",
    "customerId":CUSTOMER_ID,
    "addressId":ADDRESS_ID,
    "items":[
      {
        "productId":PRODUCT_OK_ID,
        "quantity":1
      }
    ]
  }'
```

**Esperado**: 
- HTTP 201 Created
- Guarda el `id` como `ORDER_CANCEL_ID`

---

## Paso 15: Pagar Pedido a Cancelar

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_CANCEL_ID/pay" \
  -H "Content-Type: application/json"
```

**Esperado**: HTTP 200 OK

---

## Paso 16: Cancelar Pedido Pagado

```bash
curl -X PUT "http://localhost:8080/api/orders/ORDER_CANCEL_ID/cancel" \
  -H "Content-Type: application/json"
```

**Esperado**: HTTP 200 OK

---

## Paso 17: Reporte - Productos con Stock Bajo

```bash
curl -X GET "http://localhost:8080/api/reports/low-stock-products"
```

**Esperado**: HTTP 200 OK con lista de productos

---

## Paso 18: Reporte - Productos Más Vendidos

```bash
curl -X GET "http://localhost:8080/api/reports/best-selling-products?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59"
```

**Esperado**: HTTP 200 OK con lista de productos vendidos

---

## Paso 19: Reporte - Ingresos Mensuales

```bash
curl -X GET "http://localhost:8080/api/reports/monthly-income?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59"
```

**Esperado**: HTTP 200 OK con datos de ingresos

---

## Paso 20: Reporte - Clientes Top

```bash
curl -X GET "http://localhost:8080/api/reports/top-customers?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59"
```

**Esperado**: HTTP 200 OK con lista de clientes

---

## Paso 21: Validación - Producto Inválido (Debe Fallar)

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku":"BAD-SKU-001",
    "name":"",
    "description":"x",
    "price":-1,
    "active":true,
    "categoryId":CATEGORY_ID
  }'
```

**Esperado**: 
- **HTTP 400 Bad Request**
- Mensaje indicando campos inválidos (nombre vacío, precio negativo)

---

## Paso 22: Validación - Email Inválido (Debe Fallar)

```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "firstName":"Bad",
    "lastName":"Email",
    "email":"bad-email",
    "status":"ACTIVE"
  }'
```

**Esperado**: 
- **HTTP 400 Bad Request**
- Mensaje indicando que el email no es válido

---

## Paso 23: Validación - Orden sin Items (Debe Fallar)

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "status":"CREATED",
    "customerId":CUSTOMER_ID,
    "addressId":ADDRESS_ID,
    "items":[]
  }'
```

**Esperado**: 
- **HTTP 400 Bad Request**
- Mensaje indicando que la orden debe tener al menos un item

---

## Paso 24: Actualizar Producto (PUT)

```bash
curl -X PUT "http://localhost:8080/api/products/PRODUCT_OK_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "sku":"E2E-SKU-OK-001",
    "name":"Producto OK Actualizado",
    "description":"Flujo feliz actualizado",
    "price":120.00,
    "active":true,
    "categoryId":CATEGORY_ID
  }'
```

**Esperado**: HTTP 200 OK con datos actualizados

---

## Script Completo en Bash

Si prefieres automatizar todas las pruebas, copia este script en tu terminal bash:

```bash
#!/bin/bash

# Variables globales
BASE_URL="http://localhost:8080/api"
RUN_ID=$RANDOM
EMAIL="e2e_${RUN_ID}@example.com"
TMP_DIR="/tmp/e2e_${RUN_ID}"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir
print_step() {
    echo -e "${GREEN}[✓]${NC} $1"
}

print_error() {
    echo -e "${RED}[✗]${NC} $1"
}

print_info() {
    echo -e "${YELLOW}[i]${NC} $1"
}

# Crear directorio temporal
mkdir -p "$TMP_DIR"

echo "=========================================="
echo "PRUEBAS E2E - TIENDA UNIVERSITARIA"
echo "Run ID: $RUN_ID"
echo "=========================================="
echo ""

# Verificar API
print_info "Verificando API en $BASE_URL..."
if ! curl -s "$BASE_URL/categories" > /dev/null 2>&1; then
    print_error "API no disponible en $BASE_URL"
    exit 1
fi
print_step "API disponible"

# Paso 1: Crear Categoría
print_info "Crear categoría..."
CAT_RESPONSE=$(curl -s -X POST "$BASE_URL/categories" \
    -H "Content-Type: application/json" \
    -d "{
        \"name\":\"E2E_CAT_${RUN_ID}\",
        \"description\":\"Categoria E2E\"
    }")

CATEGORY_ID=$(echo "$CAT_RESPONSE" | jq -r '.id')
if [[ "$CATEGORY_ID" =~ ^[0-9]+$ ]]; then
    print_step "Categoría creada: $CATEGORY_ID"
else
    print_error "Fallo al crear categoría"
    echo "$CAT_RESPONSE" | jq .
    exit 1
fi

# Paso 2: Crear Producto OK
print_info "Crear producto con stock normal..."
PROD_OK_RESPONSE=$(curl -s -X POST "$BASE_URL/products/with-inventory?initialStock=30&minimumStock=5" \
    -H "Content-Type: application/json" \
    -d "{
        \"sku\":\"E2E-SKU-OK-${RUN_ID}\",
        \"name\":\"Producto OK\",
        \"description\":\"Flujo feliz\",
        \"price\":100.00,
        \"active\":true,
        \"categoryId\":$CATEGORY_ID
    }")

PRODUCT_OK_ID=$(echo "$PROD_OK_RESPONSE" | jq -r '.id')
if [[ "$PRODUCT_OK_ID" =~ ^[0-9]+$ ]]; then
    print_step "Producto OK creado: $PRODUCT_OK_ID"
else
    print_error "Fallo al crear producto OK"
    echo "$PROD_OK_RESPONSE" | jq .
    exit 1
fi

# Paso 3: Crear Producto LOW
print_info "Crear producto con stock bajo..."
PROD_LOW_RESPONSE=$(curl -s -X POST "$BASE_URL/products/with-inventory?initialStock=1&minimumStock=1" \
    -H "Content-Type: application/json" \
    -d "{
        \"sku\":\"E2E-SKU-LOW-${RUN_ID}\",
        \"name\":\"Producto LOW\",
        \"description\":\"Rechazo pago\",
        \"price\":50.00,
        \"active\":true,
        \"categoryId\":$CATEGORY_ID
    }")

PRODUCT_LOW_ID=$(echo "$PROD_LOW_RESPONSE" | jq -r '.id')
if [[ "$PRODUCT_LOW_ID" =~ ^[0-9]+$ ]]; then
    print_step "Producto LOW creado: $PRODUCT_LOW_ID"
else
    print_error "Fallo al crear producto LOW"
    echo "$PROD_LOW_RESPONSE" | jq .
    exit 1
fi

# Paso 4: Crear Cliente
print_info "Crear cliente..."
CUST_RESPONSE=$(curl -s -X POST "$BASE_URL/customers" \
    -H "Content-Type: application/json" \
    -d "{
        \"firstName\":\"E2E\",
        \"lastName\":\"Runner\",
        \"email\":\"$EMAIL\",
        \"status\":\"ACTIVE\"
    }")

CUSTOMER_ID=$(echo "$CUST_RESPONSE" | jq -r '.id')
if [[ "$CUSTOMER_ID" =~ ^[0-9]+$ ]]; then
    print_step "Cliente creado: $CUSTOMER_ID"
else
    print_error "Fallo al crear cliente"
    echo "$CUST_RESPONSE" | jq .
    exit 1
fi

# Paso 5: Crear Dirección
print_info "Crear dirección..."
ADDR_RESPONSE=$(curl -s -X POST "$BASE_URL/customers/$CUSTOMER_ID/addresses" \
    -H "Content-Type: application/json" \
    -d "{
        \"street\":\"Calle E2E\",
        \"city\":\"Santa Marta\",
        \"department\":\"Magdalena\",
        \"postalCode\":\"470001\",
        \"customerId\":$CUSTOMER_ID,
        \"isDefault\":true
    }")

ADDRESS_ID=$(echo "$ADDR_RESPONSE" | jq -r '.id')
if [[ "$ADDRESS_ID" =~ ^[0-9]+$ ]]; then
    print_step "Dirección creada: $ADDRESS_ID"
else
    print_error "Fallo al crear dirección"
    echo "$ADDR_RESPONSE" | jq .
    exit 1
fi

# Paso 6: Crear Orden OK
print_info "Crear orden (flujo feliz)..."
ORDER_OK_RESPONSE=$(curl -s -X POST "$BASE_URL/orders" \
    -H "Content-Type: application/json" \
    -d "{
        \"status\":\"CREATED\",
        \"customerId\":$CUSTOMER_ID,
        \"addressId\":$ADDRESS_ID,
        \"items\":[{\"productId\":$PRODUCT_OK_ID,\"quantity\":2}]
    }")

ORDER_OK_ID=$(echo "$ORDER_OK_RESPONSE" | jq -r '.id')
if [[ "$ORDER_OK_ID" =~ ^[0-9]+$ ]]; then
    print_step "Orden OK creada: $ORDER_OK_ID"
else
    print_error "Fallo al crear orden OK"
    echo "$ORDER_OK_RESPONSE" | jq .
    exit 1
fi

# Paso 7: Pagar Orden OK
print_info "Procesar pago..."
PAY_RESPONSE=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_OK_ID/pay" \
    -H "Content-Type: application/json")
if echo "$PAY_RESPONSE" | jq . > /dev/null 2>&1; then
    print_step "Pago procesado"
else
    print_error "Fallo al procesar pago"
    echo "$PAY_RESPONSE"
    exit 1
fi

# Paso 8: Enviar Orden OK
print_info "Enviar orden..."
SHIP_RESPONSE=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_OK_ID/ship" \
    -H "Content-Type: application/json")
if echo "$SHIP_RESPONSE" | jq . > /dev/null 2>&1; then
    print_step "Orden enviada"
else
    print_error "Fallo al enviar orden"
    echo "$SHIP_RESPONSE"
    exit 1
fi

# Paso 9: Entregar Orden OK
print_info "Entregar orden..."
DELIVER_RESPONSE=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_OK_ID/deliver" \
    -H "Content-Type: application/json")
if echo "$DELIVER_RESPONSE" | jq . > /dev/null 2>&1; then
    print_step "Orden entregada"
else
    print_error "Fallo al entregar orden"
    echo "$DELIVER_RESPONSE"
    exit 1
fi

# Paso 10: Ver Historial
print_info "Consultar historial..."
HISTORY_RESPONSE=$(curl -s -X GET "$BASE_URL/orders/$ORDER_OK_ID/history")
if echo "$HISTORY_RESPONSE" | jq . > /dev/null 2>&1; then
    print_step "Historial consultado"
else
    print_error "Fallo al consultar historial"
    echo "$HISTORY_RESPONSE"
    exit 1
fi

# Paso 11: Crear Orden LOW
print_info "Crear orden con stock insuficiente..."
ORDER_LOW_RESPONSE=$(curl -s -X POST "$BASE_URL/orders" \
    -H "Content-Type: application/json" \
    -d "{
        \"status\":\"CREATED\",
        \"customerId\":$CUSTOMER_ID,
        \"addressId\":$ADDRESS_ID,
        \"items\":[{\"productId\":$PRODUCT_LOW_ID,\"quantity\":3}]
    }")

ORDER_LOW_ID=$(echo "$ORDER_LOW_RESPONSE" | jq -r '.id')
if [[ "$ORDER_LOW_ID" =~ ^[0-9]+$ ]]; then
    print_step "Orden LOW creada: $ORDER_LOW_ID"
else
    print_error "Fallo al crear orden LOW"
    echo "$ORDER_LOW_RESPONSE" | jq .
    exit 1
fi

# Paso 12: Intentar Pagar (Debe Fallar con 400)
print_info "Intentar pagar orden con stock insuficiente (debe fallar)..."
PAY_LOW_RESPONSE=$(curl -s -w "\n%{http_code}" -X PUT "$BASE_URL/orders/$ORDER_LOW_ID/pay" \
    -H "Content-Type: application/json")

HTTP_CODE=$(echo "$PAY_LOW_RESPONSE" | tail -1)
if [ "$HTTP_CODE" = "400" ]; then
    print_step "Rechazo esperado: Stock insuficiente (HTTP 400)"
else
    print_error "Se esperaba HTTP 400, se recibió HTTP $HTTP_CODE"
    echo "$PAY_LOW_RESPONSE"
    exit 1
fi

# Paso 13: Crear Orden para Cancelar
print_info "Crear orden para cancelación..."
ORDER_CANCEL_RESPONSE=$(curl -s -X POST "$BASE_URL/orders" \
    -H "Content-Type: application/json" \
    -d "{
        \"status\":\"CREATED\",
        \"customerId\":$CUSTOMER_ID,
        \"addressId\":$ADDRESS_ID,
        \"items\":[{\"productId\":$PRODUCT_OK_ID,\"quantity\":1}]
    }")

ORDER_CANCEL_ID=$(echo "$ORDER_CANCEL_RESPONSE" | jq -r '.id')
if [[ "$ORDER_CANCEL_ID" =~ ^[0-9]+$ ]]; then
    print_step "Orden a cancelar creada: $ORDER_CANCEL_ID"
else
    print_error "Fallo al crear orden para cancelación"
    echo "$ORDER_CANCEL_RESPONSE" | jq .
    exit 1
fi

# Paso 14: Pagar Orden para Cancelar
print_info "Pagar orden para cancelación..."
PAY_CANCEL_RESPONSE=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_CANCEL_ID/pay" \
    -H "Content-Type: application/json")
if echo "$PAY_CANCEL_RESPONSE" | jq . > /dev/null 2>&1; then
    print_step "Pago de orden a cancelar procesado"
else
    print_error "Fallo al pagar orden para cancelación"
    echo "$PAY_CANCEL_RESPONSE"
    exit 1
fi

# Paso 15: Cancelar Orden Pagada
print_info "Cancelar orden pagada..."
CANCEL_RESPONSE=$(curl -s -X PUT "$BASE_URL/orders/$ORDER_CANCEL_ID/cancel" \
    -H "Content-Type: application/json")
if echo "$CANCEL_RESPONSE" | jq . > /dev/null 2>&1; then
    print_step "Orden cancelada"
else
    print_error "Fallo al cancelar orden"
    echo "$CANCEL_RESPONSE"
    exit 1
fi

# Paso 16: Reportes
print_info "Consultar reportes..."

LOW_STOCK=$(curl -s -X GET "$BASE_URL/reports/low-stock-products")
if echo "$LOW_STOCK" | jq . > /dev/null 2>&1; then
    print_step "Reporte: Productos con stock bajo"
else
    print_error "Fallo al consultar reporte de stock bajo"
fi

BEST_SELLING=$(curl -s -X GET "$BASE_URL/reports/best-selling-products?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59")
if echo "$BEST_SELLING" | jq . > /dev/null 2>&1; then
    print_step "Reporte: Productos más vendidos"
else
    print_error "Fallo al consultar reporte de productos vendidos"
fi

MONTHLY_INCOME=$(curl -s -X GET "$BASE_URL/reports/monthly-income?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59")
if echo "$MONTHLY_INCOME" | jq . > /dev/null 2>&1; then
    print_step "Reporte: Ingresos mensuales"
else
    print_error "Fallo al consultar reporte de ingresos"
fi

TOP_CUSTOMERS=$(curl -s -X GET "$BASE_URL/reports/top-customers?startDate=2026-01-01T00:00:00&endDate=2026-12-31T23:59:59")
if echo "$TOP_CUSTOMERS" | jq . > /dev/null 2>&1; then
    print_step "Reporte: Clientes top"
else
    print_error "Fallo al consultar reporte de clientes top"
fi

# Paso 17: Validación - Producto Inválido (Debe Fallar)
print_info "Validar producto inválido (debe fallar)..."
BAD_PRODUCT_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/products" \
    -H "Content-Type: application/json" \
    -d "{
        \"sku\":\"BAD-SKU-${RUN_ID}\",
        \"name\":\"\",
        \"description\":\"x\",
        \"price\":-1,
        \"active\":true,
        \"categoryId\":$CATEGORY_ID
    }")

HTTP_CODE=$(echo "$BAD_PRODUCT_RESPONSE" | tail -1)
if [ "$HTTP_CODE" = "400" ]; then
    print_step "Validación correcta: Producto rechazado (HTTP 400)"
else
    print_error "Se esperaba HTTP 400, se recibió HTTP $HTTP_CODE"
fi

# Paso 18: Validación - Email Inválido (Debe Fallar)
print_info "Validar email inválido (debe fallar)..."
BAD_EMAIL_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/customers" \
    -H "Content-Type: application/json" \
    -d "{
        \"firstName\":\"Bad\",
        \"lastName\":\"Email\",
        \"email\":\"bad-email\",
        \"status\":\"ACTIVE\"
    }")

HTTP_CODE=$(echo "$BAD_EMAIL_RESPONSE" | tail -1)
if [ "$HTTP_CODE" = "400" ]; then
    print_step "Validación correcta: Email rechazado (HTTP 400)"
else
    print_error "Se esperaba HTTP 400, se recibió HTTP $HTTP_CODE"
fi

# Paso 19: Validación - Orden sin Items (Debe Fallar)
print_info "Validar orden sin items (debe fallar)..."
BAD_ORDER_RESPONSE=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/orders" \
    -H "Content-Type: application/json" \
    -d "{
        \"status\":\"CREATED\",
        \"customerId\":$CUSTOMER_ID,
        \"addressId\":$ADDRESS_ID,
        \"items\":[]
    }")

HTTP_CODE=$(echo "$BAD_ORDER_RESPONSE" | tail -1)
if [ "$HTTP_CODE" = "400" ]; then
    print_step "Validación correcta: Orden rechazada (HTTP 400)"
else
    print_error "Se esperaba HTTP 400, se recibió HTTP $HTTP_CODE"
fi

# Paso 20: Actualizar Producto (PUT)
print_info "Actualizar producto..."
UPDATE_PRODUCT=$(curl -s -X PUT "$BASE_URL/products/$PRODUCT_OK_ID" \
    -H "Content-Type: application/json" \
    -d "{
        \"sku\":\"E2E-SKU-OK-${RUN_ID}\",
        \"name\":\"Producto OK Actualizado\",
        \"description\":\"Flujo feliz actualizado\",
        \"price\":120.00,
        \"active\":true,
        \"categoryId\":$CATEGORY_ID
    }")

if echo "$UPDATE_PRODUCT" | jq . > /dev/null 2>&1; then
    print_step "Producto actualizado"
else
    print_error "Fallo al actualizar producto"
    echo "$UPDATE_PRODUCT"
fi

# Limpiar
rm -rf "$TMP_DIR"

echo ""
echo "=========================================="
echo "E2E completado OK"
echo "category=$CATEGORY_ID productOK=$PRODUCT_OK_ID productLOW=$PRODUCT_LOW_ID"
echo "customer=$CUSTOMER_ID address=$ADDRESS_ID"
echo "orders=$ORDER_OK_ID,$ORDER_LOW_ID,$ORDER_CANCEL_ID"
echo "=========================================="
```

### Cómo usar el script:

1. **Guarda el script** en un archivo llamado `e2e_test.sh`:
   ```bash
   nano e2e_test.sh
   # Copia y pega el script anterior
   # Presiona Ctrl+X, luego Y, luego Enter
   ```

2. **Dale permisos de ejecución**:
   ```bash
   chmod +x e2e_test.sh
   ```

3. **Ejecuta el script**:
   ```bash
   ./e2e_test.sh
   ```

4. **Con salida detallada** (debug):
   ```bash
   bash -x e2e_test.sh
   ```

---

## Instalación de jq (si no lo tienes)

**En Linux (Ubuntu/Debian)**:
```bash
sudo apt-get update
sudo apt-get install -y jq
```

**En macOS**:
```bash
brew install jq
```

**En Windows (con WSL)**:
```bash
sudo apt-get install -y jq
```

Si no quieres instalar jq, puedes usar alternativas como `grep` y `sed`, pero jq es más confiable.

---

## Notas Importantes

1. **jq para parsear JSON**: El script usa `jq` para extraer datos. Si no lo tienes instalado:
   ```bash
   # Linux
   sudo apt-get install jq
   # macOS
   brew install jq
   ```

2. **Variables de entorno**: Antes de ejecutar cualquier comando, define:
   ```bash
   export BASE_URL="http://localhost:8080/api"
   ```

3. **Errores comunes**:
   - `Connection refused`: La API no está corriendo
   - `400 Bad Request`: Validación fallida, revisa los datos enviados
   - `404 Not Found`: El ID no existe, verifica que copiaste correctamente
   - `jq: command not found`: Instala jq como se indicó arriba

4. **Para ver respuestas completas**, agrega `-v` a curl:
   ```bash
   curl -v -X GET http://localhost:8080/api/categories
   ```

5. **Exportar a archivo**: Guarda respuestas con `>`:
   ```bash
   curl -s http://localhost:8080/api/categories > respuesta.json
   ```

6. **Extraer solo el ID de una respuesta** (si tienes jq):
   ```bash
   curl -s http://localhost:8080/api/categories | jq '.id'
   ```

7. **Extraer sin jq** (usando grep y sed):
   ```bash
   curl -s http://localhost:8080/api/categories | grep -o '"id":[0-9]*' | cut -d: -f2
   ```

