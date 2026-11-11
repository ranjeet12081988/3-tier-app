output "network_id" { value = docker_network.app_net.id }
output "volume_name" { value = docker_volume.pg_data.name }