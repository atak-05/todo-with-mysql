public class Conn {
    //JDBC driver and database URL
    private final String jdbcDriver = "com.mysql.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost/tododb?serverTimezone=UTC";
    // Database user and password
    final String username  = "root";
    final String password ="";

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public String getPassword(){
        return password;
    }

    public String getUser(){
        return username;
    }

    public String getUrl(){
        return url;
    }

}
