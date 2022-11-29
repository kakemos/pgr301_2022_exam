terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "4.40.0"
    }
  }
  backend "s3" {
    bucket = "analytics-1015"
    key    = "1015/1015-tf.state"
    region = "eu-west-1"
  }
}