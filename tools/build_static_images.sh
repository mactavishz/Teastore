#!/bin/bash
JAR_FILE="../utilities/tools.descartes.teastore.imagegenerator/target/tools.descartes.teastore.imagegenerator-jar-with-dependencies.jar"

# get the absolute path of the jar file and the setup sql file
JAR_FILE=$(realpath $JAR_FILE)
DB_SIZES=("small" "mid" "large")

rm -rf ../services/tools.descartes.teastore.image/generated_images/
mkdir -p ../services/tools.descartes.teastore.image/generated_images/
chmod -R 755 ../services/tools.descartes.teastore.image/generated_images/

for size in "${DB_SIZES[@]}"
do
  echo "Generating static images for the $size database..."
  # use a java 17 docker container to run the jar file to generate the data for the database with different arguments: small, mid, large
  mkdir -p ./generated_images/db-"$size"
  chmod -R 755 ./generated_images/db-"$size"
  docker run --rm \
    -v "$JAR_FILE":/app.jar \
    -v "./generated_images/db-$size:/images_static" \
    openjdk:17 java -jar /app.jar "$size"
  echo "Static images for the $size database are generated successfully."
  echo "Moving static images to the image service..."
  rm -rf ../services/tools.descartes.teastore.image/generated_images/db-"$size"
  cp -R ./generated_images/db-"$size" ../services/tools.descartes.teastore.image/generated_images/
  chmod -R 777 ../services/tools.descartes.teastore.image/generated_images/db-"$size"
done

echo "All static images are generated successfully."
echo "Removing the temporary generated images directory..."
rm -rf ./generated_images

