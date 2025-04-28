package orientacion_objetos.modelo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class databaseConexion {
        private static final String URL = "jdbc:mysql://localhost:3306/Reservas";
        private static final String USER = "root";
        private static final String PASS = "admin";

        public static Connection getConnection() throws SQLException {
            try {
                return DriverManager.getConnection(URL, USER, PASS);
            } catch (SQLException e) {
                System.err.println("Error de conexión a la base de datos: " + e.getMessage());
                e.printStackTrace();
                throw e; // Re-lanzar la excepción para que pueda ser capturada en el punto de llamada
            }
        }

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("¡Conexión exitosa a la base de datos!");
            System.out.println("URL: " + URL);
            System.out.println("Usuario: " + USER);
            return true;
        } catch (SQLException e) {
            System.err.println("Error en la conexión:");
            e.printStackTrace();
            return false;
        }
    }
    public static void pruebaConexionDirecta() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS)) {
            System.out.println("Conexión a la base de datos exitosa");
        } catch (SQLException e) {
            System.err.println("Error al conectar directamente a la base de datos: " + e.getMessage());
        }


}
}


