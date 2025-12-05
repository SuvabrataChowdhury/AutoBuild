#!/bin/sh
set -e

host="${DB_HOST:-mysql-db}"
port="${DB_PORT:-3306}"

echo "Waiting for MySQL at $host:$port..."

# Option 1: using nc
until nc -z "$host" "$port"; do
  echo "MySQL not ready, sleeping..."
  sleep 2
done

# Option 2: optional MySQL ping check (more reliable)
# until mysqladmin ping -h "$host" -u "$DB_USER" -p"$DB_PASSWORD" --silent; do
#   echo "MySQL not ready, sleeping..."
#   sleep 2
# done

echo "MySQL is up, starting app..."
exec java -jar app.jar
