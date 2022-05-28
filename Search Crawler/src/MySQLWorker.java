import java.sql.*;
import java.util.ArrayList;

public class MySQLWorker {

    private static final String url = "jdbc:mysql://localhost:3306/sr_crawled_webpages";
    private static final String user = "root";
    private static final String password = "";

    private static Connection con = null;

    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    public boolean saveToDB(WebPage webpage) {
        try {
            if (con == null) {
                con = DriverManager.getConnection(url, user, password);
            }
            String url = webpage.getUrl();
            String sql = "SELECT * FROM webpages WHERE url = '" + url + "'";
            if (getFromDB(sql).size() == 0) {
                sql = "INSERT INTO webpages (url, code, headers) Values (?, ?, ?)";
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, webpage.getUrl());
                preparedStatement.setString(2, webpage.getCode().toString());
                preparedStatement.setString(3, webpage.getHeaders().toString());
                int res = preparedStatement.executeUpdate();
                if (res > 0) {
                    return true;
                }
                return false;

            }
            else {
                sql = "UPDATE webpages SET code = ?, headers = ?, datetime = CURRENT_TIMESTAMP WHERE url = ?";
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, webpage.getCode().toString());
                preparedStatement.setString(2, webpage.getHeaders().toString());
                preparedStatement.setString(3, url);
                int res = preparedStatement.executeUpdate();

                if (res > 0) {
                    return true;
                }
                return false;

            }
            //System.out.println("URL LOADED IN DB.");
        } catch (SQLException e) {
//            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getFromDB(String sqlQuery) {
        ArrayList<String> result = new ArrayList<>();
        try {
            if (con == null) {
                con = DriverManager.getConnection(url, user, password);
            }
            statement = con.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
            String resultString;
            while (resultSet.next()) {
                resultString = "";
                resultString += resultSet.getInt("id") + ". ";
                resultString += resultSet.getString("url") + "\n";
                resultString += resultSet.getString("headers") + "\n";
                resultString += resultSet.getTimestamp("datetime") + "\n";
                result.add(resultString);
            }
            return result;
        } catch (SQLException e) {
//            e.printStackTrace();
//            System.out.println("Failed to connect to database.");
            return result;
        }
    }

    public void closeAllConnections() {
        try {
            if (con != null) {
                con.close();
                con = null;
            }
            if (statement != null) {
                statement.close();
                statement = null;
            }
            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;

            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
