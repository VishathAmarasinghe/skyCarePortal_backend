#!/usr/bin/env sh
# Ensures persistent upload paths exist on the Docker host (mounted into backend as /root/skyCareUploads).
# Usage: ./scripts/prepare-upload-dirs.sh [/var/lib/skycareuploads]

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

mkdir -p "$UPLOAD_ROOT"
for dir in $SUBDIRS; do
  mkdir -p "$UPLOAD_ROOT/$dir"
done

chmod -R 775 "$UPLOAD_ROOT" 2>/dev/null || true
echo "Upload directories ready under $UPLOAD_ROOT"
