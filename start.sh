#!/bin/bash
export PATH=$PATH:/usr/local/bin:/opt/homebrew/bin

# Function to run docker-compose or docker compose
run_compose() {
    if command -v docker-compose &> /dev/null; then
        docker-compose "$@"
    else
        docker compose "$@"
    fi
}

echo "🚀 Starting Infrastructure (MySQL, Kafka, MailHog)..."
run_compose up -d

echo "⏳ Waiting for MySQL to be ready..."
MAX_RETRIES=30
COUNT=0
while [ $COUNT -lt $MAX_RETRIES ]; do
    CONTAINER_ID=$(run_compose ps -q mysql-db)
    if [ -n "$CONTAINER_ID" ]; then
        if docker exec "$CONTAINER_ID" mysqladmin ping -h"localhost" -proot --silent &> /dev/null; then
            echo "✅ MySQL is ready!"
            break
        fi
    fi
    echo "Waiting for MySQL... ($COUNT/$MAX_RETRIES)"
    sleep 2
    COUNT=$((COUNT + 1))
done

echo "⏳ Waiting for Kafka to be ready..."
sleep 15

# Get Kafka Container ID
KAFKA_ID=$(run_compose ps -q kafka)
if [ -n "$KAFKA_ID" ]; then
    echo "📡 Creating Kafka Topics..."
    docker exec "$KAFKA_ID" kafka-topics --create --topic payments_retry_jobs --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 || echo "Topic already exists"
    docker exec "$KAFKA_ID" kafka-topics --create --topic order_retry_jobs --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 || echo "Topic already exists"
    docker exec "$KAFKA_ID" kafka-topics --create --topic product_retry_jobs --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 || echo "Topic already exists"
else
    echo "⚠️ Could not find Kafka container, topics might not be created."
fi

echo "🏗️ Building and Starting Microservices..."

# Function to start a service in background
start_service() {
    local dir=$1
    local name=$2
    echo "Starting $name..."
    cd "$dir" || return
    # Use ./mvnw if available, else mvn
    if [ -f "mvnw" ]; then
        ./mvnw spring-boot:run > "../$name.log" 2>&1 &
    else
        mvn spring-boot:run > "../$name.log" 2>&1 &
    fi
    cd ..
}

start_service "eureka-server" "eureka"
sleep 15
start_service "payment-service" "payment"
start_service "order-service" "order"
start_service "product-service" "product"
sleep 5
start_service "broker-message-be" "broker"

echo "✅ All services are starting!"
echo "Check logs (*.log) for status."
echo "Eureka: http://localhost:8761"
echo "MailHog: http://localhost:8025"
