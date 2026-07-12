#!/usr/bin/env bash
set -euo pipefail

APP_NAME="avnzor-oms"
BASE_DIR="/opt/avnzor"
APP_DIR="${BASE_DIR}/app"
CONFIG_DIR="${BASE_DIR}/config"
LOG_DIR="${BASE_DIR}/logs"
BACKUP_DIR="${BASE_DIR}/backups"
SERVICE_NAME="avnzor-oms"
JAR_NAME="oms-backend.jar"
SOURCE_JAR="${1:?Usage: deploy.sh <path-to-jar>}"

timestamp="$(date +%Y%m%d_%H%M%S)"

mkdir -p "${APP_DIR}" "${CONFIG_DIR}" "${LOG_DIR}" "${BACKUP_DIR}"
chown -R avnzor:avnzor "${BASE_DIR}"

if [[ -f "${APP_DIR}/${JAR_NAME}" ]]; then
  cp "${APP_DIR}/${JAR_NAME}" "${BACKUP_DIR}/${JAR_NAME}.${timestamp}"
fi

install -o avnzor -g avnzor -m 0644 "${SOURCE_JAR}" "${APP_DIR}/${JAR_NAME}"

if [[ ! -f "${CONFIG_DIR}/application.env" ]]; then
  echo "Missing ${CONFIG_DIR}/application.env — create it from deploy/config/application.env.example"
  exit 1
fi

chmod 600 "${CONFIG_DIR}/application.env"
chown avnzor:avnzor "${CONFIG_DIR}/application.env"

systemctl daemon-reload
systemctl enable "${SERVICE_NAME}"
systemctl restart "${SERVICE_NAME}"
systemctl --no-pager status "${SERVICE_NAME}"

echo "Deployment completed successfully."
