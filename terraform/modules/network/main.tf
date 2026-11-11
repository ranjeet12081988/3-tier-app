resource "docker_network" "app_net" {
  name = "product-catalog-net"
}

resource "docker_volume" "pg_data" {
  name = "product-catalog-pg-data"
}