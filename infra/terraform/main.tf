provider "google" {
  project = var.project_id
  region  = var.region
}

resource "google_sql_database_instance" "main" {
  count            = var.create_instance ? 1 : 0
  name             = var.instance_name
  database_version = "MYSQL_8_0"
  region           = var.region
  deletion_protection = var.deletion_protection

  settings {
    tier            = var.db_tier
    disk_size       = var.disk_size_gb
    disk_autoresize = var.disk_autoresize

    ip_configuration {
      ipv4_enabled = true

      dynamic "authorized_networks" {
        for_each = var.authorized_networks
        content {
          name  = authorized_networks.value.name
          value = authorized_networks.value.value
        }
      }
    }

    dynamic "database_flags" {
      for_each = var.database_flags
      content {
        name  = database_flags.value.name
        value = database_flags.value.value
      }
    }

    final_backup_config {
      enabled = var.final_backup_enabled
    }
  }
}

resource "google_sql_database" "databases" {
  for_each = toset(var.db_names)
  name     = each.value
  instance = var.instance_name
}

resource "google_sql_user" "app_user" {
  name     = var.app_user
  instance = var.instance_name
  host     = "%"
  password = var.app_user_password

  lifecycle {
    ignore_changes = [password]
  }
}
