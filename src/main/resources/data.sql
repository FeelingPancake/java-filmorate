MERGE INTO PUBLIC.genres AS target
USING (
    VALUES
        ('Комедия'),
        ('Драма'),
        ('Мультфильм'),
        ('Триллер'),
        ('Документальный'),
        ('Боевик')
) AS source (genre_name)
ON target.genre_name = source.genre_name
WHEN NOT MATCHED THEN
    INSERT (genre_name) VALUES (source.genre_name);


 MERGE INTO PUBLIC.film_age_ratings AS target
 USING (
    VALUES
        ('G'),
        ('PG'),
        ('PG-13'),
        ('R'),
        ('NC-17')
 )  AS source (rating_name)
 ON target.rating_name = source.rating_name
 WHEN NOT MATCHED THEN
     INSERT (rating_name) VALUES (source.rating_name);



