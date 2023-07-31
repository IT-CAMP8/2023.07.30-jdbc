package pl.camp.it.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class App {
    public static Connection connection;
    public static PreparedStatement insertStatement;
    public static void main(String[] args) {
        connect();
        /*User user = new User(0, "Janusz", "Malinowski", "janusz_inny", "janusz123", 50);
        saveUser2(user);
        System.out.println(user);*/
        //System.out.println(getUserById(3));
        //System.out.println(getAllUsers());
        /*List<User> users = getAllUsers();
        users.get(0).setName("Mieczyslaw");
        updateUser(users.get(0));*/
        deleteUser(2);
        System.out.println(getAllUsers());
        disconnect();
    }

    public static void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
            insertStatement = connection.prepareStatement(
                    "INSERT INTO tuser (name, surname, login, password, age) VALUES (?,?,?,?,?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("cos nie pyklo !!!");
            e.printStackTrace();
        }
    }

    public static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveUser(User user) {
        try {
            String sql = new StringBuilder("INSERT INTO tuser (name, surname, login, password, age) VALUES ('")
                    .append(user.getName()).append("','")
                    .append(user.getSurname()).append("','")
                    .append(user.getLogin()).append("','")
                    .append(user.getPassword()).append("',")
                    .append(user.getAge())
                    .append(");").toString();

            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveUser2(User user) {
        try {
            /*String sql = "INSERT INTO tuser (name, surname, login, password, age) VALUES (?,?,?,?,?)";
            PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);*/
            insertStatement.setString(1, user.getName());
            insertStatement.setString(2, user.getSurname());
            insertStatement.setString(3, user.getLogin());
            insertStatement.setString(4, user.getPassword());
            insertStatement.setInt(5, user.getAge());
            insertStatement.executeUpdate();

            ResultSet rs = insertStatement.getGeneratedKeys();
            rs.next();
            user.setId(rs.getInt(1));
            insertStatement.clearParameters();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<User> getUserById(int id) {
        try {
            String sql = "SELECT * FROM tuser WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return Optional.of(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getInt("age")));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        try {
            String sql = "SELECT * FROM tuser";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                result.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("login"),
                        rs.getString("password"),
                        rs.getInt("age")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public static void updateUser(User user) {
        try {
            String sql = "UPDATE tuser SET name = ?, surname = ?, login = ?, password = ?, age = ? WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getLogin());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getAge());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteUser(int id) {
        try {
            String sql = "DELETE FROM tuser WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
