##############################Script to Build Mybookings project and deploy it in docker container##########################
#Holding MyBookings projects directory path system

##############################Variable declarations########################################################################
mbProjectPath="/home/vaneet.kumar/MyBookings/MyBookings"
mbProject="_Additional_Details_17.0"
seperator="/"
fullPathToMBProject=$mbProjectPath$mbProject$seperator
dockerBuildPath="/home/vaneet.kumar/docker/build/"
relWarPath="target/ROOT.war"
mbWebApp="MyBookingsWebApp$mbProject$seperator"
absMbWebApp=$fullPathToMBProject$mbWebApp
echo "Action variable is  $1 "


#############################Build Section################################################################################
if [ -z "$1" -o "$1" = "b" ] ; then
####Declaring MyBookings Project base names.
mbCommon="MyBookingsCommon$mbProject$seperator"
mbDaoInterface="MyBookingsDaoInterface$mbProject$seperator"
mbDaoImpl="MyBookingsDaoImpl$mbProject$seperator"
mbServiceInterface="MyBookingsServiceInterface$mbProject$seperator"
mbServiceImpl="MyBookingsServiceImpl$mbProject$seperator"
mbManagerInterace="MyBookingsManager$mbProject$seperator"
mbManagerImpl="MyBookingsManagerImpl$mbProject$seperator"

absMbCommon=$fullPathToMBProject$mbCommon
absMbDaoInterface=$fullPathToMBProject$mbDaoInterface
absMbDaoImpl=$fullPathToMBProject$mbDaoImpl
absMbServiceInterface=$fullPathToMBProject$mbServiceInterface
absMbServiceImpl=$fullPathToMBProject$mbServiceImpl
absMbManagerInterface=$fullPathToMBProject$mbManagerInterace
absMbManagerImpl=$fullPathToMBProject$mbManagerImpl

#############################Printing all Mb Project absolute paths to be built############################################
echo "Absolute path to MyBookings project to build is: $fullPathToMBProject"
echo "Absolute paths to all individul Mybookings projects to be built are:"
echo $absMbCommon
echo $absMbDaoInterface
echo $absMbDaoImpl
echo $absMbServiceInterface
echo $absMbServiceImpl
echo $absMbManagerInterface
echo $absMbWebApp

############################Building Maven Projects######################################################################
echo "Enter Indicated numbers to start building projects"
echo " MyBookingsCommon : 1 "
echo " MyBookingsDaoInterface : 2 "
echo " MyBookingsDaoImpl : 3 "
echo " MyBookingsServiceInterface : 4 "
echo " MyBookingsServiceImpl : 5 "
echo " MyBookingsManager : 6 "
echo " MyBookingsManagerImpl : 7 "
echo " MyBookingsWebApp : 8 "
echo " "
echo " Please Enter Project Sequence Number: "
read projectSeq 
echo "Project Sequence is $projectSeq "

if [ 1 -eq  $projectSeq ] ; then
cd $absMbCommon
mvn clean install
fi

if [ 2 -ge  $projectSeq ] ; then
cd $absMbDaoInterface
mvn clean install
fi

if [ 3 -ge $projectSeq ] ; then
cd $absMbDaoImpl
mvn clean install
fi

if [ 4 -ge $projectSeq ] ; then
cd $absMbServiceInterface
mvn clean install
fi  

if [ 5 -ge  $projectSeq ] ; then
cd $absMbServiceImpl
mvn clean install
fi

if [ 6 -ge $projectSeq ] ; then
cd $absMbManagerInterface
mvn clean install
fi

if [ 7 -ge $projectSeq ] ; then
cd $absMbManagerImpl
mvn clean install
fi

if [ 8 -ge $projectSeq ] ; then
cd $absMbWebApp
mvn clean install
fi

fi
#Build section complete.
###################################Deployment begins###########################################################

if [ -z "$1" -o "$1" = "d" ] ; then
cd $dockerBuildPath
rm -rf ROOT.war

echo "###########################"
echo "Copying War file from $absMbWebApp$relWarPath to $dockerBuildPath"
cp $absMbWebApp$relWarPath $dockerBuildPath

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











