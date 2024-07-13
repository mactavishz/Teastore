# Kubernetes Infomation:

- Teastore resources are in the namespace `teastore`.
- Prometheus + Grafana are in the namespace `monitoring`.

Both of them are in the same cluster and managed by `kustomize`.

# TeaStore:

This is designed to run TeaStore in Kubernetes and get test data from it.

All images are rebuilt to expose 9464 and pass certain environment variables for testing.

These are all images built for origin branch:
```
johnzr/cnae-g8-teastore-recommender-origin:v1.0
johnzr/cnae-g8-teastore-auth-origin:v1.0
johnzr/cnae-g8-teastore-image-origin:v1.0
johnzr/cnae-g8-teastore-webui-origin:v1.0
johnzr/cnae-g8-teastore-persistence-origin:v1.0
johnzr/cnae-g8-teastore-registry-origin:v1.0
johnzr/cnae-g8-teastore-base-origin:v1.0
johnzr/cnae-g8-teastore-kieker-rabbitmq-origin:v1.0
johnzr/cnae-g8-teastore-db-origin:v1.0
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
