# Deployment layout

Recommended Linux server structure for **Staging** and **Production** (identical layout; only config differs).

```
/opt/avnzor/
├── app/           # Application JAR and runtime binaries
├── config/        # Environment-specific configuration (application.env)
├── logs/          # Application log files (rolling)
└── backups/       # Previous JAR versions for rollback
```

## Directory purposes

### `app/`

- Contains the deployed Spring Boot JAR (`oms-backend.jar`).
- Same artifact on staging and production; only the profile and env file change.
- Owned by the `avnzor` service user.

### `config/`

- Holds `application.env` with environment variables (`DATABASE_*`, `JWT_*`, `SERVER_PORT`, etc.).
- File permissions: `600` (readable only by the service user).
- **Never commit** real values to Git; use `application.env.example` as a template.
- Change behavior per environment without rebuilding the application.

### `logs/`

- Rolling application logs written by Logback (`LOG_FILE_PATH`).
- Useful for troubleshooting, audits, and monitoring agents.
- Rotate/archive with logrotate or the built-in rolling policy.

### `backups/`

- Timestamped copies of the previous JAR before each deployment.
- Enables fast rollback: restore an older JAR and `systemctl restart avnzor-oms`.

## First-time server setup

```bash
sudo useradd --system --home /opt/avnzor --shell /usr/sbin/nologin avnzor
sudo mkdir -p /opt/avnzor/{app,config,logs,backups}
sudo chown -R avnzor:avnzor /opt/avnzor

# Copy and edit environment file (use staging or production template)
sudo cp application.env.example /opt/avnzor/config/application.env
sudo chmod 600 /opt/avnzor/config/application.env
sudo chown avnzor:avnzor /opt/avnzor/config/application.env

# Install systemd unit
sudo cp deploy/systemd/avnzor-oms.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable avnzor-oms
```

## Promoting staging → production

1. Verify the release on staging (same JAR artifact).
2. Trigger **Deploy Production** in GitHub Actions (manual approval required).
3. Production receives the **same JAR** with production `application.env` values.
4. No code or rebuild changes are required—only environment variables and profile.

## GitHub Environments

Configure in **Settings → Environments**:

| Environment  | Protection rules                          |
|--------------|-------------------------------------------|
| `staging`    | Optional: deployment branch `master`    |
| `production` | **Required reviewers** (manual approval)  |

Store secrets per environment (never in the repository):

- `SSH_HOST`, `SSH_USER`, `SSH_PRIVATE_KEY`
- `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER`, `DATABASE_PASSWORD`
- `JWT_SECRET`, `JWT_EXPIRATION`
- `SERVER_PORT`, `LOG_FILE_PATH`
