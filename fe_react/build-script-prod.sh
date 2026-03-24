#! /bin/bash
# remove current container and image
docker rm -f elearning_support_fe
docker rmi -f leopepe2012/elearning_support_system:fe-prod
# rebuild image
docker build -f ./Dockerfile --tag=leopepe2012/elearning_support_system:fe-prod .
# push image to Docker Registry
docker push leopepe2012/elearning_support_fe:1.0.0
# run the container using docker-compose
docker compose -f docker-compose-production.yml up -d
# view logs after running the container
docker logs -f elearning_support_fe --tail 500