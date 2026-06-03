# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.12/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.12/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.12/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.12/reference/data/sql.html#data.sql.jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

---
./src/main/java/com/ccsw/tutorial/


## Modificaciones respecto al tutorial

### General
- Manejo de *isDeleteable* mediante su propio DTO. [DeleteCheck](./src/main/java/com/ccsw/tutorial/common/deleteCheck/)
- Manejo de errores mediante un **GlobalExceptionHandler** y *Excepciones propias* para los distintos errores según las Reglas de Negocio. [ExceptionHandler](./src/main/java/com/ccsw/tutorial/exceptions/) 
- Para los filtros, se genereliza el *GameSpecification* a un *GenericSpecification*. [Criteria](./src/main/java/com/ccsw/tutorial/common/criteria/).
  
### Autor
- Ninguna modificación.

### Categoría
- Ninguna modificación.

### Cliente
- Se implementan funciones en su *Repository* para los filtr os de **Préstamo**. [ClientRepository](./src/main/java/com/ccsw/tutorial/client/ClientRepository.java)

### Juego
- Se aplican un cambio de *Case* para hacer más preciso el filtro.
- Se implementan funciones en su *Repository* para los filtros de **Préstamo**. [GameRepository](./src/main/java/com/ccsw/tutorial/game/GameRepository.java)

### Préstamo
- **DTO's específicos** para transmitir la información necesaria a la hora de ver si un *Préstamo* es válido. [Avaliability](./src/main/java/com/ccsw/tutorial/loan/model/available/)
- **DTO's específicos** para transmitir la información necesaria a la hora de encontrar una **lista paginada** de *Préstamos* dados los *filtros* y la *paginación*. [PageFiltered](./src/main/java/com/ccsw/tutorial/loan/model/filter/)
- **Specifiaction** para los intervalos de Fechas.
- Endpoint para revisar si, dados los filtros, el *Préstamo* es válido.
- Endpoint específico para obtener el último *id* de cara al *UX*.

### Otros
- Implementación de un **Interceptor** y su consecuente *SecurityConfig* para revisar el token válido en las peticiones entrantres. [Interceptor](./src/main/java/com/ccsw/tutorial/security/)
- Añadidas las **Rutas Públicas** (rutas que no necesitan token) y una clase suya propia.
- Implementación de la seguridad con token JWT. [Token](./src/main/java/com/ccsw/tutorial/tokenAuth/tokenAuthController.java)
- Implementación de la lógica de **Inicio de Sesión** y **Registro** de usuarios, con *Contraeñas Hasheadas*. [UserAuthentication](./src/main/java/com/ccsw/tutorial/userAuth/)
