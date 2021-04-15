K8S Example Controller Application
==================================

Example application demonstrates how to run Job in K8S on incoming HTTP request, cleanup and save results

We can use k0s as simplest k8s implementation to run this application, no other requirements except docker installed:

```
docker run -it --rm --name k0s --hostname k0s --privileged -v /var/lib/k0s -p 6443:6443 k0sproject/k0s
```

Wait a bit until all pods in kube-system namespace will be running:

```
docker exec -it k0s kubectl get pods --all-namespaces
```

And copy configuration from container:

```
mkdir -p ~/.kube && docker exec k0s cat /var/lib/k0s/pki/admin.conf > ~/.kube/config
```

Now we are ready to comple and run example application (with local OpenJDK+gradle installed):

```
gradle run
```

As last step we can run some HTTP requests on another console to ensure if example application works:

```
curl -v -d 'HELLO' -H 'Content-Type: text/plain' localhost:8080
curl -v localhost:8080
```

Additional (but required for real k8s controller) steps includes:
- pack example application to docker container and push it to some registry
- run container in k8s with pod and rbac deployment descriptors
- allow external HTTP access to example application via port-forward or ingress
