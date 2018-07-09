##############################Script to Build Mybookings project and deploy it in docker container##########################
#Holding MyBookings projects directory path system

##############################Variable declarations########################################################################
multiModulembProjectPath="/home/vaneet.kumar/MyBookings/MyBookings_Service/Mybookings-Service"
seperator="/"
fullPathToMBProject=$multiModulembProjectPath$seperator
dockerBuildPath="/home/vaneet.kumar/docker/build/"
relWarPath="MyBookingsWebApp/target/ROOT.war"

#############################Build Section################################################################################
if [ -z "$1" -o "$1" = "b" ] ; then
#############################Printing Multi module Mb Project absolute paths to be built############################################
echo "Absolute path to MyBookings project to build is: $fullPathToMBProject"

############################Building Maven Projects####################################################################
cd $fullPathToMBProject
############################Prompt User to select Git branch.
echo "Enter Project Git Branch to build."
############################Showing Git branches of Mybookings Project
git branch
############################Taking branch to  be checkout and built for project from user.
read branch
############################Switching to branch input.
git checkout $branch

############################Building Maven Maven Project
mvn clean install
fi
#Build section complete.

###################################Deployment begins###########################################################
if [ -z "$1" -o "$1" = "d" ] ; then
cd $dockerBuildPath
rm -rf ROOT.war

echo "###########################"
echo "Copying War file from $fullPathToMBProject$relWarPath to $dockerBuildPath"
cp   $fullPathToMBProject$relWarPath $dockerBuildPath

echo "#######################"
echo "Stopping All containers"
sudo docker rm -f mybookings
sudo docker rm -f mybookinigsRedisCacheServer

echo "######################"
echo "Deleting mybookings:1.0 image."
sudo docker rmi mybookings:1.0

echo "######################"
echo "Building mybookings:1.0 again ."
sudo docker build -t mybookings:1.0 .

echo "######################"
echo "Running mybookinigsRedisCacheServer container"
sudo docker run -p 2864:6379 --name mybookinigsRedisCacheServer --network mynetwork  -d -v /home/vaneet.kumar/docker/mapped-volumes/redis/data-dir:/data redis redis-server --requirepass myb00kingc@che

echo "######################"
echo "Running mybookings container "
sudo docker run -p 9090:8080 -p 8000:8000 --name mybookings --network mynetwork -v /home/vaneet.kumar/run/tomcat/logs:/data/yatra/run/tomcat/logs -v /home/vaneet.kumar/docker/YT/var/log/myaccount:/data/yatra/YT/var/log/myaccount -v /etc/hosts:/etc/hosts -v /home/vaneet.kumar/YT/environment.properties:/data/yatra/YT/environment.properties -e train_services_environment=properties/qa -e environment=dev -it  mybookings:1.0
fi










