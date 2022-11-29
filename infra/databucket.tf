# Jim; this just fails ... commented it out ! We need to figure this out later, starting new task instead...
// test
data "aws_s3_bucket" "analyticsbucket" {
  bucket = "analytics-${var.candidate_id}"
}

output "my_bucket_name" {
  value = data.aws_s3_bucket.analyticsbucket.bucket
}