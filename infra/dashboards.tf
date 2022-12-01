resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = var.candidate_id
## Jim; seriously! we can use any word here.. How cool is that?
  dashboard_body = <<DEATHSTAR
{
  "widgets": [
    {
      "type": "metric",
      "x": 13,
      "y": 0,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.candidate_id}",
            "cart_count.value"
          ]
        ],
        "period": 30,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Total number of active carts"
      }
    },
    {
      "type": "metric",
      "x": 0,
      "y": 7,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.candidate_id}",
            "carts_value.value"
          ]
        ],
        "period": 30,
        "stat": "Maximum",
        "region": "eu-west-1",
        "title": "Total sum for all active carts"
      }
    },
    {
      "type": "metric",
      "x": 13,
      "y": 7,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.candidate_id}",
            "checkout_count.count"
          ]
        ],
        "period": 3600,
        "stat": "Sum",
        "region": "eu-west-1",
        "title": "Number of checked out carts per hour"
      }
    },
    {
      "type": "metric",
      "x": 0,
      "y": 13,
      "width": 12,
      "height": 6,
      "properties": {
        "metrics": [
          [
            "${var.candidate_id}",
            "checkout_latency.count"
          ]
        ],
        "period": 30,
        "stat": "Average",
        "region": "eu-west-1",
        "title": "Average response time for checkout method"
      }
    }
  ]
}
DEATHSTAR
}