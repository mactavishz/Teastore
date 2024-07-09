#!/bin/bash

cd ..

mvn clean install -DskipTests

if [ $? -ne 0 ]; then
  echo "Maven build failed. Exiting..."
  exit 1
fi

cd tools || exit 1

./build_db_dumps.sh

if [ $? -ne 0 ]; then
  echo "Database dump generation failed. Exiting..."
  exit 1
fi

cd ..

mvn -am -pl :imagegenerator install -DskipTests

if [ $? -ne 0 ]; then
  echo "Maven build image generator failed. Exiting..."
  exit 1
fi

cd tools || exit 1

./build_static_images.sh

if [ $? -ne 0 ]; then
  echo "Static image generation failed. Exiting..."
  exit 1
fi

exit 0