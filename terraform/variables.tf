variable "db_password" {
  description = "PostgreSQL password"
  type        = string
  sensitive   = true
}

variable "app_version" {
  description = "Tag of the Spring Boot app"
  type        = string
  default     = "1.0.2"
}