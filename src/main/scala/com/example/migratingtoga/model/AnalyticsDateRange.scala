package com.example.migratingtoga.model


case class AnalyticsDateRange(startDate: String, endDate: String) {
  val uaDateRange: com.google.api.services.analyticsreporting.v4.model.DateRange =
    new com.google.api.services.analyticsreporting.v4.model.DateRange()
      .setStartDate(startDate).setEndDate(endDate)
  val ga4DateRange: com.google.analytics.data.v1beta.DateRange =
    com.google.analytics.data.v1beta.DateRange
      .newBuilder.setStartDate(startDate).setEndDate(endDate).build
}