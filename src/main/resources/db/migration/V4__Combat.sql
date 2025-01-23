ALTER TABLE characters
    ADD COLUMN hit_points INT;
ALTER TABLE characters
    ADD COLUMN max_hit_points INT;
ALTER TABLE characters
    ADD COLUMN armor INT;
ALTER TABLE characters
    ADD COLUMN agility INT;
ALTER TABLE characters
    ADD COLUMN attack INT;
ALTER TABLE characters
    ADD COLUMN experience INT;
ALTER TABLE characters
    ADD COLUMN experience_for_level_up INT;
ALTER TABLE characters
    ALTER COLUMN money DECIMAL(10,2);

UPDATE characters SET hit_points=1000000, max_hit_points=1000000, armor=1000000, agility=1000000, attack=1000000, experience=0, experience_for_level_up=0 where id=0;

INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('BASIC ARMOR', 'ARMOR', 500, 20);
INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('RARE ARMOR', 'ARMOR', 2000, 100);
INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('LEGENDARY ARMOR', 'ARMOR', 5000, 300);

INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('BASIC WEAPON', 'WEAPON', 500, 20);
INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('RARE WEAPON', 'WEAPON', 2000, 100);
INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('LEGENDARY WEAPON', 'WEAPON', 5000, 300);