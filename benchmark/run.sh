#!/bin/bash

BASE_URL=""
WORKLOAD="test"
PROTOCOL="http"
WEBUI_PORT=${WEBUI_PORT:-"30080"}
RECOMMENDER_PORT=${RECOMMENDER_PORT:-"30082"}
PERSISTENCE_PORT=${PERSISTENCE_PORT:-"30083"}
IMAGE_PORT=${IMAGE_PORT:-"30084"}
AUTH_PORT=${AUTH_PORT:-"30085"}

declare -a TEST_TARGETS=(
	"recommender"
	"persistence"
	"image"
	"auth"
	"webui"
)

while [[ $# -gt 0 ]]; do
	key="$1"
	case $key in
	# add help message using -h or --help
	-h | --help)
		echo "Usage: $0 [options]"
		echo "Options:"
		echo "  -H, --host <hostname>        The base URL of the system under test. Default: localhost"
		echo "  -p, --protocol <http|https>  The protocol to use for the test. Default: http"
		echo "  -w, --workload <name>        The workload name to run, available workloads: test, average, stress, breakpoint. Default: test"
		echo "  -t, --targets <list>         The list of comma seperated target services to test. Default: recommender,persistence,image,auth,webui"
		echo "  -c --compress                Compress the reports folder after the test"
		exit 0
		;;
	-H | --host)
		HOST=$(echo $2 | sed 's:/*$::')
		# if the host is not specified, use the default host
		BASE_URL=${HOST:-"localhost"}
		shift
		shift
		;;
	-p | --protocol)
		PROTOCOL="$2"
		if [ $PROTOCOL != "http" ] && [ $PROTOCOL != "https" ]; then
			echo "Invalid protocol, only http or https is allowed"
			exit 1
		fi
		shift
		shift
		;;
	-w | --workload)
		WORKLOAD="$2"
		shift
		shift
		;;
	-t | --targets)
		TARGETS="$2"
		# split the targets by comma
		# if no targets are specified, use the default targets
		IFS=',' read -r -a TEST_TARGETS <<<"$TARGETS"
		shift
		shift
		;;
	-u | --user)
		USER="$2"
		shift
		shift
		;;
	-c | --compress)
		COMPRESS=true
		shift
		shift
		;;
	*)
		shift
		;;
	esac
done

echo "Running benchmark on $BASE_URL"
echo "Selected workload: $WORKLOAD"
echo "Test target services:" "${TEST_TARGETS[@]}"

# loop through the test targets
for target in "${TEST_TARGETS[@]}"; do
	# loop the directory and run the test
	test_dir="tests/api/$target"
	if [ $target == "webui" ]; then
		test_dir="tests/webui"
	fi
	for file in $test_dir/*.js; do
		echo $file
		mkdir -p reports/$target/$WORKLOAD
		echo "Running test $file"
		k6 run \
			-e AUTH_BASE_URL="$PROTOCOL://$BASE_URL:$AUTH_PORT" \
			-e RECOMMENDER_BASE_URL="$PROTOCOL://$BASE_URL:$RECOMMENDER_PORT" \
			-e PERSISTENCE_BASE_URL="$PROTOCOL://$BASE_URL:$PERSISTENCE_PORT" \
			-e IMAGE_BASE_URL="$PROTOCOL://$BASE_URL:$IMAGE_PORT" \
			-e WEBUI_BASE_URL="$PROTOCOL://$BASE_URL:$WEBUI_PORT" \
			-e USER="$USER" \
			-e K6_WEB_DASHBOARD="true" \
			-e K6_WEB_DASHBOARD_EXPORT=reports/$target/$WORKLOAD/$(basename ${file%.*}).html \
			-e WORKLOAD="$WORKLOAD" \
			--out csv=reports/$target/$WORKLOAD/$(basename ${file%.*}).csv \
			"$file"
		if [ $WORKLOAD != "test" ]; then
			# wait for 5 mins to let the system cool down
			sleep 300
		fi
	done
done

# compress the report folder with timestamp using xz to have a high compression ratio
if [ $COMPRESS ]; then
	echo "Compressing the reports folder"
	tar --exclude='.DS_Store' -cJf reports-$(date +%Y%m%d%H%M%S).tar.xz reports
fi
