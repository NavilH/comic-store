ALTER TABLE comic_authors
    DROP CONSTRAINT comic_authors_comic_id_fkey,
    ADD CONSTRAINT comic_authors_comic_id_fkey
        FOREIGN KEY (comic_id) REFERENCES comics(id) ON DELETE CASCADE;

ALTER TABLE inventory
    DROP CONSTRAINT inventory_comic_id_fkey,
    ADD CONSTRAINT inventory_comic_id_fkey
        FOREIGN KEY (comic_id) REFERENCES comics(id) ON DELETE CASCADE;
