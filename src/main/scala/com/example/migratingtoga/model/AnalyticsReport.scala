package com.example.migratingtoga.model

import com.google.analytics.data.v1beta.BatchRunReportsResponse
import com.google.api.services.analyticsreporting.v4.model.Report

import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.util.Try

case class AnalyticsReport(dimensions: List[String], metrics: List[String], metricTypes: List[String],
                           rowCount: Int, totals: List[Float], rows: List[AnalyticsReportRow])

object AnalyticsReport {
  def fromUAReport(report: Report): AnalyticsReport = {
    val headers = report.getColumnHeader.getMetricHeader.getMetricHeaderEntries.asScala.toList
    AnalyticsReport(
      report.getColumnHeader.getDimensions.asScala.toList,
      headers.map(_.getName),
      headers.map(_.getType),
      report.getData.getRowCount,
      report.getData.getTotals.asScala.flatMap(_.getValues.asScala.map(_.toFloat)).toList,
      report.getData.getRows // may be null
        .asScala.toList.map(AnalyticsReportRow.fromUAReportRow)
    )
  }

  def fromGA4BatchRunReport(report: BatchRunReportsResponse): List[AnalyticsReport] = {
    report.getReportsList.asScala.toList.map(report =>
    AnalyticsReport(
      report.getDimensionHeadersList.asScala.map(_.getName).toList,
      report.getMetricHeadersList.asScala.map(_.getName).toList,
      report.getMetricHeadersList.asScala.map(_.getType.name).toList,
      report.getRowCount,
      report.getTotalsList.asScala.flatMap(_.getMetricValuesList.asScala.map(_.getValue)
          .map(metricValue => Try(metricValue.toFloat).getOrElse(0.0F))).toList,
      report.getRowsList.asScala.map(AnalyticsReportRow.fromGA4ReportRow).toList
    ))
  }
}