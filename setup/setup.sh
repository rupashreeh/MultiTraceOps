#!/bin/bash

# Check if a command is available
command_exists() {
  command -v "$1" >/dev/null 2>&1
}

# Print a separator line for better output
print_separator() {
  echo "--------------------------------------------------------"
}

# Install Java 17
if ! command_exists java; then
  echo "Java 17 is not installed. Installing..."
  sudo apt update
  sudo apt install -y openjdk-17-jdk
else
  echo "Java 17 is already installed."
fi

# Install Maven 3.8.6
if ! command_exists mvn; then
  echo "Maven 3.8.6 is not installed. Installing..."
  sudo apt update
  sudo apt install -y maven
else
  echo "Maven 3.8.6 is already installed."
fi

# Install MySQL
if ! command_exists mysql; then
  echo "MySQL is not installed. Installing..."
  sudo apt update
  sudo apt install -y mysql-server
else
  echo "MySQL is already installed."
fi

# Install Zookeeper
if ! command_exists zookeeper-server-start; then
  echo "Zookeeper is not installed. Installing..."
  sudo apt update
  sudo apt install -y zookeeper
else
  echo "Zookeeper is already installed."
fi

# Install Kafka
if ! command_exists kafka-server-start; then
  echo "Kafka is not installed. Installing..."

  KAFKA_VERSION="3.3.1"
  KAFKA_DOWNLOAD_URL="https://downloads.apache.org/kafka/${KAFKA_VERSION}/kafka_2.12-${KAFKA_VERSION}.tgz"
  wget "${KAFKA_DOWNLOAD_URL}" -O /tmp/kafka.tgz
  sudo tar -xzf /tmp/kafka.tgz -C /usr/local/
  sudo mv "/usr/local/kafka_2.12-${KAFKA_VERSION}" /usr/local/kafka
  rm /tmp/kafka.tgz

  # Create a systemd service for Kafka using the existing kafka.service file
  sudo cp kafka.service /etc/systemd/system/kafka.service

  echo "Kafka has been successfully installed."
else
  echo "Kafka is already installed."
fi

# Create a systemd service for Zookeeper using the existing zookeeper.service file
if [ ! -f /etc/systemd/system/zookeeper.service ]; then
  echo "Creating a systemd service for Zookeeper..."
  sudo cp zookeeper.service /etc/systemd/system/zookeeper.service
  sudo systemctl daemon-reload
  echo "Zookeeper service has been created."
else
  echo "Zookeeper service already exists."
fi

# Start Zookeeper and Kafka services
sudo systemctl start zookeeper
sudo systemctl start kafka

# Enable auto-start on system boot
sudo systemctl enable zookeeper
sudo systemctl enable kafka

# Wait for Kafka to fully start
sleep 5

# Create topics and set up producers and consumers
echo "Setting up Kafka topics and producers/consumers..."

# Update this variable with the path to the Kafka binaries
KAFKA_BIN_PATH="/usr/local/kafka/bin"

# Create the 'logs' topic
"${KAFKA_BIN_PATH}/kafka-topics.sh" --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic logs

# Create the 'appmetrics' topic
"${KAFKA_BIN_PATH}/kafka-topics.sh" --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic appmetrics

# Create the 'dbmetrics' topic
"${KAFKA_BIN_PATH}/kafka-topics.sh" --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic dbmetrics


# Start a producer for the 'logs' topic
"${KAFKA_BIN_PATH}/kafka-console-producer.sh" --broker-list localhost:9092 --topic logs &

# Start a consumer for the 'logs' topic
"${KAFKA_BIN_PATH}/kafka-console-consumer.sh" --bootstrap-server localhost:9092 --topic logs --from-beginning &

# Start a producer for the 'appmetrics' topic
"${KAFKA_BIN_PATH}/kafka-console-producer.sh" --broker-list localhost:9092 --topic appmetrics &

# Start a consumer for the 'appmetrics' topic
"${KAFKA_BIN_PATH}/kafka-console-consumer.sh" --bootstrap-server localhost:9092 --topic appmetrics --from-beginning &

# Start a producer for the 'dbmetrics' topic
"${KAFKA_BIN_PATH}/kafka-console-producer.sh" --broker-list localhost:9092 --topic dbmetrics &

# Start a consumer for the 'dbmetrics' topic
"${KAFKA_BIN_PATH}/kafka-console-consumer.sh" --bootstrap-server localhost:9092 --topic dbmetrics --from-beginning &

echo "Kafka topics and producers/consumers have been set up."

