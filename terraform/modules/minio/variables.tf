variable "network_id" { type = string }
variable "minio_port" {
  type    = number
  default = 9000 # S3 API Port
}
variable "console_port" {
  type    = number
  default = 9001 # Web Console Port
}