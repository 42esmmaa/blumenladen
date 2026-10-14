# Gateway API durch kgateway

## Namespace kgateway-system

Wenn _kgateway_ als _Gateway API_ installiert wird (s. unten), dann wird es im
Namespace `kgateway-system` installiert, der einmalig angelegt werden muss,
falls noch nicht bei der initialen Installation zu Semesterbeginn geschen:

```shell
    kubectl create namespace kgateway-system
```

## CRD für das Gateway API

Zuerst muss man die _CRD_ (= Custom Resource Definition) für das Gateway API_ installieren:

```shell
    kubectl apply -f https://github.com/kubernetes-sigs/gateway-api/releases/download/v1.4.0/standard-install.yaml
```

## CRDs für kgateway

Jetzt wird das _Helm-Chart_ mit den _CRD_s für _kgateway_ installiert:

```shell
    helm install kgateway-crds oci://cr.kgateway.dev/kgateway-dev/charts/kgateway-crds --version v2.1.2 -n kgateway-system
```

Ob die CRDs erfolgreich installiert wurden, kann folgendermaßen überprüft werden:

```shell
    kubectl get crds
```

## Helm-Chart für kgateway

Abschließend wird das _Helm-Chart_ für _kgateway_ installiert:

```shell
    helm install kgateway oci://cr.kgateway.dev/kgateway-dev/charts/kgateway --version v2.1.2 -n kgateway-system
```

Ob _kgateway_ erfolgreich installiert wurde, kann folgendermaßen überprüft werden:

```shell
    kubectl get pods -n kgateway-system
    kubectl get gatewayclass kgateway -o yaml
```

## HTTPS

Ein _Gateway API_ sollte sinnvollerweise mit _HTTPS_ benutzt werden können.
Dazu muss im Kubernetes-Cluster zunächst ein privater Schlüssel mit zugehörigem
Zertifikat als ein _Secret_ gemäß Kubernetes installiert werden. Das _Secret_
wird im zuvor angelegten Namespace `kgateway-system` installiert.

```shell
    kubectl create secret tls gateway-tls --cert=certificate.crt --key=private-key.pem -n kgateway-system
```

Ob das _Secret_ erfolgreich angelegt wurde, lässt sich folgendermaßen überprüfen:

```shell
    kubectl describe secret gateway-tls -n kgateway-system
```

## Deinstallation

Das Helm-Chart für _kgateway_ wird folgendermaßen deinstalliert:

```shell
    helm uninstall kgateway -n kgateway-system
    helm uninstall kgateway-crds -n kgateway-system
```
