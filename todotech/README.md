# TodoTech Shop — Guía de configuración y ejecución

## Requisitos previos
- Java 17 o superior
- Oracle Database XE (Express Edition) — gratis
- SQL Developer (para ejecutar el script)
- VS Code con extensión "Extension Pack for Java"
- ojdbc17.jar (driver Oracle)

---

## Paso 1 — Instalar Oracle XE

Descarga Oracle Database 21c XE desde:
https://www.oracle.com/database/technologies/xe-downloads.html

Durante la instalación te pedirá una contraseña para SYS/SYSTEM.
Guárdala, la necesitas en el paso 2.

---

## Paso 2 — Crear la base de datos

Abre SQL Developer y conéctate como:
- Usuario: system
- Contraseña: (la que pusiste al instalar)
- Host: localhost
- Port: 1521
- SID: xe

Ejecuta la primera parte del archivo `database.sql`:

```sql
CREATE USER todotech IDENTIFIED BY todotech123;
GRANT CONNECT, RESOURCE, DBA TO todotech;
GRANT UNLIMITED TABLESPACE TO todotech;
```

Luego desconéctate y crea una nueva conexión:
- Usuario: todotech
- Contraseña: todotech123
- Host: localhost
- Port: 1521
- SID: xe

Con esa conexión ejecuta el resto del archivo `database.sql`
(desde CREATE TABLE CLIENTES hasta el COMMIT final).

---

## Paso 3 — Agregar el driver Oracle al proyecto

1. Descarga ojdbc17.jar desde:
   https://www.oracle.com/database/technologies/appdev/jdbc-downloads.html
   
   O agrégalo con Maven agregando esto a pom.xml si usas Maven:
   ```xml
   <dependency>
     <groupId>com.oracle.database.jdbc</groupId>
     <artifactId>ojdbc17</artifactId>
     <version>21.9.0.0</version>
   </dependency>
   ```

2. Si NO usas Maven, crea la carpeta lib/ en la raíz del proyecto
   y copia ojdbc11.jar ahí:
   ```
   todotech/
   ├── lib/
   │   └── ojdbc17.jar
   ├── src/
   └── database.sql
   ```

---

## Paso 4 — Configurar la conexión en el código

Abre `src/main/java/todotech/config/ConexionDB.java`
y verifica estas líneas:

```java
private static final String URL      = "jdbc:oracle:thin:@localhost:1521:xe";
private static final String USUARIO  = "todotech";
private static final String PASSWORD = "todotech123";
```

Si tu Oracle usa un puerto o SID diferente, cámbialo aquí.

---

## Paso 5 — Compilar y ejecutar

### Sin Maven (manual):

**En Mac/Linux:**
```bash
cd todotech

# Compilar
javac -cp lib/ojdbc17.jar -d out $(find src -name "*.java")

# Ejecutar
java -cp out:lib/ojdbc17.jar todotech.vista.VentanaPrincipal
```

**En Windows (cmd):**
```cmd
cd todotech

# Compilar
javac -cp lib\ojdbc17.jar -d out src\main\java\todotech\config\*.java src\main\java\todotech\modelo\*.java src\main\java\todotech\observador\*.java src\main\java\todotech\estrategia\*.java src\main\java\todotech\repositorio\*.java src\main\java\todotech\servicio\*.java src\main\java\todotech\vista\*.java

# Ejecutar
java -cp out;lib\ojdbc17.jar todotech.vista.VentanaPrincipal
```

### Con VS Code:
1. Instala la extensión "Extension Pack for Java"
2. Abre la carpeta todotech/ en VS Code
3. Agrega ojdbc17.jar a Referenced Libraries en el panel de Java Projects
4. Clic derecho en VentanaPrincipal.java → Run Java

---

## Credenciales de prueba (datos precargados)

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | admin | admin123 |
| Vendedor | vendedor | vend123 |
| Cajero | cajero | caj123 |
| Despachador | despacho | desp123 |

## Clientes de prueba

| Identificación | Nombre |
|---------------|--------|
| 1001234567 | Juan Pérez |
| 1009876543 | María López |
| 1005551234 | Carlos Ruiz |

## Productos de prueba (6 productos con stock)

- PROD-001 Laptop Dell Inspiron 15 — $2.500.000 (stock: 10)
- PROD-002 Mouse Inalámbrico Logitech — $85.000 (stock: 50)
- PROD-003 Teclado Mecánico Redragon — $180.000 (stock: 25)
- PROD-004 Monitor Samsung 24" — $650.000 (stock: 8)
- PROD-005 Auriculares Sony WH-1000XM4 — $950.000 (stock: 15)
- PROD-006 Disco Duro Externo Seagate 1TB — $220.000 (stock: 30)

---

## Estructura del proyecto

```
todotech/
├── lib/
│   └── ojdbc17.jar          ← tú lo descargas
├── src/main/java/todotech/
│   ├── config/
│   │   └── ConexionDB.java  ← Singleton Oracle
│   ├── modelo/
│   │   ├── Cliente.java
│   │   ├── Producto.java
│   │   ├── OrdenVenta.java
│   │   ├── DetalleOrden.java
│   │   ├── EstadoOrden.java
│   │   └── RolUsuario.java
│   ├── estrategia/
│   │   ├── MetodoPago.java   ← interfaz Strategy
│   │   ├── PagoEfectivo.java
│   │   ├── PagoTarjeta.java
│   │   └── PagoCheque.java
│   ├── observador/
│   │   └── ObservadorOrden.java ← interfaz Observer
│   ├── repositorio/
│   │   ├── ClienteRepositorio.java
│   │   ├── ProductoRepositorio.java
│   │   ├── OrdenRepositorio.java
│   │   └── DevolucionRepositorio.java
│   ├── servicio/
│   │   ├── ClienteServicio.java
│   │   ├── OrdenServicio.java
│   │   └── DevolucionServicio.java
│   └── vista/
│       ├── VentanaPrincipal.java
│       ├── FormCliente.java    ← CRUD
│       ├── FormOrden.java      ← Crear orden
│       ├── FormPago.java       ← Procesar pago
│       └── FormDevolucion.java ← Devoluciones
├── database.sql               ← Script Oracle
└── README.md
```
