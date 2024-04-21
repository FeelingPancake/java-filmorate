package ru.yandex.practicum.filmorate.integrityTests;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.database.Dao.*;
import ru.yandex.practicum.filmorate.storage.interfacesDao.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({GenreDaoImpl.class, MpaDaoImpl.class, LikeDaoImpl.class, FriendShipDaoImpl.class})
@AllArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindUserById() {
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        List<Genre> genreList = genreDao.get(1L);

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .build();
        FilmDaoImpl filmStorage = new FilmDaoImpl(jdbcTemplate);
        long id = filmStorage.add(newFilm);
        genreDao.add(id, genreList);
        List<Genre> genres = genreDao.get(id);
        Film savedFilm = filmStorage.get(id);


        assertThat(savedFilm).isNotNull().usingRecursiveComparison().isEqualTo(filmStorage.get(id));

        assertEquals(genreList, genres);
    }

    @Test
    public void testFindALlFilms() {
        FilmDao filmStorage = new FilmDaoImpl(jdbcTemplate);
        GenreDao genreDao = new GenreDaoImpl(jdbcTemplate);
        MpaDao mpaDao = new MpaDaoImpl(jdbcTemplate);
        List<Long> expected = new ArrayList<>();

        Long mpaid = mpaDao.add("pg");
        for (int i = 1; i < 11; i++) {
            List<Genre> genreList = new ArrayList<>();
            genreList.add(new Genre(2L, "Драма"));
            Film newFilm = Film.builder()
                    .id((long) i)
                    .mpa(mpaDao.get(mpaid))
                    .name("Крепкий орешек" + i)
                    .description("Брюс против Снегга")
                    .releaseDate(LocalDate.of(1990, 1, i))
                    .duration(130)
                    .build();
            Long id = filmStorage.add(newFilm);
            expected.add(id);
            genreDao.add(id, genreList);
        }
        List<Long> films = filmStorage.getAll().stream().map(Film::getId).toList();

        Assertions.assertIterableEquals(expected, films);

        for (int i = 0; i < films.size(); i++) {
            assertThat(films.get(i)).isNotNull().usingRecursiveComparison().isEqualTo(expected.get(i));
            assertEquals(new Genre(2L, "Драма"), genreDao.get(films.get(i)).get(0));
        }


    }

    @Test
    public void testUpdateFilm() {

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .build();


        FilmDaoImpl filmStorage = new FilmDaoImpl(jdbcTemplate);
        Long id = filmStorage.add(newFilm);
        Film updatedFilm = newFilm.toBuilder().id(id).name("Джон уик").build();
        Film oldFilm = filmStorage.get(id);
        filmStorage.update(updatedFilm);

        Film savedFilm = filmStorage.get(id);

        assertThat(oldFilm).isNotNull().usingRecursiveComparison().isNotEqualTo(savedFilm);

        assertSame(oldFilm.getId(), savedFilm.getId());
    }

    @Test
    public void testDeleteFilm() {
        GenreDaoImpl genreDao = new GenreDaoImpl(jdbcTemplate);
        List<Genre> genreList = genreDao.get(1L);

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .build();

        FilmDaoImpl filmStorage = new FilmDaoImpl(jdbcTemplate);
        Long id = filmStorage.add(newFilm);
        genreDao.add(id, genreList);
        filmStorage.delete(id);


        assertThrows(NotFoundException.class, () -> {
            filmStorage.get(id);
        });

        assertTrue(genreDao.get(id).isEmpty());
    }

    @Test
    public void testLikeFilm() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        LikeDao likeDao = new LikeDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Long id = userDao.add(newUser);


        Film newFilm = Film.builder()
                .id(1L).mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .build();

        FilmDaoImpl filmStorage = new FilmDaoImpl(jdbcTemplate);
        Long idFilm = filmStorage.add(newFilm);
        likeDao.add(new Like(id, idFilm));
        Film film2 = filmStorage.get(idFilm);
        assertEquals(1, likeDao.get(idFilm).size());
        assertEquals(new Like(id, idFilm), likeDao.get(idFilm).get(0));
    }

    @Test
    public void testdislikeFilm() {
        UserDao userDao = new UserDaoImpl(jdbcTemplate);
        LikeDao likeDao = new LikeDaoImpl(jdbcTemplate);

        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Long id = userDao.add(newUser);

        Film newFilm = Film.builder()
                .id(1L).mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .build();

        FilmDaoImpl filmStorage = new FilmDaoImpl(jdbcTemplate);
        Long idFilm = filmStorage.add(newFilm);
        likeDao.add(new Like(id, idFilm));
        likeDao.delete(new Like(id, idFilm));

        assertTrue(likeDao.get(idFilm).isEmpty());
    }


}
