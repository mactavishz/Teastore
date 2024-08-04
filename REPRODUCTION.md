# Reproduction of the runnable refactored system

This document describes how to develop, deploy and reproduce the evaluation of the runnable refactored system of the TeaStore project.

## Run the system locally

The following steps are only used to run the system locally via docker compose. If you want to reproduce the benchmarking results of system, please refer to the next section.

```bash
# step 1: build the databases, corresponding static images and Java artifacts
cd ./tools && ./build.sh && cd ..

# step 2: build the docker images using the mid size database
docker compose build --no-cache --build-arg DB_SIZE=mid

# step 3: run the system via docker compose
docker compose up -d
```

## Evaluation environment

The experiment was conducted on a Kubernetes cluster with 4 nodes on [Linode](https://www.linode.com/) with the following configuration:

- 8 vCPU cores (2 cores per node)
- 16 GB RAM (4 GB per node)
- 320 GB SSD storage (80 GB per node)
- Region: Frankfurt, Germany
- Kubernetes version: 1.29

This is the same configuration as the one used in the baseline evaluation of the TeaStore project.

## Benchmarking

### Raw benchmark data

The raw benchmark data for the report is compressed and stored in the `benchmark/reports-ol-webui.tar.xz`. It contains raw csv files of time series data that could be consumed by databases such as [InfluxDB](https://www.influxdata.com/) or [Prometheus](https://prometheus.io/) and generated charts (in PNGs) for each benchmark test as well as a summary of the results in `stdout.txt`.

As mentioned in the final report, due to changes of the benchmarking scripts, the original version is re-benchmarked and the results are stored in the `benchmark/reports-old-versions-v2.tar.xz`.

The previous version of the benchmarking results are stored in the `benchmark/reports-old-version.tar.xz` file.

You can also open the HTML files to check the benchmark results in a more human-readable format.

### Reproducing the benchmark

```bash
cd benchmark

# print the help message
./run.sh -h

# run the benchmark with the average workload for the recommender, persistence, image, auth, and webui services and store the report in the `reports-reproduction` directory
./run.sh -H 139.162.147.254 -p http -w average -t recommender,persistence,image,auth,webui j-o reports-reproduction

# run the benchmark with stress workload for the recommender, persistence, image, auth, and webui services
./run.sh -H 139.162.147.254 -p http -w stress -t image,auth,persistence,recommender,webui -o reports-reproduction

# if you want to compress the generated data after th execution, use the -c flag
./run.sh ... -c

# if you want to skip the sleeping phase after the benchmark, use the -n flag
./run.sh ... -n

# Please check the help message for more information
./run.sh -h
```

The `-H` flag is used to specify the target host (Kubernetes cluster's node IP), the `-w` flag is used to specify the workload, and the `-t` flag is used to specify the target services. The `-c` flag is used to compress the generated data. The `-o` flag is used to specify the output directory. The `-n` flag is used to skip the sleeping phase after the benchmark.

### Benchmarking scripts

All benchmarking scripts are implemented using [K6](https://k6.io/) and are located in the `benchmark/tests` directory.

### Generating the Graphs

We have provided a python script to generate the graphs for the reports generated from our benchmarking scripts.

```bash
cd benchmark

# set up the virtual environment
python -m venv .venv

# install the dependencies
pip install -r ./requirements.txt

# activate the virtual environment
source .venv/bin/activate

# check if the python is using the virtual environment
which python

# generate the graphs for the reports in the `reports` directory
python ./charts/generate.py

# generate the graphs for a custom directory
python ./charts/generate.py ./my-dir > ./my-dir/stdout.txt
```

The directory of the reports could be located anywhere, as long as you provide the correct path to the script. The default directory is `./reports`.

### Caveats

The benchmark script is designed to test against the deployment on a Kubernetes cluster. It is not designed to test against the local deployment of the TeaStore system. Each services are required to expose a certain port:

- `recommender`: 30082
- `persistence`: 30083
- `image`: 30084
- `auth`: 30085
- `webui`: 30080

You can use the configuration files we provided in the `kubernetes` directory to deploy the TeaStore system on a Kubernetes cluster.

If you wish to test against a local deployment, you need to specify some environment variables to the benchmark script to override the default ports:

```bash
# for example, test against the local deployment using the docker compose configuration we provided with the following command
WEBUI_PORT=8080 IMAGE_PORT=8082 PERSISTENCE_PORT=8084 AUTH_PORT=8083 RECOMMENDER_PORT=8081 ./run.sh -H localhost -w average -t webui,image,auth,persistence,recommender -o reports-local
```

**IMPORTANT**: Each test is designed to run for 5 minutes with 5 minutes pause between each other, unless you have specified the `-n` flag. Only the test for the `webui` is stateful, so we recommend to re-deploy the all the services after finishing one workload test.

If you wish to get a fairer benchmark result, we recommend to warm up the system before running the benchmark test, this could be done by running the `breakpoint` workload on `webui` for some time.

```bash
# warm up the system
./run.sh -H 139.162.147.254 -p http -w breakpoint -t webui -o reports-warmup
```

## Kubernetes

### Kubernetes Infomation:

- Teastore resources are in the namespace `teastore`.
- Prometheus + Grafana are in the namespace `monitoring-tea`.

Both of them are in the same cluster and managed by `kustomize`.

### TeaStore:

This is designed to run TeaStore in Kubernetes and get test data from it.

All images are rebuilt to expose 9464 and pass certain environment variables for testing.

These are all images built for refactored TeaStore:
```
macsalvation/teastore-webui:v0.2.0-open-liberty-webui
macsalvation/teastore-recommender:v0.2.0
macsalvation/teastore-persistence:v0.2.0
macsalvation/teastore-image:v0.2.0-dbmid
macsalvation/teastore-db:v0.2.0-dbmid
macsalvation/teastore-auth:v0.2.0
```

### HPA(Horizontal Pod Autoscaler):
HPA is enabled for recommender/auth/image/webui/persistence in TeaStore.

If you run it on the `docker-desktop`, you need to install metrics-server first.

*If you run it on e.g. `K3S`, you can ignore the installation of metrics-server.*

```bash
cd metrics-server
kubectl apply -f ./
```


All components are exposed by `NodePort` to the outside for testing purpose.

### Mapping:
| Component | IP:Port |
|:---------|:---------|
| webui | localhost:30080 |
| registry | localhost:30081 |
| recommender | localhost:30082 |
| persistence | localhost:30083 |
| image | localhost:30084 |
| auth | localhost:30085 |




# Prometheus:

Prometheus and Grafana are used to monitor TeaStore.

Prometheus is authorized through `RBAC`（Role Based Access Control） to monitor across namespaces.

Prometheus’s configuration file is in `prometheus_grafana/config/prometheus.yaml`.
In this file, the scraping target is set.

# Grafana
Grafana connects to Prometheus `http://prometheus-service.monitoring.svc:9090` to get data and visualize it.

Grafana default username and password are `admin` and `admin`.


# How to run with kustomize:

Run TeaStore with kustomize:
```bash
# start
kubectl apply -k ./

# refactored
kubectl apply -k . --set-string IMAGE_CDN_HOST=image-cdn.domain.com

# update
kubectl apply -k ./

# stop
kubectl delete -k ./
```

Run Prometheus + Grafana with kustomize:
```bash
# go to the directory
cd prometheus_grafana

# start
kubectl apply -k ./

# update
kubectl apply -k ./

# stop
kubectl delete -k ./
```


# Data persistence
Note that 'emptyDir' method is chosen to store data, so the data will be deleted after retart.

