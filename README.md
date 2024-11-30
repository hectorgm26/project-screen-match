# ScreenMatch Series Manager 🎬

## Descripción

**ScreenMatch Series Manager** es una aplicación Java que permite a los usuarios buscar, almacenar y gestionar series y episodios utilizando la API de [OMDb](https://www.omdbapi.com/). Se enfoca en la organización de datos de series, evaluaciones y episodios, y utiliza Spring Data JPA para persistencia en base de datos.

## Características

1. **Búsqueda de series** desde la API de OMDb.
2. **Búsqueda de episodios** por título o serie.
3. **Top 5 series mejor valoradas.**
4. **Filtrado** por género, temporadas o evaluación.
5. **Persistencia de datos** con Spring Data JPA.

## Tecnologías

- Java 17+
- Spring Boot
- Spring Data JPA
- Hibernate (ORM)
- Jakarta Persistence API
- OMDb API (para datos de series)
- PostgreSQL o cualquier base de datos compatible con JPA

## Estructura del Proyecto

El proyecto sigue una arquitectura orientada a objetos y utiliza buenas prácticas como la inyección de dependencias y el uso de `Optional` para evitar `null`.

### Paquetes principales

- `com.aluracursos.screenmatch.principal`  
  Contiene la clase principal `Principal`, que maneja la lógica del menú y las operaciones principales.

- `com.aluracursos.screenmatch.model`  
  Modelos JPA que representan las entidades `Serie`, `Episodio` y registros de datos como `DatosSerie`, `DatosEpisodio`.

- `com.aluracursos.screenmatch.service`  
  Servicios que manejan la interacción con la API externa y la conversión de datos JSON a objetos Java.

- `com.aluracursos.screenmatch.repository`  
  Interfaces para la persistencia de datos usando Spring Data JPA.

## Funcionalidades del Menú

### 1. Buscar series  
Permite buscar series por título en la API de OMDb y guardarlas en la base de datos local.

### 2. Buscar episodios  
Muestra los episodios de una serie buscada y los almacena en la base de datos.

### 3. Mostrar series buscadas  
Muestra todas las series almacenadas ordenadas por género.

### 4. Buscar series por título  
Busca series almacenadas por coincidencia parcial en el título.

### 5. Top 5 series mejor valoradas  
Muestra las 5 series mejor evaluadas de la base de datos.

### 6. Buscar series por género/categoría  
Permite filtrar series por género, como Acción, Comedia, Drama, etc.

### 7. Filtrar series por temporadas y evaluación mínima  
Filtra las series según el número de temporadas y una evaluación mínima.

### 8. Buscar episodios por título  
Busca episodios por título dentro de las series almacenadas.

### 9. Top 5 episodios mejor valorados por serie  
Muestra los 5 episodios mejor valorados de una serie específica.

## Configuración del Proyecto

### Dependencias Maven
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

### Configuración de application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/screenmatch
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

### API Key de OMDb
Debes reemplazar INSERTAR_TU_API_KEY_AQUI con tu clave de la API en la clase Principal.

## Ejecución del Proyecto

Clona el repositorio.
```bash
git clone https://github.com/tu_usuario/screenmatch.git
```

Configura la base de datos en application.properties.

Ejecuta la aplicación desde tu IDE o con:
```bash
mvn spring-boot:run
```

## Uso de Variables de Entorno

Para proteger credenciales sensibles, como la clave de la API de OMDb y la configuración de la base de datos, se utilizan variables de entorno en el archivo `application.properties`. Esto evita que datos confidenciales se almacenen directamente en el código.

### Configuración en `application.properties`

```properties
# OMDb API Key
omdb.api.key=${OMDB_API_KEY}

# Configuración de la base de datos
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Variables de Entorno Requeridas

* `OMDB_API_KEY`: Clave de acceso a la API de OMDb.
* `DATABASE_URL`: URL de la base de datos (ejemplo: `jdbc:mysql://localhost:3306/screenmatch`).
* `DATABASE_USERNAME`: Nombre de usuario de la base de datos.
* `DATABASE_PASSWORD`: Contraseña de la base de datos.

### Configuración de Variables de Entorno

En Linux/macOS:
```bash
export OMDB_API_KEY=tu_api_key
export DATABASE_URL=jdbc:mysql://localhost:3306/screenmatch
export DATABASE_USERNAME=root
export DATABASE_PASSWORD=tu_contraseña
```

En Windows (PowerShell):
```powershell
$env:OMDB_API_KEY="tu_api_key"
$env:DATABASE_URL="jdbc:mysql://localhost:3306/screenmatch"
$env:DATABASE_USERNAME="root"
$env:DATABASE_PASSWORD="tu_contraseña"
```

### Conexión con la Base de Datos

#### Configuración de Spring Data JPA

Spring Boot configura automáticamente la conexión a la base de datos a través de las propiedades definidas en `application.properties`. Utiliza **Spring Data JPA** para la comunicación con la base de datos, mapeando las entidades `Serie` y `Episodio` a tablas correspondientes.

#### Entidades

`Serie.java`  
Representa una serie con sus atributos principales como título, género, temporadas y evaluación.

`Episodio.java`  
Representa un episodio de una serie, incluyendo título, temporada, número de episodio y evaluación.

#### Repositorios

Spring Data JPA gestiona la persistencia mediante **repositorios**, como `SerieRepository`, que proporciona métodos personalizados para consultas como `findTop5ByOrderByEvaluacionDesc()`.

## Autor
Desarrollado por [Tu Nombre] como parte de un curso de Alura.

## Licencia
Este proyecto está bajo la licencia MIT. Consulta el archivo LICENSE para más detalles.
