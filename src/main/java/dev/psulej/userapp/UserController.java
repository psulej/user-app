package dev.psulej.userapp;

import java.sql.*;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public UserController(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<BasicUser> getUsers(
        @RequestParam(value = "firstName", required = false) String firstName,
        @RequestParam(value = "lastName", required = false) String lastName,
        @RequestParam(value = "sort", defaultValue = "id") String sort) {

        String sql = "SELECT id, first_name, last_name, login, email FROM users ORDER BY id";
        Map<String, Object> parameters = new HashMap<>();

        RowMapper<BasicUser> rowMapper = new RowMapper<>() {
            @Override
            public BasicUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                return new BasicUser(id, firstName, lastName);
            }
        };
        return jdbcTemplate.query(
                sql, parameters, rowMapper
        );
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable long id) {
        String sql = "SELECT id, first_name, last_name, login, email FROM users WHERE id = :id";

        Map<String,Object> parameters = new HashMap<>();
        parameters.put("id",id);

        RowMapper<User> userRowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String login = rs.getString("login");
                String email = rs.getString("email");
                return new User(id,firstName,lastName,login,email,null);
            }
        };

        User user = jdbcTemplate.queryForObject(sql, parameters, userRowMapper);
        return user;
    }


    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody User newUser) {
        long userId = insertUser(newUser);
        String sql = "INSERT INTO user_addresses(user_id, country, city, street, house_number, zip_code) " +
                "VALUES(:id, :country, :city, :street, :houseNumber, :zipCode)";
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put("id",userId);
        Address address = newUser.address;
        parameters.put("country", address.country);
        parameters.put("city", address.city);
        parameters.put("street", address.street);
        parameters.put("houseNumber", address.houseNumber);
        parameters.put("zipCode", address.zipCode);

        jdbcTemplate.update(sql, parameters);

        return new User(
                userId,
                newUser.firstName,
                newUser.lastName,
                newUser.login,
                newUser.email,
                address
        );
    }

    private long insertUser(User newUser) {
        String sql =
                "INSERT INTO users(id, first_name, last_name, login, email)" +
                " VALUES (nextval('users_seq'), :firstName, :lastName, :login, :email)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", newUser.firstName);
        parameters.put("lastName", newUser.lastName);
        parameters.put("login" , newUser.login);
        parameters.put("email" , newUser.email);
        KeyHolder key = new GeneratedKeyHolder(); // zwraca id dla usera
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters), key, new String[] { "id" });
        long userId = key.getKey().longValue();
        return userId;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable long id, @RequestBody User existingUser) {
        String sql = "UPDATE users SET first_name = :firstName, last_name = :lastName, login = :login, email = :email WHERE id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", existingUser.firstName);
        parameters.put("lastName", existingUser.lastName);
        parameters.put("id",id);
        parameters.put("email", existingUser.email);
        parameters.put("login", existingUser.login);
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters));
        return new User(
                id,
                existingUser.firstName,
                existingUser.lastName,
                existingUser.login,
                existingUser.email,
                null
        );
    }

    @RequestMapping(value = "/users/{id}" , method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long id){
        deleteUserAddresses(id);
        deleteUsers(id);
    }

    private void deleteUserAddresses(long id) {
        String sql = "DELETE FROM user_addresses WHERE user_id = :userId";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("userId", id);
        jdbcTemplate.update(sql, parameters);
    }

    private void deleteUsers(long id) {
        String sql = "DELETE FROM users WHERE id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        jdbcTemplate.update(sql, parameters);
    }
}
