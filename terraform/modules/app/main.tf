resource "docker_image" "app" {
  name         = "ranjeet1988/3-tier-app:${var.app_version}"
  keep_locally = true 
}

resource "docker_container" "app" {
  name  = "product-catalog-app"
  image = docker_image.app.image_id

  networks_advanced {
    name = var.network_id
  }

  env = [
    "SPRING_DATASOURCE_URL=jdbc:postgresql://${var.db_host}:5432/product_catalog",
    "SPRING_DATASOURCE_USERNAME=postgres",
    "SPRING_DATASOURCE_PASSWORD=${var.db_password}",
    "SERVER_PORT=9090",
    "OLLAMA_BASE_URL=http://host.docker.internal:11434"
  ]

  ports {
    internal = 9090
    external = 9090
  }

  depends_on = [var.db_host] 
}