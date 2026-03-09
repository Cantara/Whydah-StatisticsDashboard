# Whydah-StatisticsDashboard

## Purpose
Dashboard web application providing insights into user activities for a running Whydah installation. Visualizes user counts, application counts, login frequency, new user registrations, and session statistics.

## Tech Stack
- Language: Java 11+
- Framework: Web UI
- Build: Maven
- Key dependencies: Whydah SDKs

## Architecture
Standalone web application that connects to a Whydah installation and presents usage analytics through a visual dashboard. Tracks metrics including total users, total applications, new users per day (by source application), logins per day, deleted users, and session access patterns. Configurable via `environment_config.json`.

## Key Entry Points
- `environment_config.json` - Environment configuration (deployment target, favicon)
- Dashboard web UI

## Development
```bash
# Build
mvn clean install

# Run
java -jar target/Whydah-StatisticsDashboard-*.jar
```

## Domain Context
Whydah IAM analytics and reporting. Provides operational visibility into user activity patterns, application usage, and authentication trends across a Whydah deployment.
