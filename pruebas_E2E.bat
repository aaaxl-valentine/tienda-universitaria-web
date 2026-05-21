@echo off
setlocal EnableExtensions EnableDelayedExpansion
goto :main

:post_and_get_id
set "%~3="
set "RESP_FILE=%TMP_DIR%\resp.json"
curl -s -m 10 -X POST "%~1" -H "Content-Type: application/json" --data-binary "@%~2" > "%RESP_FILE%"
if errorlevel 1 (
  echo [ERROR] Timeout o fallo de conexion en POST a %~1
  exit /b 1
)
for /f "delims=" %%i in ('powershell -NoProfile -Command "$j=Get-Content -Raw -LiteralPath '%RESP_FILE%'; try { ($j | ConvertFrom-Json).id } catch { '' }"') do set "%~3=%%i"
call :require_numeric %~3
if errorlevel 1 (
  echo [ERROR] Fallo POST a %~1 - Respuesta invalida
  type "%RESP_FILE%"
  exit /b 1
)
exit /b 0

:expect_code
set "CODE_FILE=%TMP_DIR%\http_code.txt"
set "RESP_FILE=%TMP_DIR%\resp_code_test.json"
curl -s -m 10 -o "%RESP_FILE%" -w "%%{http_code}" -X %~1 "%~2" > "%CODE_FILE%"
set /p CODE=<"%CODE_FILE%"
if "%CODE%"=="%~3" exit /b 0
echo [ERROR] %~1 %~2 respondio %CODE% (esperado %~3)
type "%RESP_FILE%"
exit /b 1

:expect_code_with_body
set "CODE_FILE=%TMP_DIR%\http_code.txt"
set "RESP_FILE=%TMP_DIR%\resp_body_test.json"
curl -s -m 10 -o "%RESP_FILE%" -w "%%{http_code}" -X %~1 "%~2" -H "Content-Type: application/json" --data-binary "@%~4" > "%CODE_FILE%"
set /p CODE=<"%CODE_FILE%"
if "%CODE%"=="%~3" exit /b 0
echo [ERROR] %~1 %~2 respondio %CODE% (esperado %~3)
type "%RESP_FILE%"
exit /b 1

