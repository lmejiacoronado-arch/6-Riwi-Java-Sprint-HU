ALTER TABLE venues
    ADD COLUMN city VARCHAR(100);

UPDATE venues
    SET city = 'Unknown'
    WHERE city IS NULL;

ALTER TABLE venues
    ALTER COLUMN city SET NOT NULL;

ALTER TABLE events
    ADD COLUMN active BOOLEAN;

UPDATE events
SET active = true
WHERE active IS NULL;

ALTER TABLE events
    ALTER COLUMN active SET NOT NULL;

ALTER TABLE events
    ADD COLUMN venue_id BIGINT;

UPDATE events
    SET venue_id = (
        SELECT id
        FROM venues
        ORDER BY id
        LIMIT 1
    )
WHERE venue_id IS NULL;

ALTER TABLE events
    ALTER COLUMN venue_id SET NOT NULL;

ALTER TABLE events
    ADD CONSTRAINT fk_events_venues
        FOREIGN KEY (venue_id)
            REFERENCES venues(id);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE events_categories (
    event_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, category_id),
    CONSTRAINT fk_events_categories_event
       FOREIGN KEY (event_id)
           REFERENCES events(id),
    CONSTRAINT fk_events_categories_category
       FOREIGN KEY (category_id)
           REFERENCES categories(id)
);