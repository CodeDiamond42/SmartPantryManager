# Smart Pantry Manager

## About the App

Smart Pantry Manager is an Android application, written in Java, that helps reduce food waste. It keeps track of the ingredients a user has at home and suggests recipes that can be made using only those ingredients.

A recipe is only suggested when every ingredient it needs is already in the pantry, in a large enough quantity. If even one ingredient is missing or there isn't enough of it, the recipe is not suggested. This means the user never needs to go shopping to make a suggested recipe.

## Features

### Pantry
- Add ingredients with a name, quantity, unit and an optional expiry date
- Edit and delete existing ingredients
- View all pantry items in a list
- Input validation with clear error messages (for example, an empty name or a quantity of 0)
- Unit hints: when typing an ingredient name, the app shows which unit the recipes use for it (for example, honey is measured in tbsp)
- Expiry alerts: items that are expired or expiring soon are highlighted, with a message at the top showing how many there are

### Suggested Recipes
- Compares the pantry against the built-in recipe collection
- Shows only the recipes that can be made right now
- Shows a helpful message when no recipes match, instead of an empty screen
- An optional "Almost there" section lists recipes missing just one ingredient. These are kept separate and are never counted as suggestions

### Recipe Details
Selecting a recipe shows:
- Recipe name
- Preparation time
- Whether the pantry has everything needed, or which ingredients are missing
- Each ingredient with its quantity and unit, marked as available or missing
- Step-by-step preparation instructions

### Settings
- Turn expiry alerts on or off
- Choose how many days before expiry to be warned (1, 3 or 7 days)
- Sort the pantry by name or by expiry date
- Show or hide the "Almost there" section
- Clear all pantry items

Settings are saved, so they stay the same after the app is closed and reopened.

## How Recipe Matching Works

For a recipe to be suggested:
1. Every required ingredient must be in the pantry
2. The pantry must have at least the amount the recipe needs

To make matching reliable, the app also handles small differences:
- **Capital letters and extra spaces** are ignored ("Butter" matches "butter")
- **Plurals** are handled ("Tomatoes" matches "tomato")
- **Units are converted** where possible (1 kg covers a recipe needing 500 g, and 3 tsp equals 1 tbsp)
- **Separate entries** of the same ingredient are added together

Units that measure different things, such as grams and millilitres, cannot be converted into each other, so they are not matched.

## Database

### Database Option: SQLite

The app uses SQLite, managed through Android's `SQLiteOpenHelper` class.

### Why SQLite Was Chosen

- **It's built into Android:** every Android device includes SQLite, so there's nothing extra to install or set up
- **No server needed:** the app doesn't depend on an external database or an internet connection
- **Works offline:** the pantry can be managed anywhere, including in a kitchen with poor signal
- **Keeps data private:** a pantry belongs to one person, so the data stays on their device
- **Suits relational data:** each recipe is linked to its list of ingredients, which fits a SQL database well
- **Matches the module content:** SQLite is the persistent storage approach covered in the course

### Tables

**pantry_items** stores the ingredients the user has:
- ID, name, normalised name (used for matching), quantity, unit, expiry date (optional)

**recipes** stores the built-in recipes:
- ID, name, preparation steps, preparation time in minutes

**recipe_ingredients** stores the ingredients each recipe needs:
- ID, recipe ID (links to the recipe), name, normalised name, quantity, unit

The full SQL schema can be found in `database/schema.sql`.

User settings are stored separately using `SharedPreferences`, since they are simple on/off and choice values that don't need a database table.

## Requirements

- Android Studio
- Android SDK (installed through Android Studio)
- JDK 17 or newer (included with Android Studio)
- An Android emulator or a physical Android device running Android 7.0 (API 24) or newer

## Setup and Run Instructions

### 1. Clone the Repository

```
git clone https://github.com/CodeDiamond42/SmartPantryManager.git
```

### 2. Open the Project
1. Open Android Studio
2. Select **File → Open**
3. Choose the cloned project folder
4. Wait for the project to load

### 3. Gradle Sync
Android Studio should sync the project with Gradle automatically. If a **Sync Now** prompt appears, click it. Wait for the sync to finish and check that the Build window shows no errors. If Android Studio asks to install missing SDK components, allow it.

### 4. Choose a Device

#### Emulator

1. Open **Device Manager**
2. Create or select a virtual device (API 24 or newer)
3. Start it

#### Physical device

1. Turn on **Developer options** on the phone
2. Turn on **USB debugging**
3. Connect the phone to the computer with a USB cable
4. Accept the prompt on the phone to allow debugging
5. Select the phone from Android Studio's device list

### 5. Run
Click **Run** in Android Studio. The app will build, install and open on the selected device.

## First Run

The first time the app opens, it creates its SQLite database on the device (`smart_pantry_db.db`), builds the tables, and loads 20 recipes. The pantry starts empty, so the next step is to add some ingredients from the Pantry screen.

## Running the Unit Tests

The matching rules, name handling and expiry date calculations are covered by unit tests. These run on the computer and don't need a device.

In Android Studio, go to **app → java → com.example.assignment (test)**, right-click the **logic** package, and choose **Run 'Tests in 'logic''**.

The tests cover:
- **RecipeMatcherTest:** the strict matching rule, unit conversion, plurals and the "Almost there" list
- **IngredientNormalizerTest:** capital letters, spacing and singular/plural names
- **ExpiryCheckerTest:** days until expiry, expired items and the warning window

## Project Structure

The main package is `com.example.assignment`, split into:

- **adapter:** RecyclerView adapters for the pantry list, recipe cards and recipe ingredients
- **database:** `DatabaseHelper` (tables and CRUD operations) and `RecipeSeeder` (the built-in recipes)
- **logic:** recipe matching, unit conversion, name normalisation and expiry checks
- **model:** `PantryItem`, `Recipe` and `RecipeIngredient`
- **settings:** `PreferencesManager`, which saves the user's settings
- **ui:** the five screens (Pantry, Add/Edit Ingredient, Suggested Recipes, Recipe Detail, Settings) and the bottom navigation helper

## Troubleshooting

#### The app doesn't build

Try **Build → Clean Project**, then **Build → Rebuild Project**. Make sure Gradle sync finished without errors.

#### Starting from a fresh database

Uninstall the app from the device, or go to **Settings → Apps → Smart Pantry Manager → Storage → Clear data**. The database and recipes will be created again the next time the app opens. To only remove pantry items, use **Clear pantry** in the app's Settings screen.

#### A recipe isn't appearing

Check that:
- Every ingredient in the recipe is in the pantry
- The pantry quantities are large enough
- The units are the right type. For example, if a recipe measures honey in tbsp, honey must be added as a volume (ml, L, tsp, tbsp or cup), not in grams

Opening the recipe from the "Almost there" section shows which ingredient is missing.

## Technologies Used

- Java
- Android Studio and Android SDK
- SQLite (SQLiteOpenHelper)
- SharedPreferences
- RecyclerView with custom adapters
- Activities and Intents
- Material Components
- JUnit 4
- Git and GitHub

## Author

Yashin Naidoo

Mobile App Development 700
