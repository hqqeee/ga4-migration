package com.example.migratingtoga.model


import scala.jdk.CollectionConverters.IterableHasAsScala

case class AnalyticsAccount(id: String, name: String, properties: List[AnalyticsProperty])

object AnalyticsAccount {
  def fromUAAccountSummary(account: com.google.api.services.analytics.model.AccountSummary): AnalyticsAccount =
    AnalyticsAccount(
      account.getId,
      account.getName,
      account.getWebProperties.asScala.toList.map(AnalyticsProperty.fromUAWebPropertySummery)
    )

  def fromGA4AccountSummary(account: com.google.analytics.admin.v1beta.AccountSummary): AnalyticsAccount = {
    AnalyticsAccount(
      account.getAccount,
      account.getDisplayName,
      account.getPropertySummariesList.asScala.toList.map(AnalyticsProperty.fromGA4PropertySummery)
    )
  }
}