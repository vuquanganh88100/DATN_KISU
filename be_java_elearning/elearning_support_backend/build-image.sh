docker rm -f elearning_support_backend
docker rmi -f leopepe2012/elearning_support_system:java-backend
docker build -f ./Dockerfile --tag=leopepe2012/elearning_support_system:java-backend .