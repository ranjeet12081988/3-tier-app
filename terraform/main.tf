module "network" {
  source = "./modules/network"
}

module "postgres" {
  source      = "./modules/postgres"
  network_id  = module.network.network_id
  volume_name = module.network.volume_name
  db_password = var.db_password
}

module "app" {
  source       = "./modules/app"
  network_id   = module.network.network_id
  app_version  = var.app_version
  db_password  = var.db_password
  db_host      = module.postgres.db_container_name
}

module "grafana" {
  source     = "./modules/grafana"
  network_id = module.network.network_id
}

module "airflow" {
  source = "./modules/airflow"
  # If you want Airflow to connect to your existing PostgreSQL, you need to pass db_password and other variables in; the simplified version uses SQLite
}

module "minio" {
  source     = "./modules/minio"
  network_id = module.network.network_id
}