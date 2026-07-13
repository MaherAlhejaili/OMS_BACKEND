import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ListKeyColumns {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/avnzor?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection c = DriverManager.getConnection(url, "root", "");
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS "
                             + "WHERE TABLE_SCHEMA='avnzor' AND COLUMN_NAME IN ('id','sale_id','updated_by','total_items') "
                             + "ORDER BY TABLE_NAME, COLUMN_NAME")) {
            while (rs.next()) {
                System.out.printf("%s.%s = %s%n", rs.getString(1), rs.getString(2), rs.getString(3));
            }
        }
    }
}
