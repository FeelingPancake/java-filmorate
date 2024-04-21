package ru.yandex.practicum.filmorate.storage.database.Dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfacesDao.MpaDao;

import java.util.List;
import java.util.Map;

@Repository
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Mpa get(Long id) {
        String sql = "SELECT * FROM film_age_ratings WHERE rating_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql,
                    (rs, rn) -> new Mpa(rs.getLong("rating_id"),
                            rs.getString("rating_name")), id);
        } catch (DataAccessException e) {
            throw new NotFoundException(sql);
        }

    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM film_age_ratings";
        return jdbcTemplate.query(sql,
                ((rs, rowNum) -> new Mpa(rs.getLong("rating_id"), rs.getString("rating_name"))));
    }

    @Override
    public Long add(String name) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_age_ratings")
                .usingGeneratedKeyColumns("rating_id");

        return simpleJdbcInsert.executeAndReturnKey(Map.of("rating_name", name)).longValue();

    }
}
