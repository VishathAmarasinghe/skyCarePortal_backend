#!/usr/bin/env sh
# Ensures persistent upload paths exist on the Docker host (mounted into backend as /root/skyCareUploads).
# Usage: ./scripts/prepare-upload-dirs.sh [/var/lib/skycareuploads]
#
# Jenkins often runs as a user without write access to /var/lib/*; when direct mkdir fails,
# directories are created via a short-lived Docker container (same pattern as compose deploy).

set -eu

UPLOAD_ROOT="${1:-/var/lib/skycareuploads}"

SUBDIRS="
staffTraining
profilePhotos
Client_profile_Photos
careGiverDocuments
agreements/org
agreements/assets
agreements/signatures
agreements/instances
agreements/reference-materials
incidents
Signatures
chatDocuments
Task_Attachments
shiftNotes
resources
notes
ClientDocuments
AppointmentAttachments
"

create_dirs_direct() {
  mkdir -p "$UPLOAD_ROOT"
  for dir in $SUBDIRS; do
    mkdir -p "$UPLOAD_ROOT/$dir"
  done
  chmod -R 775 "$UPLOAD_ROOT" 2>/dev/null || true
}

create_dirs_with_docker() {
  if ! command -v docker >/dev/null 2>&1; then
    echo "error: cannot write to $UPLOAD_ROOT and docker is not available" >&2
    return 1
  fi

  echo "Direct write to $UPLOAD_ROOT not permitted; creating directories via Docker..."

  # shellcheck disable=SC2086
  docker run --rm \
    -v "${UPLOAD_ROOT}:/uploads" \
    alpine:3.20 \
    sh -c "
      set -eu
      mkdir -p /uploads
      for dir in $SUBDIRS; do
        mkdir -p \"/uploads/\$dir\"
      done
      chmod -R 775 /uploads
    "
}

if create_dirs_direct 2>/dev/null; then
  :
else
  create_dirs_with_docker
fi

echo "Upload directories ready under $UPLOAD_ROOT"
