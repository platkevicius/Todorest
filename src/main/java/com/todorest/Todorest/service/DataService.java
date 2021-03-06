package com.todorest.Todorest.service;

import com.todorest.Todorest.model.Item;
import com.todorest.Todorest.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class DataService {

    public DataSource dataSource() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/todolist?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=EST");
        ds.setUsername("root");
        ds.setPassword("");
        return ds;
    }

    private JdbcTemplate jdbcTemplate;

    public DataService() {
        try {
            jdbcTemplate = new JdbcTemplate(dataSource());
        } catch (Exception e) {
            e.printStackTrace();
        }

        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (name VARCHAR(20), pw text, email text)");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS list (name VARCHAR(20), item text)");
    }


    // Methods for login and register
    public int register(User user) {
        String sql = "INSERT INTO users (name, email, pw)" +
                "VALUES (" + "'" + user.getUsername() + "'" + ", " + "'" + user.getEmail() + "'" + ", " + "'" + user.getPassword() + "'" + ")";

        User registerUser = getUserByName(user.getUsername());
        if (!registerUser.getUsername().equals("")) {
            return 0; // unsucessful
        }

        if (user.getUsername() != null && user.getEmail() != null && user.getPassword() != null) {
            jdbcTemplate.execute(sql);
            return 1;
        }

        return 0; // unsucessful
    }

    public User getUserByName(String name) {
        String sql = "SELECT *  FROM users WHERE name = ?";

        List<User> result = jdbcTemplate.query(sql, new Object[]{name},
                new RowMapper<User>() {
                    @Override
                    public User mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new User(resultSet.getString("name"),
                                resultSet.getString("email"),
                                resultSet.getString("pw"));
                    }
                });

        if (result.size() == 0) {
            return new User("", "", "");
        } else {
            return result.get(0);
        }
    }

    // Methods for adding to todolist, removing and loading
    public int addItem(Item item) {
        String sql = "INSERT INTO list (name, item) VALUES (" + "'" + item.getName() + "'" + ", " + "'" + item.getItem() + "'" + ")";

        jdbcTemplate.execute(sql);
        return 1;
    }

    public int removeItem(Item item) {
       String sql = "DELETE FROM list WHERE (name = " + "'" + item.getName() + "' && item = " + "'" + item.getItem() + "')";

       jdbcTemplate.execute(sql);
       return 1;
    }

    public List<String> getTodoList(String name) {
        String sql = "SELECT list.item FROM users, list WHERE (users.name = list.name) && (users.name = " + "'" + name + "'" + ");";

        List<String> todolist = jdbcTemplate.queryForList(sql, String.class);
        return todolist;
    }

}
