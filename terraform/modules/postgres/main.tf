

resource "docker_image" "postgres" {
  name = "postgres:15-alpine"
}

resource "docker_container" "postgres" {
  name  = "product-catalog-db"
  image = docker_image.postgres.image_id
  
  networks_advanced { name = var.network_id }
  
  volumes {
    volume_name    = var.volume_name
    container_path = "/var/lib/postgresql/data"
  }
  
  env = [
    "POSTGRES_DB=product_catalog",
    "POSTGRES_USER=postgres",
    "POSTGRES_PASSWORD=${var.db_password}",
  ]
  
  ports {
    internal = 5432
    external = 5434
  }
}
