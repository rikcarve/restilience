# restilience
Small client library for resilient REST calls

docker run -d --name registrator --net=host -v //var/run/docker.sock:/tmp/docker.sock gliderlabs/registrator:latest -ip 192.168.99.100 etcd://192.168.99.100:2379/services

docker run -d --name etcd -p 4001:4001 -p 7001:7001 quay.io/coreos/etcd etcd -listen-client-urls http://0.0.0.0:4001,http://0.0.0.0:2379 -advertise-client-urls http://0.0.0.0:4001,http://0.0.0.0:2379

docker run -d --name etcd -p 2379:2379 -p 2380:2380 quay.io/coreos/etcd etcd -listen-client-urls http://0.0.0.0:2379 -advertise-client-urls http://0.0.0.0:2379
