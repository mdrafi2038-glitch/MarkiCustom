# 9Drive deployment plan

## Components

- Frontend: React + Vite
- Backend: Express + TypeScript
- Database: MySQL + Prisma
- Storage: Google Drive and S3-compatible storage
- Containerization: Docker / Docker Compose

## Required secrets

Do not commit real passwords, OAuth secrets, JWT secrets, encryption keys, or API tokens. Put them in the server's environment or GitHub/VPS secret store.

Typical configuration includes:

- DATABASE_URL
- APP_PORT
- FRONTEND_URL
- JWT_ACCESS_SECRET
- TOKEN_ENCRYPTION_KEY
- GOOGLE_CLIENT_ID
- GOOGLE_CLIENT_SECRET
- GOOGLE_REDIRECT_URI
- VITE_API_URL

See the upstream README for the current complete configuration and deployment commands.
