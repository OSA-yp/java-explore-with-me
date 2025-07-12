
-- Таблица категорий
CREATE TABLE categories (
                            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
                       id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       name VARCHAR(250) NOT NULL,
                       email VARCHAR(254) NOT NULL UNIQUE
);

-- Таблица локаций
CREATE TABLE IF NOT EXISTS locations (
                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                           lat FLOAT NOT NULL,
                           lon FLOAT NOT NULL
);

-- Таблица событий
CREATE TABLE IF NOT EXISTS events (
                        id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        annotation VARCHAR(2000) NOT NULL,
                        description VARCHAR(7000) NOT NULL,
                        event_date TIMESTAMP NOT NULL,
                        created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        published_on TIMESTAMP,
                        paid BOOLEAN DEFAULT FALSE,
                        title VARCHAR(120) NOT NULL,
                        participant_limit INT DEFAULT 0,
                        request_moderation BOOLEAN DEFAULT TRUE,
                        state VARCHAR(20) NOT NULL CHECK (state IN ('PENDING', 'PUBLISHED', 'CANCELED')),
                        initiator_id BIGINT REFERENCES users(id),
                        category_id BIGINT REFERENCES categories(id),
                        location_id BIGINT REFERENCES locations(id),
                        views BIGINT DEFAULT 0,
                        confirmed_requests BIGINT DEFAULT 0
);

-- Таблица заявок на участие
CREATE TABLE IF NOT EXISTS requests (
                          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                          created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          event_id BIGINT REFERENCES events(id),
                          requester_id BIGINT REFERENCES users(id),
                          status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'REJECTED'))
);

-- Таблица подборок событий
CREATE TABLE IF NOT EXISTS compilations (
                              id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                              title VARCHAR(50) NOT NULL,
                              pinned BOOLEAN DEFAULT FALSE
);

-- Связь между подборками и событиями
CREATE TABLE IF NOT EXISTS compilation_events (
                                    compilation_id BIGINT REFERENCES compilations(id),
                                    event_id BIGINT REFERENCES events(id),
                                    PRIMARY KEY (compilation_id, event_id)
);