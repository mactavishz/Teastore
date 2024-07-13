#!/bin/bash
DOCKER_PLATFORMS='linux/amd64,linux/arm64'
registry='' # e.g. 'descartesresearch/'
tag=''
DEPLOY=0

print_usage() {
	printf "Usage: docker_build.sh [-p] [-d] [-r REGISTRY_NAME] [-t tag]\n"
}

while getopts 'dpr:t:' flag; do
	case "${flag}" in
	d) DEPLOY=1 ;;
	p) push_flag='true' ;;
	r) registry="${OPTARG}" ;;
	t) tag="${OPTARG}" ;;
	*)
		print_usage
		exit 1
		;;
	esac
done

if [[ ! -z $tag ]]; then
	tag=":${tag}"
fi

DB_SIZES=("small" "mid" "large")

DB_PATH="../utilities/tools.descartes.teastore.database/"
IMAGE_PATH="../services/tools.descartes.teastore.image/"
PERSISTENCE_PATH="../services/tools.descartes.teastore.persistence/"
WEBUI_PATH="../services/tools.descartes.teastore.webui/"
AUTH_PATH="../services/tools.descartes.teastore.auth/"
RECOMMENDER_PATH="../services/tools.descartes.teastore.recommender/"

if [[ $DEPLOY = 1 ]]; then
	docker run -it --rm --privileged tonistiigi/binfmt --install all
	docker buildx create --use --name mybuilder

  for size in "${DB_SIZES[@]}"
  do
    docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-db${tag}-db${size}" --build-arg DB_SIZE=${size} ${DB_PATH} ${push_flag:+--push}
    docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-image${tag}-db${size}" --build-arg DB_SIZE=${size} ${IMAGE_PATH} -f "${IMAGE_PATH}Dockerfile.prod" ${push_flag:+--push}
  done
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-persistence${tag}" ${PERSISTENCE_PATH} -f "${PERSISTENCE_PATH}Dockerfile.prod" ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-webui${tag}" ${WEBUI_PATH} -f "${WEBUI_PATH}Dockerfile.prod" ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-auth${tag}" ${AUTH_PATH} -f "${AUTH_PATH}Dokcerfile.prod" ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-recommender${tag}" ${RECOMMENDER_PATH} -f "${RECOMMENDER_PATH}Dockerfile.prod" ${push_flag:+--push}
	docker buildx rm mybuilder
else
  cd .. && docker compose build --no-cache
fi

