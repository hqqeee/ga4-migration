package com.example.migratingtoga

import com.example.migratingtoga.model.{AnalyticsDateRange, AnalyticsReportRequest, AnalyticsRequest}

object Example {
  def main(args: Array[String]): Unit = {
    val googleGA4AnalyticsService = new AnalyticsServiceGA4
    val token = "<access_token>"

    val accounts = googleGA4AnalyticsService.getAccounts(token)
    val metadata = googleGA4AnalyticsService.getMetadata(token, accounts.head.id)
    val goals = googleGA4AnalyticsService.getGoals(token, accounts.head.id,
      accounts.head.properties.head.id,
      accounts.head.properties.head.profiles.head.id)
    val request = AnalyticsRequest(
      token,
      accounts.head.properties.head.id,
      accounts.head.properties.head.profiles.head.id,
      List(
        AnalyticsReportRequest(
          List(AnalyticsDateRange("2023-09-01", "2024-02-01")),
          List("totalRevenue", "userConversionRate", "sessionConversionRate"),
          List("campaignName"),
          Some("purchase"),
          1,
          10
        )
      )
    )
    val report = googleGA4AnalyticsService.getReports(request)
  }
}
