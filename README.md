# Vinyl Exchange - Vinyl Record Trading Platform

Vinyl Exchange is a web platform for buying and trading vinyl records. Sellers list their albums, buyers browse and message sellers, and complete trades through an escrow-based payment system.

## Prerequisites

- **Docker** - all services run in containers

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

### Setup
 
Copy `.env.example` to `.env` and fill in your credentials:
 
```bash
cp .env.example .env
```
 
### Running
 
```bash
# Start all services (PostgreSQL, OpenSearch, backend, frontend)
make start
 
# Stop all services
make stop
```
 
`make start` builds and starts all containers, then waits for each service to pass its health check before printing the access URLs.
 
The stack includes:
 
| Service | Description |
|---|---|
| `postgres` | PostgreSQL 17 database |
| `opensearch` | OpenSearch 2.19.4 for listing search |
| `vx-backend` | Spring Boot API |
| `vx-frontend` | React app served via Nginx on port 80 |
## Notes

- Work in progress, core features functional, escrow and full payment flow not yet implemented
- Messaging is HTTP-based because of low-frequency interactions. Websocket felt overkill
