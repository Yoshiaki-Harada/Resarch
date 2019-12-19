#!/bin/bash

set -eu

PROJECT_DIR=$(pwd)
BUILD_DIR="/tmp/buildlatex-"$(date "+%Y%m%d-%H%M%S")

mkdir "${BUILD_DIR}"
cp -pR "${PROJECT_DIR}/src" "${BUILD_DIR}/"
cp "${PROJECT_DIR}/.latexmkrc" "${BUILD_DIR}/"
cd "${BUILD_DIR}/src"
pandoc -F pandoc-crossref \
  --top-level-division=chapter \
  ./*.md -o ./main.tex
perl -0pi -e 's/\\\[\n\\begin\{align\}/\\begin\{align\}/m' main.tex
perl -0pi -e 's/\\end\{align\}\n\\\]/\\end\{align\}/m' main.tex
perl -pi -e "s/fig.~/Fig.~/g" main.tex #pandocのymlが効かなかった
sed -i -e "s|\begin{figure}|\begin{figure}[H]|g" main.tex
mv "${BUILD_DIR}/src/template/boilerplate.tex" "${BUILD_DIR}/src"
cp -pR  "${BUILD_DIR}/src/template/abstract/`*`" "${BUILD_DIR}/src"
cp -pR  "${BUILD_DIR}/src/template/title/`*`" "${BUILD_DIR}/src"
cp -pR  "${BUILD_DIR}/src/template/others/`*`" "${BUILD_DIR}/src"

latexmk boilerplate
mv ./boilerplate.pdf "${PROJECT_DIR}/dest/output.pdf"
rm -rf "${BUILD_DIR}"
