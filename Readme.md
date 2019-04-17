# Set up for a project using spark streaming

This repository serves as a start example for a streaming application using spark streaming with docker and as example for [this post](https://medium.com/@salohyprivat/monitor-spark-streaming-with-prometheus-5b0eff5e318d) on medium, where we want to monitore the metrics of this streaming application using Prometheus and Grafana.

## Build

```
   docker build -t spark-app .
```

## Run the Image and let the metrics sink to graphite

```
docker run --name movebis-spark-app \
       -e ENABLE_INIT_DAEMON=false \
       --link spark-master:spark-master \
       -v path-to-your/metrics.properties:/spark/conf/metrics.properties \
       --network=dev_setup_default \
       spark-app
```

Note that we have mount the `metrics.properties` here.

Read [this](https://medium.com/@salohyprivat/monitor-spark-streaming-with-prometheus-5b0eff5e318d) for the rest.
