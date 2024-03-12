package com.example.migratingtoga.model

import com.google.analytics.data.v1beta.Filter.StringFilter
import com.google.analytics.data.v1beta.{Filter, FilterExpression, RunReportRequest}
import com.google.api.services.analyticsreporting.v4.model.ReportRequest

import scala.jdk.CollectionConverters.SeqHasAsJava

case class AnalyticsReportRequest(dateRanges: List[AnalyticsDateRange], metrics: List[String],
                                  dimensions: List[String], goalId: Option[String], pageNumber: Int, pageSize: Int) {
  def toGoogleRequest(viewId: String): ReportRequest = {
    val metricsWithGoals = goalId.fold {
      metrics.map(metric => if (metric.contains("XX")) {
        metric.replaceAll("XX", "") + "All"
      } else metric)
    } {
      goalId =>
        metrics.map(metric =>
          if (metric.startsWith("ga:goal"))
            metric.replace("XX", goalId)
          else metric)
    }
    new ReportRequest()
      .setViewId(viewId)
      .setDateRanges(dateRanges.map(_.uaDateRange).asJava)
      .setMetrics(metricsWithGoals.map(new com.google.api.services.analyticsreporting.v4.model.Metric().setExpression(_)).asJava)
      .setDimensions(dimensions.map(new com.google.api.services.analyticsreporting.v4.model.Dimension().setName(_)).asJava)
      .setPageSize(pageSize)
      .setPageToken((pageSize * pageNumber).toString)
  }

  def toGA4Requests(propertyId: String, viewId: String): RunReportRequest = {
    val metricsWithConversionEvents = goalId.fold(metrics) { goalId =>
      metrics.map(metric =>
        if (List("sessionConversionRate", "userConversionRate").contains(metric)) s"$metric:$goalId"
        else metric)
    }
    RunReportRequest.newBuilder
      .setProperty(propertyId)
      .addAllDimensions(dimensions.map(com.google.analytics.data.v1beta.Dimension.newBuilder.setName(_).build).asJava)
      .addAllMetrics(metricsWithConversionEvents.map(com.google.analytics.data.v1beta.Metric.newBuilder.setName(_).build).asJava)
      .addAllDateRanges(dateRanges.map(_.ga4DateRange).asJava)
      .setLimit(pageSize)
      .setOffset(pageNumber * pageSize)
      .setDimensionFilter(FilterExpression.newBuilder
        .setFilter(Filter.newBuilder.setFieldName("streamId").setStringFilter(
          StringFilter.newBuilder.setMatchType(StringFilter.MatchType.EXACT).setValue(viewId))))
      .build
  }
}
