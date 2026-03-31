# Vinyl Exchange - Vinyl Record Trading Platform

Vinyl Exchange is a web platform for buying and trading vinyl records. Sellers list their albums, buyers browse and message sellers, and complete trades through an escrow-based payment system.

## Prerequisites

- **Java 17+ JVM** - backend
- **PostgreSQL** - database
- **Docker** - for OpenSearch

## How it works

**Frontend**
- Built with React and Tailwind CSS
- Login and registration with JWT stored in HTTP-only cookies
- Optional MusicBrainz lookup during listing creation. Search and select listing to auto-fill album metadata
- OpenSearch-powered listing search by title, artist, label, and album format
- Messaging section for buyer-seller communication
- Notifications for new messages and new listings that matching a user's watchlist

**Backend**
- Built with Java and Spring Boot
- Handles auth, listings, messaging, notifications and checkout
- Integrates with MusicBrainz API for record metadata, OpenSearch for listing search
- Stores all monetary values in the smallest currency unit for precision and compliance

## Getting Started

**Frontend**
- Run `npm install` in the frontend directory
- Run `npm run dev`

**Backend**
- Copy .env.example to .env and fill in your credentials
- Start Docker
- Run `./mvnw spring-boot:run`
- OpenSearch will start automatically if not already running

## Notes

- Work in progress, core features functional, escrow and full payment flow not yet implemented
- Messaging is HTTP-based because of low-frequency interactions. Websocket felt overkill
