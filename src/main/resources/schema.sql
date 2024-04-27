DROP TABLE IF EXISTS film_liked_by CASCADE;
DROP TABLE IF EXISTS user_friendship CASCADE;
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS film_age_ratings CASCADE;
DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE IF NOT EXISTS film_age_ratings (
  rating_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  rating_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS films (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  release_date TIMESTAMP,
  duration INTEGER,
  rating INTEGER,
  age_rating INTEGER NOT NULL,
  FOREIGN KEY (age_rating) REFERENCES film_age_ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS genres (
  genre_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  genre_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genres (
  film_id BIGINT NOT NULL,
  genre_id BIGINT NOT NULL,
  FOREIGN KEY (genre_id) REFERENCES genres (genre_id),
  FOREIGN KEY (film_id) REFERENCES films (id)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  login VARCHAR(255) NOT NULL,
  name VARCHAR(255),
  birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS film_liked_by (
  user_id BIGINT NOT NULL,
  film_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, film_id),
  FOREIGN KEY (film_id) REFERENCES films (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_friendships (
  user_id BIGINT NOT NULL,
  friend_id BIGINT NOT NULL,
  is_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (user_id, friend_id),
  FOREIGN KEY (user_id) REFERENCES users (id),
  FOREIGN KEY (friend_id) REFERENCES users (id),
  CONSTRAINT user_friendships UNIQUE(user_id, friend_id)
);

