import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.TreeSet;

public class ListProductColumns {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/avnzor?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection connection = DriverManager.getConnection(url, "root", "");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT COLUMN_NAME FROM information_schema.COLUMNS "
                             + "WHERE TABLE_SCHEMA = 'avnzor' AND TABLE_NAME = 'sma_products' "
                             + "ORDER BY ORDINAL_POSITION")) {
            TreeSet<String> columns = new TreeSet<>();
            while (rs.next()) {
                columns.add(rs.getString(1));
            }
            System.out.println("Column count: " + columns.size());
            System.out.println("Has rrp: " + columns.contains("rrp"));
            columns.forEach(System.out::println);
        }
    }
}
