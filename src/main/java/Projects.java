import recipes.dao.DbConnection;

import java.sql.Connection;

public class Projects {
    public static void main(String[] args) {
        Connection connection = DbConnection.getConnection();
    }
}
