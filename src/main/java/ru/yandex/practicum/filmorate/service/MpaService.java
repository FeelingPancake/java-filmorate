package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfacesDao.MpaDao;

import java.util.Comparator;
import java.util.List;

@Service
public class MpaService {
    private final MpaDao mpaDao;

    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Mpa get(Long id) {
        return mpaDao.get(id);
    }

    public List<Mpa> getAll() {
        return mpaDao.getAll().stream().sorted(Comparator.comparing(Mpa::id)).toList();
    }
}
