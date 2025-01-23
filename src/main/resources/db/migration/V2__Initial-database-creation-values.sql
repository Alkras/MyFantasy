INSERT INTO characters (id, name, level, type, money, hit_points,max_hit_points,armor,agility,attack,experience,experience_for_level_up)
VALUES (0, 'ShopKeeper Dave', 1, 'SHOPKEEPER', 0,1000000,1000000,1000000,1000000,1000000,0,0);

INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('SMALL HP POTION', 'CONSUMABLE', 100, 100);
INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('HP POTION', 'CONSUMABLE', 500, 1000);
INSERT INTO item_templates (name, item_type, price, strength)
VALUES ('BIG HP POTION', 'CONSUMABLE', 1000, 3000);

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
