package com.vinyl.VinylExchange.listing;

import org.springframework.stereotype.Service;

import com.vinyl.VinylExchange.shared.exception.GenreNotFoundException;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre saveGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    public Genre findGenreByName(String name) {
        return genreRepository.findByName(name).orElseThrow(() -> new GenreNotFoundException());
    }
}
