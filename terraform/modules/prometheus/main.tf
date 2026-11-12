terraform {
  required_providers {
    docker = { source = "kreuzwerker/docker" }
  }
}

resource "docker_image" "prometheus" {
  name = "prom/prometheus:latest"
}

resource "docker_container" "prometheus" {
  name  = "prometheus"
  image = docker_image.prometheus.image_id
  
  networks_advanced { name = var.network_id }

    ports {
    internal = 9090
    external = 9091  # <-- CHANGE THIS FROM 9090 TO 9091
  }

  # Mount the config file we created in the terraform root
  volumes {
    host_path      = abspath("${path.module}/../../prometheus.yml")
    container_path = "/etc/prometheus/prometheus.yml"
  }
}