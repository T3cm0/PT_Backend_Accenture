output "instance_name" {
  value = var.instance_name
}

output "databases" {
  value = var.db_names
}

output "app_user" {
  value = var.app_user
}

output "instance_connection_name" {
  value = try(google_sql_database_instance.main[0].connection_name, null)
}
