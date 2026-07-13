# CineFind 🎬

A Java desktop application for discovering movies — search titles, browse details, and build watchlists. Built as a team project for CSC207 (Software Design) at the University of Toronto.

<!-- TODO: Add a screenshot or GIF of the app here. This is the single highest-impact thing you can add. -->
<!-- ![CineFind screenshot](docs/screenshot.png) -->

## Features

<!-- TODO: Replace with your actual user stories/features. Examples of the format: -->
- **Search movies** — look up any title and view details like rating, genre, and synopsis
- **[Feature 2]** — [one-line description]
- **[Feature 3]** — [one-line description]
- **[Feature 4]** — [one-line description]

## Architecture

The project follows **Clean Architecture**, with strict separation between entities, use cases, interface adapters, and frameworks/drivers:

```
src/
├── entity/               # Core business objects (Movie, User, ...)
├── use_case/             # Application business rules (one package per use case)
├── interface_adapter/    # Controllers, presenters, view models
├── view/                 # Swing UI
└── data_access/          # API and file-based data access
```
<!-- TODO: Adjust the tree above to match your actual package names -->

Each use case is wired through the standard CSC207 flow: View → Controller → Interactor → Presenter → ViewModel, keeping business logic independent of the UI and external APIs.

## Tech Stack

- **Java 17** with **Maven** for build management
- **Swing** for the desktop UI
- **[Movie API name]** for movie data <!-- TODO: TMDB? OMDb? Fill in with a link -->
- **JUnit** for testing

## Running the App

```bash
git clone https://github.com/gsverlov/CineFind-Movie-app.git
cd CineFind-Movie-app
mvn clean install
```

<!-- TODO: Add the actual entry point, e.g.: -->
Then run `Main.java` in `src/main/java/...` from your IDE, or:

```bash
mvn exec:java -Dexec.mainClass="app.Main"
```

<!-- TODO: If an API key is needed, document it: -->
<!-- Set the `API_KEY` environment variable with your [API name] key. -->

## Team

Built by a team of [N] for CSC207 (Fall 2025), University of Toronto.

**My contributions:** <!-- TODO: This is the most important section for recruiters. Name the specific use case(s) you owned, e.g.: -->
- Implemented the [X] use case end-to-end (entity, interactor, presenter, view)
- [Other contribution]

Teammates: [names or GitHub handles]
