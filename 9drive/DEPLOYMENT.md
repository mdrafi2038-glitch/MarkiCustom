# 9Drive VPS + Docker deployment

Prerequisites: Linux VPS, Docker Engine + Compose plugin, Git, and DNS pointing your domain to the VPS.

Deploy:

    git clone --branch 9drive --recurse-submodules https://github.com/mdrafi2038-glitch/MarkiCustom.git
    cd MarkiCustom/9drive
    cp .env.docker.example .env
    nano .env
    chmod +x deploy.sh
    ./deploy.sh

Set strong unique values for database passwords, JWT_ACCESS_SECRET and TOKEN_ENCRYPTION_KEY. Never commit .env.

Google OAuth callback: https://YOUR_DOMAIN/connected-accounts/google/callback

Operations: docker compose ps; docker compose logs -f backend; docker compose logs -f frontend; docker compose logs -f mysql; docker compose restart

Do not run `docker compose down -v` unless you intentionally want to remove persistent database data.

Updates:
    git pull origin 9drive
    git submodule update --init --recursive
    ./deploy.sh

Back up the MySQL volume before production updates.
