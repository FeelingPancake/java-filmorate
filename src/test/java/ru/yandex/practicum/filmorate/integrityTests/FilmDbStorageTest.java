package ru.yandex.practicum.filmorate.integrityTests;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.IdNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.database.Dao.FriendShipDaoImpl;
import ru.yandex.practicum.filmorate.storage.database.Dao.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.database.Dao.LikeDaoImpl;
import ru.yandex.practicum.filmorate.storage.database.Dao.MpaDaoImpl;
import ru.yandex.practicum.filmorate.storage.database.DaoStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.database.DaoStorage.UserDbStorage;
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
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;
    private final FriendShipDao friendShipDao;

    @Test
    public void testFindUserById() {

        List<Genre> genreList = genreDao.get(1L);

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .genres(genreList)
                .build();
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreDao, mpaDao, likeDao);
        long id = filmStorage.add(newFilm);

        Film savedFilm = filmStorage.get(id);


        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(filmStorage.get(id));
    }

    @Test
    public void testFindALlFilms() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreDao, mpaDao, likeDao);
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
                    .genres(genreList)
                    .build();

            Long id = filmStorage.add(newFilm);
            expected.add(id);
        }
        List<Long> films = filmStorage.getAll().stream().map(Film::getId).toList();

        Assertions.assertIterableEquals(expected, films);

        for (int i = 0; i < films.size(); i++) {
            assertThat(films.get(i)).isNotNull().usingRecursiveComparison().isEqualTo(expected.get(i));
        }
    }

    @Test
    public void testUpdateFilm() {
        List<Genre> genreList = genreDao.get(1L);

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .genres(genreList)
                .build();


        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreDao, mpaDao, likeDao);
        Long id = filmStorage.add(newFilm);
        Film updatedFilm = newFilm.toBuilder().id(id).name("Джон уик").build();
        Film oldFilm = filmStorage.get(id);
        filmStorage.update(updatedFilm);

        Film savedFilm = filmStorage.get(id);

        assertThat(oldFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isNotEqualTo(savedFilm);

        assertSame(oldFilm.getId(), savedFilm.getId());
    }

    @Test
    public void testDeleteFilm() {

        List<Genre> genreList = genreDao.get(1L);

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .genres(genreList)
                .build();

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreDao, mpaDao, likeDao);
        Long id = filmStorage.add(newFilm);
        filmStorage.delete(id);

        assertThrows(IdNotFoundException.class, () -> {
            filmStorage.get(id);
        });
    }

    @Test
    public void testLikeFilm() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate, friendShipDao);

        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        Long id = userStorage.add(newUser);

        List<Genre> genreList = genreDao.get(1L);

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .genres(genreList)
                .build();

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreDao, mpaDao, likeDao);
        Long idFilm = filmStorage.add(newFilm);
        filmStorage.likeFilm(id, idFilm);
        Film film2 = filmStorage.get(idFilm);
        assertEquals(1, filmStorage.get(idFilm).getLikedBy().size());
        assertEquals(new Like(id, idFilm), filmStorage.get(idFilm).getLikedBy().get(0));
    }

    @Test
    public void testdislikeFilm() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate, friendShipDao);

        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .login("vanya123")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1)).build();

        Long id = userStorage.add(newUser);
        List<Genre> genreList = genreDao.getGenresIdForFilm(id).stream().map(x -> new Genre(x, null)).toList();

        Film newFilm = Film.builder()
                .id(1L)
                .mpa(new Mpa(2L, "PG"))
                .name("Крепкий орешек")
                .description("Брюс против Снегга")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(130)
                .genres(genreList)
                .build();

        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, genreDao, mpaDao, likeDao);
        Long idFilm = filmStorage.add(newFilm);
        filmStorage.likeFilm(id, idFilm);
        filmStorage.dislikeFilm(id, idFilm);

        assertTrue(filmStorage.get(idFilm).getLikedBy().isEmpty());
    }


}
