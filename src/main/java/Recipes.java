import recipes.dao.DbConnection;

import java.sql.Connection;

public class Recipes {
    public static void main(String[] args) {
        Connection connection = DbConnection.getConnection();
    }
}
