variable "project_id" {
  type        = string
  description = "GCP project id."
}

variable "region" {
  type        = string
  description = "GCP region for Cloud SQL."
  default     = "us-central1"
}

variable "instance_name" {
  type        = string
  description = "Cloud SQL instance name."
  default     = "franchise-mysql"
}

variable "db_tier" {
  type        = string
  description = "Cloud SQL machine tier (e.g., db-f1-micro, db-g1-small, db-custom-1-3840)."
  default     = "db-f1-micro"
}

variable "disk_size_gb" {
  type        = number
  description = "Disk size in GB."
  default     = 20
}

variable "disk_autoresize" {
  type        = bool
  description = "Enable disk autosize."
  default     = true
}

variable "authorized_networks" {
  type = list(object({
    name  = string
    value = string
  }))
  description = "List of authorized networks for public IP access."
  default     = []
}

variable "create_instance" {
  type        = bool
  description = "Whether to create the Cloud SQL instance."
  default     = true
}

variable "deletion_protection" {
  type        = bool
  description = "Protect the instance from being destroyed by Terraform."
  default     = true
}

variable "db_names" {
  type        = list(string)
  description = "Database names to create."
  default     = ["TestPT", "ProdPT"]
}

variable "app_user" {
  type        = string
  description = "Application database user."
  default     = "app_user"
}

variable "app_user_password" {
  type        = string
  description = "Password for the application user."
  sensitive   = true
}

variable "database_flags" {
  type = list(object({
    name  = string
    value = string
  }))
  description = "Database flags to apply."
  default     = []
}

variable "final_backup_enabled" {
  type        = bool
  description = "Whether to enable final backup on instance deletion."
  default     = false
}
