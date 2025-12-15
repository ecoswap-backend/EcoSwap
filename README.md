# EcoSwap Backend (Spring Boot API)
## Objetivos
EcoSwap Backend es una aplicación diseñada para soportar una plataforma de intercambio de ropa usada de forma sostenible, segura y escalable.
El objetivo principal de este backend es proporcionar una API RESTful robusta que gestione la autenticación de usuarios, la publicación e intercambio de prendas, el sistema de puntos y la reserva de artículos, promoviendo el consumo consciente y la moda circular.

La aplicación permite la gestión completa de usuarios, artículos y reservas, garantizando una experiencia segura y accesible tanto para visitantes como para usuarios registrados.

---
## Competencias Técnicas
- **Backend Development:** Implementación de la lógica del servidor y endpoints RESTful para la gestión de usuarios, artículos y reservas.
- **Database Components:** Diseño de entidades y repositorios para la persistencia de datos.
- **CRUD Implementation:** Desarrollo completo de operaciones CRUD para usuarios y artículos.
- **Autenticación y Autorización:** Implementación de seguridad basada en JWT.
- **Mappers & DTOs:** Uso de objetos de transferencia de datos para separar la lógica de negocio de la exposición de datos.

---
## Tecnologías
- **Lenguaje:** Java 21
- **Framework Backend:** Spring Boot 3.5.8
- **Seguridad:** Spring Security + JWT
- **Base de Datos:** PostgreSQL
- **Gestor de dependencias:**  Maven
- **Testing:** Junit 5, Mockito
- **Control de Versiones:** Git/GitHub
- **Pruebas de Api:** Postman
  
---
## Herramientas
- IntelliJ IDEA / Visual Studio Code
- Trello (gestión ágil del proyecto)
- Postman (pruebas y desarrollo de la API)

---
## Funcionalidades Principales
El backend de EcoSwap implementa las funcionalidades esenciales para el intercambio de prendas de manera segura y controlada.

---
## Autenticación
- **Registro de Usuarios:** Permite crear nuevas cuentas de usuario.
- **Inicio de Sesión:** Autentica al usuario y genera un token JWT.
- **Acceso Autorizado:** Los endpoints protegidos requieren un token JWT válido.
- **Cierre de Sesión:** Manejado desde el cliente invalidando el token.

---
## Gestión de Usuarios
- Visualización del perfil del usuario autenticado.
- Edición de datos personales (nombre, correo, contraseña, imagen).
- Gestión de puntos obtenidos por intercambios.

---
## Gestión de Artículos (CRUD)
- **Crear Artículo (POST):** Permite al usuario publicar una prenda.
- **Listar Artículos (GET):** Retorna artículos públicos con paginación y filtros.
- **Actualizar Artículo (PUT):** Permite modificar un artículo propio.
- **Eliminar Artículo (DELETE):** Permite eliminar artículos propios.

---
## Reservas e Intercambios
- **Reserva de Artículo:** Un usuario puede reservar una prenda disponible.
- **Cancelación de Reserva:** Permite liberar un artículo reservado.
- **Restricción de Reserva:** Un artículo solo puede ser reservado por un usuario a la vez.
- **Aceptación de Reserva:** El dueño del artículo confirma la entrega.
- **Sistema de Puntos:** Al marcar un artículo como entregado, el sistema asigna puntos automáticamente al usuario que entrega la prenda.

---
## Relaciones de la Base de Datos
- **Usuario → Artículos (1:N):** Un usuario puede crear múltiples artículos.
- **Usuario → Reservas (1:N):** Un usuario puede realizar varias reservas.
- **Artículo → Usuario (N:1):** Cada artículo pertenece a un único usuario.
- **Artículo → Reserva (1:1):** Un artículo solo puede tener una reserva activa.

---
## Validaciones y Seguridad
- **Propiedad del Artículo:** Un usuario solo puede editar o eliminar sus propios artículos.
- **Control de Reservas:** No se permite doble reserva de un mismo artículo.
- **Validación de Datos:** Campos obligatorios (título, descripción, categoría).
- **Seguridad:** Contraseñas almacenadas mediante hashing.
- **Autorización:** Acceso a recursos restringido según el usuario autenticado.

---
## Cómo Iniciar el Proyecto
### Requisitos Previos
- **Java 21** instalado
- **PostgreSQL** instalado y en ejecución
- **Maven** configurado

---
## Pasos para Iniciar
### 1. Clonar el repositorio
```bash
git clone https://github.com/EcoSwap/EcoSwap-Backend.git
cd EcoSwap-Backend
```
### 2. Configurar la base de datos
```sql
CREATE DATABASE ecoswap;
```
---
### 3. Configurar el archivo application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ecoswap
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
jwt.secret=tu_clave_secreta
jwt.expiration=86400000
```
### 4. Ejecutar el proyecto
**Linux / Mac**
```bash
./mvnw spring-boot:run
```
**Windows**
```bash
mvnw.cmd spring-boot:run
```
### 5. Verificar la instalación
- La API estará disponible en: **👉 http://localhost:8080**

---
## Desarrolladora 
| Nombre | GitHub | LinkedIn |
|--------|--------|----------|
| **Sofia Toro** | [@sofiatoroviafara01](https://github.com/sofiatoroviafara01) | [Sofía Toro Viafara](https://www.linkedin.com/in/sof%C3%ADa-toro-viafara-690124356/) |

---
