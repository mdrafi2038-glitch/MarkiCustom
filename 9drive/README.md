# 9Drive setup

This branch is dedicated to the open-source **9Drive** project.

## Upstream project

The original source is tracked as a Git submodule at:

- `9drive/upstream` → https://github.com/zenhosta/9drive
- Pinned upstream commit: `811d4a2137538b73abb43d195d7bf452e01b0c58`

This keeps the upstream project intact instead of copying its source into this repository.

## Project structure

```
9drive/
├── upstream/          # 9Drive upstream source (git submodule)
└── README.md
```

The upstream application contains the React/Vite frontend, Express/TypeScript backend, Prisma/MySQL database layer, Docker deployment files, and Google Drive/S3-compatible storage integrations.

## Important

GitHub stores the source, but the full 9Drive application needs a server/VPS (or another backend-capable host) because it uses Node.js and MySQL. GitHub Pages alone cannot run the backend/database.

## Next deployment step

Clone this branch with submodules:

```bash
git clone --branch 9drive --recurse-submodules https://github.com/mdrafi2038-glitch/MarkiCustom.git
cd MarkiCustom/9drive/upstream
```

Then follow the upstream 9Drive setup instructions for environment variables, Google OAuth, MySQL and Docker.
