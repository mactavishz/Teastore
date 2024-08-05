## Kubernetes Information

### 1. Namespace:

- Teastore resources are in the namespace `teastore`.
- Prometheus + Grafana are in the namespace `monitoring-tea`.

Both of them are in the same cluster and managed by `kustomize`.

### 2. Images:

These are all images built for refactored TeaStore:
```
macsalvation/teastore-webui:v0.2.0-open-liberty-webui
macsalvation/teastore-recommender:v0.2.0
macsalvation/teastore-persistence:v0.2.0
macsalvation/teastore-image:v0.2.0-dbmid
macsalvation/teastore-db:v0.2.0-dbmid
macsalvation/teastore-auth:v0.2.0
```

### 3. Ports:
All components are exposed by `NodePort` to the outside for testing purpose.

| Component | IP:Port |
|:---------|:---------|
| webui | localhost:30080 |
| recommender | localhost:30082 |
| persistence | localhost:30083 |
| image | localhost:30084 |
| auth | localhost:30085 |

### 4. Data persistence:
Note that 'emptyDir' method is chosen to store data, so the data will be deleted after retart.


## How to run the refactored TeaStore on Kubernetes

#### 1. Set environment variables
Because we simulate the image-CDN with nginx (within cluster), you must specify the cluster's external IP in the file `kubernetes/env/webui-env.txt`.

e.g. `IMAGE_CDN_HOST=< cluster external IP >`


#### 2. Start TeaStore with kustomize:
```bash
cd kubernetes
kubectl apply -k ./
```
- HPA: HPA could be enabled for webui/recommender/auth/image/persistence in TeaStore.
In order to enable HPA, you need to uncomment `- hpa.yaml` under `resources` section in `kubernetes/kustomization.yaml` .


#### 3. Start Prometheus + Grafana with kustomize:
```bash
cd kuberetes/prometheus_grafana
kubectl apply -k ./
```
- Data source: Prometheus and Grafana are preconfigured to be connected, means that Grafana uses Prometheus as the default data source.

- Prometheus is authorized through `RBAC`（Role Based Access Control）to monitor across namespaces.

- Prometheus’s configuration file is `kubernetes/prometheus_grafana/config/prometheus.yaml`. In this file, the scraping target is set.

- Grafana dashboard: `kubernetes/prometheus_grafana/dashboards/*.json`

- Grafana default username and password are `admin` and `admin`.


#### 4. Start Kube-state-metrics:
```bash
cd kubernetes/kube-state-metrics
kubectl apply -k ./
```

#### 5. Metrics-server:
Metrics-server is also needed, please check if your cluster already has it.<br/>
Many kubernetes distributions have it by default, e.g. K3S.<br/>
If not, you can install it by:
```bash
cd kubernetes/metrics-server
kubectl apply -f ./
```
