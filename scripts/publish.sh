#!/usr/bin/env bash

# Publish the website.

set -ex

#git pull -r
./gradlew clean check javadocJar asciidoctor install
git checkout gh-pages

for proj in sdsai-common sdsai-itrex sdsai-itrex-shell sdsai-net sdsai-sandbox
do
	rm -fr "javadocs/$proj" || true
	mv "$proj/build/docs/javadoc" "javadocs/$proj" || true

	rm -fr "docs/$proj" || true
	mv "$proj/build/asciidoc/*html" "docs/$proj" || true

	rm -f "info.properties" || true
	mv "$proj/build/info.properties" "$proj.properties" || true
done

echo Page updated. Make final edits.
