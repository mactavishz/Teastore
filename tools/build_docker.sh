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

if [[ $DEPLOY = 1 ]]; then
	docker run -it --rm --privileged tonistiigi/binfmt --install all
	docker buildx create --use --name mybuilder
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-db${tag}" ../utilities/tools.descartes.teastore.database/ ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-persistence${tag}" ../services/tools.descartes.teastore.persistence/ ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-image${tag}" ../services/tools.descartes.teastore.image/ ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-webui${tag}" ../services/tools.descartes.teastore.webui/ ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-auth${tag}" ../services/tools.descartes.teastore.auth/ ${push_flag:+--push}
	docker buildx build --platform ${DOCKER_PLATFORMS} -t "${registry}teastore-recommender${tag}" ../services/tools.descartes.teastore.recommender/ ${push_flag:+--push}
	docker buildx rm mybuilder
else
  cd .. && docker compose build --no-cache
fi

