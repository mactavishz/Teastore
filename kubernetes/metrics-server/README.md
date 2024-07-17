# metrics-server

Metrics-server is needed to enable the Horizontal Pod Autoscaler in Kubernetes.

**You don't need to install this if you are using some cluster like K3S because it is pre-installed.**

This is needed if you are running kubernetes in docker-desktop and added `--kubelet-insecure-tls` to fix error.

```bash
# install metrics-server
kubectl apply -f ./

# check the status
kubectl top nodes
kubectl top pods
```


### Reference
https://github.com/kubernetes-sigs/metrics-server