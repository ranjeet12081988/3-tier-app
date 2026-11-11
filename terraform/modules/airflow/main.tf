terraform {
  required_providers {
    docker = { source = "kreuzwerker/docker" }
    null   = { source = "hashicorp/null" }
  }
}

resource "docker_network" "airflow_net" {
  name = "airflow-net"
}

resource "null_resource" "airflow_compose" {
  # This triggers a rebuild if you change the docker-compose.yaml file
  triggers = {
    compose_file = filemd5("${path.module}/files/docker-compose.yaml")
  }

  provisioner "local-exec" {
    command = "docker compose -f ${path.module}/files/docker-compose.yaml up -d"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "docker compose -f ${path.module}/files/docker-compose.yaml down"
  }
}