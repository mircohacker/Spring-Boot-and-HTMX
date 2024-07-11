#!/usr/bin/env bash
cd "$(dirname -- "$0")"

rsync -v ../dist/index-imports-only.html ../../src/main/resources/templates/fragments/vue-imports.ftlh
rsync -vr --delete ../dist/assets/ ../../src/main/resources/static/assets/