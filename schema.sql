PRAGMA foreign_keys = ON;

--ingredients that the user has at home
CREATE TABLE IF NOT EXISTS pantry_items(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT NOT NULL,
	normalized_name TEXT NOT NULL,
	quantity REAL NOT NULL CHECK (quantity > 0),
	unit TEXT NOT NULL,
	expiry_date TEXT
);


--preloaded recipes that are seeded on the first run
CREATE TABLE IF NOT EXISTS recipes(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	name TEXT NOT NULL UNIQUE,
	steps TEXT NOT NULL
);


--ingredients each recipe requires, one to many relationship between recipe and ingredients
CREATE TABLE IF NOT EXISTS recipe_ingredients(
	id INTEGER PRIMARY KEY AUTOINCREMENT,
	recipe_id INTEGER NOT NULL,
	name TEXT NOT NULL,
	normalized_name TEXT NOT NULL,
	quantity REAL NOT NULL CHECK (quantity > 0),
	unit TEXT NOT NULL,
	FOREIGN KEY (recipe_id) REFERENCES recipes(id) on DELETE CASCADE
);

-- indexes to speed up matching lookups
CREATE INDEX IF NOT EXISTS idx_pantry_normalized_name ON pantry_items(normalized_name);
CREATE INDEX IF NOT EXISTS idx_recipe_ingredients_recipe_id ON recipe_ingredients(recipe_id);

INSERT INTO recipes (name, steps)
VALUES ('Test Toast', '1. Toast the bread. Add butter.');

INSERT INTO recipe_ingredients (recipe_id, name, normalized_name, quantity, unit)
VALUES (1, 'Bread slices', 'bread slice', 2, 'pcs'),
       (1, 'Butter', 'butter', 10, 'g');

INSERT INTO pantry_items (name, normalized_name, quantity, unit, expiry_date)
VALUES ('Butter', 'butter', 250, 'g', '2026-11-01');

-- Each recipe with its ingredients
SELECT r.name AS recipe, ri.name AS ingredient, ri.quantity, ri.unit
FROM recipes r
JOIN recipe_ingredients ri ON ri.recipe_id = r.id;
