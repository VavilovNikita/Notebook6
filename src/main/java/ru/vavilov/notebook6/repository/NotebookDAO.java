package ru.vavilov.notebook6.repository;

import org.springframework.stereotype.Component;
import ru.vavilov.notebook6.entity.Notebook;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotebookDAO {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USERNAME = "user";
    private static final String PASSWORD = "1234";

    private static Connection connection;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void update(int id, Notebook notebook) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE notebook SET head=?, text=? WHERE id=?");
            preparedStatement.setString(1, notebook.getHead());
            preparedStatement.setString(2, notebook.getText());
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Notebook> index() {
        List<Notebook> notebook = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM notebook");
            while (resultSet.next()){
                Notebook notebook1 = new Notebook();
                notebook1.setId(resultSet.getInt("id"));
                notebook1.setHead(resultSet.getString("head"));
                notebook1.setText(resultSet.getString("text"));
                notebook.add(notebook1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notebook;
    }

    public Notebook show(int id) {
        Notebook notebook;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM notebook WHERE id=?");
            preparedStatement.setInt(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            notebook = new Notebook(resultSet.getInt("id"),resultSet.getString("head"),resultSet.getString("text"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notebook;
    }

    public void save(Notebook notebook) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO notebook(head, text) VALUES(?,?)");
            preparedStatement.setString(1, notebook.getHead());
            preparedStatement.setString(2, notebook.getText());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM notebook WHERE id=?");
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
