docker rm -f elearning_support_fe
docker rmi -f leopepe2012/elearning_support_system:fe-prod
docker build -f ./Dockerfile --tag=leopepe2012/elearning_support_system:fe-prod .
#docker build -f ./Dockerfile-mpec --tag=leopepe2012/elearning_support_system:fe-mpec .