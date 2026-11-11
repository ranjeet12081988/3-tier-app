terraform {
  required_providers {
    docker = { source = "kreuzwerker/docker" }
  }
}

# 1. MinIO Server Image
resource "docker_image" "minio_img" {
name = "cgr.dev/chainguard/minio:latest" 
}

# 2. MinIO Client Image (used to create buckets)
resource "docker_image" "mc_img" {
 name = "cgr.dev/chainguard/minio-client:latest"
}

# 3. MinIO Data Volume
resource "docker_volume" "minio_data" {
  name = "minio-data"
}

# 4. MinIO Server Container
resource "docker_container" "minio" {
  name  = "minio"
  image = docker_image.minio_img.image_id
  command = ["server", "/data", "--console-address", ":9001"]

  networks_advanced { name = var.network_id }

  env = [
    "MINIO_ROOT_USER=minioadmin",
    "MINIO_ROOT_PASSWORD=minioadmin",
    # Enable Webhook targeting the Relay Service (container name)
    "MINIO_NOTIFY_WEBHOOK_ENABLE_PRIMARY=on",
    "MINIO_NOTIFY_WEBHOOK_ENDPOINT_PRIMARY=http://relay-service:5000/webhook",
  ]

  ports {
    internal = 9000
    external = var.minio_port
  }
  ports {
    internal = 9001
    external = var.console_port
  }

  volumes {
    volume_name    = docker_volume.minio_data.name
    container_path = "/data"
  }
}

# 5. Bucket Initialization (Runs once to create a bucket)
resource "docker_container" "minio_init" {
  name    = "minio-init"
  image   = docker_image.mc_img.image_id
  command = [
    "/bin/sh", "-c",
    "until mc alias set local http://minio:9000 minioadmin minioadmin; do sleep 2; done; mc mb local/my-bucket --ignore-existing; echo 'Bucket ready'; exit 0"
  ]
  
  networks_advanced { name = var.network_id }
  
  must_run = false   # Key fix: Allow container to exit after one-time task completes
  attach   = false   # Ensure logs aren't captured, avoiding blocking due to log output
  
  depends_on = [docker_container.minio]
}