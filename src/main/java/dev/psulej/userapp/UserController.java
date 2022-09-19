package dev.psulej.userapp;

import java.sql.*;
import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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

        String sql = "SELECT id, first_name, last_name FROM users ORDER BY id";
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

    private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable long id) {
        // TODO: Implement
        return null;
    }


    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody User newUser) {
        String sql = "INSERT INTO users(id, first_name, last_name) VALUES (nextval('users_seq'), :firstName, :lastName)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", newUser.firstName);
        parameters.put("lastName", newUser.lastName);
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters), key, new String[] { "id" });

        return new User(
                key.getKey().longValue(),
                newUser.firstName,
                newUser.lastName,
                "not-set",
                "not-set"
        );
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable long id, @RequestBody User existingUser) {
        String sql = "UPDATE users SET first_name = :firstName, last_name = :lastName WHERE id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", existingUser.firstName);
        parameters.put("lastName", existingUser.lastName);
        parameters.put("id",id);
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters));
        return new User(
                id,
                existingUser.firstName,
                existingUser.lastName,
                "not-set",
                "not-set"
        );
    }

    @RequestMapping(value = "/users/{id}" , method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long id){
        String sql = "DELETE FROM users WHERE id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id",id);
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters));
    }
}
