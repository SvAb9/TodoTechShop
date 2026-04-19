package todotech.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton de conexión a Oracle.
 * Garantiza una sola instancia de conexión en toda la aplicación.
 *
 * Uso: Connection con = ConexionDB.getInstance().getConexion();
 */
public class ConexionDB {

    // Configura estos datos según tu entorno Oracle
    private static final String URL      = "jdbc:oracle:thin:@localhost:1521/XEPDB1";
    private static final String USUARIO  = "todotech";
    private static final String PASSWORD = "todotech123";

    private static ConexionDB instancia;
    private Connection conexion;

    /** Constructor privado — nadie puede crear instancias desde afuera */
    private ConexionDB() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("Conexión a Oracle establecida correctamente.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver Oracle no encontrado. Agrega ojdbc al classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con Oracle: " + e.getMessage(), e);
        }
    }

    /** Retorna la única instancia. La crea si no existe todavía. */
    public static ConexionDB getInstance() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }

    public Connection getConexion() {
        try {
            // Reconecta si la conexión se cerró
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar conexión: " + e.getMessage(), e);
        }
        return conexion;
    }

    public void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                instancia = null;
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}
