# Challenge App

Un microservicio sencillo en Spring Boot para realizar cálculos y registrar operaciones, respaldado por PostgreSQL, y empaquetado con Docker y Docker Compose.

---

## Tabla de Contenidos

* [Requisitos Previos](#requisitos-previos)
* [Configuración](#configuración)
* [Ejecución con Docker Compose](#ejecución-con-docker-compose)
* [Desarrollo Local](#desarrollo-local)
* [Colección de Postman](#colección-de-postman)
* [Imagen en DockerHub](#imagen-en-dockerhub)
* [Endpoints de la API](#endpoints-de-la-api)
* [Credenciales de Base de Datos](#credenciales-de-base-de-datos)
* [Licencia](#licencia)

---

## Requisitos Previos

* Docker y Docker Compose instalados
* Java 21 (para desarrollo local)
* Maven (para compilar localmente)
* Cliente de PostgreSQL (por ejemplo, DBeaver)

---

## Configuración

Las variables de entorno se definen en `docker-compose.yml` y se consumen en `application.yml`.

---

## Ejecución con Docker Compose

1. Clona este repositorio.
2. Desde la raíz del proyecto, ejecuta:
   docker-compose up -d

3. Verifica los contenedores en ejecución:
   docker ps

4. La API estará disponible en `http://localhost:8080`.

---

## Desarrollo Local

Si prefieres ejecutar la aplicación sin Docker:

1. Asegúrate de tener PostgreSQL instalado en tu máquina (puede usar puerto 5433 para evitar conflictos).
2. Ajusta las variables de entorno o edita `application.yml`:

   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/challenge
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=1234

3. Compila y ejecuta con Maven:

   mvn clean install
   mvn spring-boot:run

---

## Colección de Postman

Importa la carpeta `postman` para probar los endpoints de la API.

* Nombre de la colección: **RETO TENPO**

---

## Imagen en DockerHub

La imagen está publicada en Docker Hub:

[jeanpier39/challenge-app en DockerHub](https://hub.docker.com/r/jeanpier39/challenge-app/tags)

Para descargarla:
docker pull jeanpier39/challenge-app:1.0

---

## Endpoints de la API

| Método | Ruta                          | Descripción                                      |
| ------ | ----------------------------- | ------------------------------------------------ |
| POST   | `/api/calculate`              | Calcula la suma de `num1 + num2` con porcentaje. |
| GET    | `/api/logs?page={p}&size={s}` | Obtiene logs paginados (página y tamaño).        |

**Ejemplo de payload**:

```json
{
  "num1": 10,
  "num2": 20
}
```

---

## Credenciales de Base de Datos

* **Base en Docker**

    * URL: `jdbc:postgresql://postgres:5432/challenge`
    * Usuario: `user`
    * Contraseña: `pass`

* **Base local**

    * URL: `jdbc:postgresql://localhost:5433/challenge`
    * Usuario: `postgres`
    * Contraseña: `1234`

