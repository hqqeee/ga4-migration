package com.example.migratingtoga

import com.example.migratingtoga.model.{AnalyticsAccount, AnalyticsGoal, AnalyticsMetadata, AnalyticsReport, AnalyticsRequest}

trait AnalyticsService {
  def getAccounts(token: String): List[AnalyticsAccount]
  def getMetadata(token: String, propertyId: String): List[AnalyticsMetadata]
  def getGoals(token: String, accountId: String, propertyId: String, viewId: String): List[AnalyticsGoal]
  def getReports(request: AnalyticsRequest): List[AnalyticsReport]
}
