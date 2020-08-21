export WA_API_VERSION="2.23.4"
export M2_HOME="/Users/vaneetkumar/softwares/buildtools/maven/apache-maven-3.6.3"
PATH="${M2_HOME}/bin:${PATH}"
PATH="/Users/vaneetkumar/softwares/databases/mongodb/mongodb-macos-x86_64-4.2.8/bin:${PATH}"
PATH="/Users/vaneetkumar/softwares/queue/kafka/kafka_2.12-2.5.0/bin:${PATH}"
PATH="/Users/vaneetkumar/softwares/databases/mongodb/mongodb-osx-x86_64-3.6.2/bin:${PATH}"
PATH="/Users/vaneetkumar/softwares/bigdata/storm/apache-storm-2.2.0/bin:${PATH}"
export PATH


######### Project Paths #######################
alias tornrcode='cd /Users/vaneetkumar/workspace/projects/paytm/repos/RNR/reviews-and-ratings'
###############################################

######### Local Services Up/Down #######################
alias zookeeperup='zookeeper-server-start.sh ~/softwares/queue/kafka/kafka_2.12-2.5.0/config/zookeeper.properties'
alias zookeeperdown='zookeeper-server-stop.sh'
alias kafkaup='kafka-server-start.sh ~/softwares/queue/kafka/kafka_2.12-2.5.0/config/server.properties'
alias kafkadown='kafka-server-stop.sh'
alias kafkaconnectup='connect-distributed.sh ~/softwares/queue/kafka/kafka_2.12-2.5.0/config/connect-distributed.properties'
alias kafkaconnectup1='connect-distributed.sh ~/softwares/queue/kafka/kafka_2.12-2.5.0/config/connect-distributed1.properties'
alias mongoup='mongod --dbpath /data/db --replSet rs0'
###############################################

######### RNR Remote server login #######################
alias tornrstgproducerapp='ssh vaneet_26129@10.254.16.199'
alias tornrstgprocessorapp='ssh vaneet_26129@10.254.27.41'
alias tornrstgkafkaconnect='ssh vaneet_26129@10.254.17.25'
###############################################
