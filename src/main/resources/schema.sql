DROP TABLE IF EXISTS "liked_by";
DROP TABLE IF EXISTS "friendship";
DROP TABLE IF EXISTS "genres";
DROP TABLE IF EXISTS "film";
DROP TABLE IF EXISTS "genre";
DROP TABLE IF EXISTS "users";
DROP TABLE IF EXISTS "age_rating";

CREATE TABLE IF NOT EXISTS "age_rating" (
  "rating_id" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "rating_name" VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "film" (
  "id" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "name" VARCHAR(255) NOT NULL,
  "description" VARCHAR(255) NOT NULL,
  "release_date" TIMESTAMP,
  "duration" INTEGER,
  "rating" INTEGER,
  "age_rating" INTEGER NOT NULL,
  FOREIGN KEY ("age_rating") REFERENCES "age_rating" ("rating_id")
);

CREATE TABLE IF NOT EXISTS "genre" (
  "genre_id" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "genre_name" VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS "genres" (
  "film_id" BIGINT NOT NULL,
  "genre_id" BIGINT NOT NULL,
  FOREIGN KEY ("genre_id") REFERENCES "genre" ("genre_id"),
  FOREIGN KEY ("film_id") REFERENCES "film" ("id")
);

CREATE TABLE IF NOT EXISTS "users" (
  "id" BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  "email" VARCHAR(255) NOT NULL,
  "login" VARCHAR(255) NOT NULL,
  "name" VARCHAR(255),
  "birthday" TIMESTAMP
);

CREATE TABLE IF NOT EXISTS "liked_by" (
  "user_id" BIGINT NOT NULL,
  "film_id" BIGINT NOT NULL,
  PRIMARY KEY ("user_id", "film_id"),
  FOREIGN KEY ("film_id") REFERENCES "film" ("id"),
  FOREIGN KEY ("user_id") REFERENCES "users" ("id")
);

CREATE TABLE IF NOT EXISTS "friendship" (
  "user_id" BIGINT NOT NULL,
  "friend_id" BIGINT NOT NULL,
  "is_confirmed" BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY ("user_id", "friend_id"),
  FOREIGN KEY ("user_id") REFERENCES "users" ("id"),
  FOREIGN KEY ("friend_id") REFERENCES "users" ("id"),
  CONSTRAINT "friendship" UNIQUE("user_id", "friend_id")
);

