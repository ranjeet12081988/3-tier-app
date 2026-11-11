output "minio_endpoint" {
  value = "http://localhost:${var.minio_port}"
}

output "minio_console" {
  value = "http://localhost:${var.console_port}"
}