# Gateway API durch Traefik

## Namespace traefik

Wenn _Traefik_ als _Gateway API_ installiert wird (s. unten), dann wird es im
Namespace `traefik` installiert, der einmalig angelegt werden muss, falls bei
der initialen Installation zu Semesterbeginn noch nicht geschehen:

```shell
    kubectl create namespace traefik
```

## Helm Repository

_Traefik_ wird mit einem _Helm-Chart_ installiert. Dieses Helm-Chart wird durch
Traefik in einem eigenen Repository bereitgestellt, welches einmalig in die lokale
Helm-Installation eingetragen werden muss.

```shell
    helm repo add traefik https://traefik.github.io/charts
    helm repo update
```

Danach ist das zusätzliche Repository für Helm bekannt:

```shell
    helm repo list
```

## HTTPS

Ein _Gateway API_ sollte sinnvollerweise mit _HTTPS_ benutzt werden können.
Dazu muss im Kubernetes-Cluster zunächst ein privater Schlüssel mit zugehörigem
Zertifikat als ein _Secret_ gemäß Kubernetes installiert werden. Das _Secret_
wird im zuvor angelegten Namespace `traefik` installiert, der später für die
Installation von _Traefik_ genutzt wird.

```shell
    kubectl create secret tls gateway-tls --cert=certificate.crt --key=private-key.pem -n traefik
```

Ob das _Secret_ erfolgreich angelegt wurde, lässt sich folgendermaßen überprüfen:

```shell
    kubectl describe secret gateway-tls -n traefik
```

## Installation mit dem Helm-Chart

Jetzt kann _Traefik_ mit einem sogenannten _Helm-Chart_ installiert werden.
Dieses Helm-Chart wird durch Traefik in einem eigenen Repository bereitgestellt,
welches zunächst in die lokale Helm-Installation eingetragen werden muss.
Danach kann das Helm-Chart mit den Konfigurationswerten aus der Datei `values-traefik.yaml`
(s. unten) im Namespace `traefik` (s. oben) installiert werden.

```shell
    helm install traefik traefik/traefik -f traefik.yaml -n traefik
```

## Installation mit dem Helm-Chart

Ob die Installation erfolgreich war, lässt sich mit folgenden Kommandos überprüfen:

```shell
    helm list -n traefik
    kubectl describe gatewayclass traefik -n traefik
    kubectl describe gateway traefik-gateway -n traefik
```

## Deinstallation des Helm-Charts

Das Helm-Chart für _Traefik_ wird folgendermaßen deinstalliert:

```shell
    helm uninstall traefik -n traefik
```

## Routen für das Gateway API

Routen für _Traefik_ als Gateway API werden folgendermaßen installiert:

```shell
    kubectl apply -f route.traefik.yaml
```

Diese Route(n) kann man wie folgt inspizieren:

```shell
    kubectl describe HTTPRoute blumenladen-route -n traefik
```

Mit folgendem Kommando kann man die Route(n) deinstallieren:

```shell
    kubectl delete -f route.traefik.yaml
```
