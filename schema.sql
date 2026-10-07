PRAGMA foreign_keys = ON;

-- Ingredients the user currently has at home
CREATE TABLE IF NOT EXISTS pantry_items (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            TEXT    NOT NULL,
    normalized_name TEXT    NOT NULL,
    quantity        REAL    NOT NULL CHECK (quantity > 0),
    unit            TEXT    NOT NULL,
    expiry_date     TEXT                               -- "yyyy-MM-dd" or NULL
);

-- Pre-loaded recipes (seeded on first run)
CREATE TABLE IF NOT EXISTS recipes (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    name         TEXT    NOT NULL UNIQUE,
    steps        TEXT    NOT NULL,
    prep_minutes INTEGER NOT NULL CHECK (prep_minutes > 0)
);

-- Ingredients each recipe requires (one recipe -> many ingredients)
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    recipe_id       INTEGER NOT NULL,
    name            TEXT    NOT NULL,
    normalized_name TEXT    NOT NULL,
    quantity        REAL    NOT NULL CHECK (quantity > 0),
    unit            TEXT    NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);

-- Indexes to speed up matching lookups
CREATE INDEX IF NOT EXISTS idx_pantry_normalized_name ON pantry_items(normalized_name);
CREATE INDEX IF NOT EXISTS idx_recipe_ingredients_recipe_id ON recipe_ingredients(recipe_id);