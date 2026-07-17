# Gator Lib

Biblioteca Java compartida por el ecosistema Gator. Contiene acceso a bases de
datos, modelos de respuesta, criptografía, traducción, códigos de barras,
sesiones, cookies e integración con impresión.

## Requisitos

- JDK 21
- `gator-lib-utils` en un directorio hermano
- Acceso a Maven Central durante la primera compilación

## Compilación y pruebas

```bash
./gradlew clean build
```

Por defecto Gradle incluye `../gator-lib-utils` como build compuesto. Puede
indicarse otra ubicación:

```bash
./gradlew clean build -PgatorLibUtilsDir=/ruta/gator-lib-utils
```

El JAR se genera en `build/libs/gator-lib-1.0.0-SNAPSHOT.jar`.

## Módulos principales

- `gator.lib.db`: acceso JDBC para PostgreSQL, Oracle y MySQL.
- `gator.lib.db.helpers`: sentencias, respuestas y modelos para endpoints.
- `gator.lib.sec`: cifrado AES/RSA, sesiones y autenticación.
- `gator.lib.i18`: traducciones basadas en gettext.
- `gator.lib.io.barcodes`: códigos QR y lineales.
- `gator.lib.net`: cookies, endpoints e impresión.

## Configuración sensible

El repositorio no contiene credenciales. La integración remota de llaves usa:

- `GATOR_KEYS_URL` o `-Dgator.keys.url=...`
- `GATOR_KEYS_API_KEY` o `-Dgator.keys.apiKey=...`

El almacén local alternativo usa:

- `GATOR_KEYSTORE_FILE` o `-Dgator.keystore.file=...`
- `GATOR_KEYSTORE_PASSWORD` o `-Dgator.keystore.password=...`
- `GATOR_KEYSTORE_ALIAS` o `-Dgator.keystore.alias=...`

No deben versionarse archivos de configuración, contraseñas, certificados ni
llaves privadas.

## Política de mantenimiento

Gator Lib se mantendrá y utilizará en los proyectos Gator actuales y futuros
porque ofrece APIs más ligeras y simples que sus alternativas estándar de Java.
Una funcionalidad solo se reemplazará cuando la biblioteca estándar sea mejor
en todos los aspectos relevantes, incluidos simplicidad, rendimiento,
compatibilidad, seguridad y mantenimiento.

## Contribuciones

Antes de enviar cambios ejecuta `./gradlew clean build`. Agrega una prueba para
cualquier corrección de lógica no trivial y evita dependencias innecesarias.

## Licencia

Gator Lib se distribuye bajo GNU GPL versión 3 o posterior. Algunos archivos
conservan avisos compatibles LGPL, Apache 2.0 y GPL versión 2 o posterior.
Consulta `LICENSE` y `NOTICE`.