:require_numeric
set "VALUE=!%~1!"
set "VALUE=!VALUE: =!"
if not defined VALUE (
  echo [ERROR] %~1 vacio o invalido
  exit /b 1
)
echo(!VALUE!| findstr /r "^[0-9][0-9]*$" > nul
if errorlevel 1 (
  echo [ERROR] %~1 no es numerico: !VALUE!
  exit /b 1
)
set "%~1=!VALUE!"
exit /b 0

:fail
if exist "%TMP_DIR%" (
  rd /s /q "%TMP_DIR%" > nul 2>&1
  if errorlevel 1 echo [WARNING] No se pudo limpiar directorio temporal %TMP_DIR%
)
echo.
echo [FAIL] Pruebas E2E fallaron.
exit /b 1

:main
set "BASE_URL=http://localhost:8080/api"
set "RUN_ID=%RANDOM%%RANDOM%"
set "TMP_DIR=%TEMP%\e2e_%RUN_ID%"

set "CATEGORY_ID="
set "PRODUCT_OK_ID="
set "PRODUCT_LOW_ID="
set "CUSTOMER_ID="
set "ADDRESS_ID="
set "ORDER_OK_ID="
set "ORDER_LOW_ID="
set "ORDER_CANCEL_ID="

echo ==========================================
echo PRUEBAS E2E - TIENDA UNIVERSITARIA
echo Run ID: %RUN_ID%
echo ==========================================

curl -s -m 10 "%BASE_URL%/categories" > nul 2>&1
if errorlevel 1 (
  echo [ERROR] API no disponible en %BASE_URL%
  exit /b 1
)

if not exist "%TMP_DIR%" mkdir "%TMP_DIR%"

set "CATEGORY_NAME=E2E_%RUN_ID%_CAT"
set "SKU_OK=E2E-%RUN_ID%-OK"
set "SKU_LOW=E2E-%RUN_ID%-LOW"
set "EMAIL=e2e_%RUN_ID%@example.com"

echo [1/12] Crear categoria...
> "%TMP_DIR%\category.json" echo {"name":"%CATEGORY_NAME%","description":"Categoria E2E"}
call :post_and_get_id "%BASE_URL%/categories" "%TMP_DIR%\category.json" CATEGORY_ID
if errorlevel 1 goto :fail

echo [2/12] Crear producto con stock normal...
> "%TMP_DIR%\product_ok.json" echo {"sku":"%SKU_OK%","name":"Producto OK %RUN_ID%","description":"Flujo feliz","price":100.00,"active":true,"categoryId":%CATEGORY_ID%}
set "RESP_FILE=%TMP_DIR%\resp_product_ok.json"
curl -s -X POST "%BASE_URL%/products/with-inventory?initialStock=30&minimumStock=5" -H "Content-Type: application/json" --data-binary "@%TMP_DIR%\product_ok.json" > "%RESP_FILE%"
for /f "delims=" %%i in ('powershell -NoProfile -Command "$j=Get-Content -Raw -LiteralPath '%RESP_FILE%'; try { ($j | ConvertFrom-Json).id } catch { '' }"') do set "PRODUCT_OK_ID=%%i"
call :require_numeric PRODUCT_OK_ID
if errorlevel 1 (
  echo [ERROR] Fallo POST a %BASE_URL%/products/with-inventory?initialStock=30^&minimumStock=5
  type "%RESP_FILE%"
  goto :fail
)

echo [3/12] Crear producto con stock bajo...
> "%TMP_DIR%\product_low.json" echo {"sku":"%SKU_LOW%","name":"Producto LOW %RUN_ID%","description":"Rechazo pago","price":50.00,"active":true,"categoryId":%CATEGORY_ID%}
set "RESP_FILE=%TMP_DIR%\resp_product_low.json"
curl -s -X POST "%BASE_URL%/products/with-inventory?initialStock=1&minimumStock=1" -H "Content-Type: application/json" --data-binary "@%TMP_DIR%\product_low.json" > "%RESP_FILE%"
for /f "delims=" %%i in ('powershell -NoProfile -Command "$j=Get-Content -Raw -LiteralPath '%RESP_FILE%'; try { ($j | ConvertFrom-Json).id } catch { '' }"') do set "PRODUCT_LOW_ID=%%i"
call :require_numeric PRODUCT_LOW_ID
if errorlevel 1 (
  echo [ERROR] Fallo POST a %BASE_URL%/products/with-inventory?initialStock=1^&minimumStock=1
  type "%RESP_FILE%"
  goto :fail
)

echo [4/12] Crear cliente...
> "%TMP_DIR%\customer.json" echo {"firstName":"E2E","lastName":"Runner","email":"%EMAIL%","status":"ACTIVE"}
call :post_and_get_id "%BASE_URL%/customers" "%TMP_DIR%\customer.json" CUSTOMER_ID
if errorlevel 1 goto :fail

echo [5/12] Crear direccion...
> "%TMP_DIR%\address.json" echo {"street":"Calle E2E","city":"Santa Marta","department":"Magdalena","postalCode":"470001","customerId":%CUSTOMER_ID%,"isDefault":true}
call :post_and_get_id "%BASE_URL%/customers/%CUSTOMER_ID%/addresses" "%TMP_DIR%\address.json" ADDRESS_ID
if errorlevel 1 goto :fail

echo [6/12] Flujo feliz: crear pedido...
> "%TMP_DIR%\order_ok.json" echo {"status":"CREATED","customerId":%CUSTOMER_ID%,"addressId":%ADDRESS_ID%,"items":[{"productId":%PRODUCT_OK_ID%,"quantity":2}]}
call :post_and_get_id "%BASE_URL%/orders" "%TMP_DIR%\order_ok.json" ORDER_OK_ID
if errorlevel 1 goto :fail
call :expect_code PUT "%BASE_URL%/orders/%ORDER_OK_ID%/pay" 200
if errorlevel 1 goto :fail
call :expect_code PUT "%BASE_URL%/orders/%ORDER_OK_ID%/ship" 200
if errorlevel 1 goto :fail
call :expect_code PUT "%BASE_URL%/orders/%ORDER_OK_ID%/deliver" 200
if errorlevel 1 goto :fail
call :expect_code GET "%BASE_URL%/orders/%ORDER_OK_ID%/history" 200
if errorlevel 1 goto :fail

echo [7/12] Rechazo de pago por stock insuficiente...
> "%TMP_DIR%\order_low.json" echo {"status":"CREATED","customerId":%CUSTOMER_ID%,"addressId":%ADDRESS_ID%,"items":[{"productId":%PRODUCT_LOW_ID%,"quantity":3}]}
call :post_and_get_id "%BASE_URL%/orders" "%TMP_DIR%\order_low.json" ORDER_LOW_ID
if errorlevel 1 goto :fail
call :expect_code PUT "%BASE_URL%/orders/%ORDER_LOW_ID%/pay" 400
if errorlevel 1 goto :fail

echo [8/12] Cancelacion de pagado...
> "%TMP_DIR%\order_cancel.json" echo {"status":"CREATED","customerId":%CUSTOMER_ID%,"addressId":%ADDRESS_ID%,"items":[{"productId":%PRODUCT_OK_ID%,"quantity":1}]}
call :post_and_get_id "%BASE_URL%/orders" "%TMP_DIR%\order_cancel.json" ORDER_CANCEL_ID
if errorlevel 1 goto :fail
call :expect_code PUT "%BASE_URL%/orders/%ORDER_CANCEL_ID%/pay" 200
if errorlevel 1 goto :fail
call :expect_code PUT "%BASE_URL%/orders/%ORDER_CANCEL_ID%/cancel" 200
if errorlevel 1 goto :fail

echo [9/12] Reportes...
call :expect_code GET "%BASE_URL%/reports/low-stock-products" 200
if errorlevel 1 goto :fail

set "START_DATE=2026-01-01T00:00:00"
set "END_DATE=2026-12-31T23:59:59"

call :expect_code GET "%BASE_URL%/reports/best-selling-products?startDate=%START_DATE%&endDate=%END_DATE%" 200
if errorlevel 1 goto :fail

call :expect_code GET "%BASE_URL%/reports/monthly-income?startDate=%START_DATE%&endDate=%END_DATE%" 200
if errorlevel 1 goto :fail

call :expect_code GET "%BASE_URL%/reports/top-customers?startDate=%START_DATE%&endDate=%END_DATE%" 200
if errorlevel 1 goto :fail

echo [10/12] Validacion: producto invalido (400)...
> "%TMP_DIR%\bad_product.json" echo {"sku":"BAD-%RUN_ID%","name":"","description":"x","price":-1,"active":true,"categoryId":%CATEGORY_ID%}
call :expect_code_with_body POST "%BASE_URL%/products" 400 "%TMP_DIR%\bad_product.json"
if errorlevel 1 goto :fail

echo [11/12] Validacion: cliente email invalido (400)...
> "%TMP_DIR%\bad_customer.json" echo {"firstName":"Bad","lastName":"Email","email":"bad-email","status":"ACTIVE"}
call :expect_code_with_body POST "%BASE_URL%/customers" 400 "%TMP_DIR%\bad_customer.json"
if errorlevel 1 goto :fail

echo [12/12] Validacion: orden sin items (400)...
> "%TMP_DIR%\bad_order.json" echo {"status":"CREATED","customerId":%CUSTOMER_ID%,"addressId":%ADDRESS_ID%,"items":[]}
call :expect_code_with_body POST "%BASE_URL%/orders" 400 "%TMP_DIR%\bad_order.json"
if errorlevel 1 goto :fail

echo [13/13] Actualizar producto (PUT)...
> "%TMP_DIR%\product_update.json" echo {"sku":"%SKU_OK%","name":"Producto OK Actualizado %RUN_ID%","description":"Flujo feliz actualizado","price":120.00,"active":true,"categoryId":%CATEGORY_ID%}
call :expect_code_with_body PUT "%BASE_URL%/products/%PRODUCT_OK_ID%" 200 "%TMP_DIR%\product_update.json"
if errorlevel 1 goto :fail

echo.
echo ==========================================
echo E2E completado OK
echo category=%CATEGORY_ID% productOK=%PRODUCT_OK_ID% productLOW=%PRODUCT_LOW_ID%
echo customer=%CUSTOMER_ID% address=%ADDRESS_ID%
echo orders=%ORDER_OK_ID%,%ORDER_LOW_ID%,%ORDER_CANCEL_ID%
echo ==========================================

if exist "%TMP_DIR%" (
  rd /s /q "%TMP_DIR%" > nul 2>&1
  if errorlevel 1 echo [WARNING] No se pudo limpiar directorio temporal %TMP_DIR%
)
exit /b 0
