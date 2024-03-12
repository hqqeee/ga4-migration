package com.example.migratingtoga.model

import com.google.analytics.data.v1beta.Row
import com.google.api.services.analyticsreporting.v4.model.ReportRow

import scala.jdk.CollectionConverters.IterableHasAsScala

case class AnalyticsReportRow(dimensions: List[String], metrics: List[Float])

object AnalyticsReportRow {
  def fromUAReportRow(row: ReportRow): AnalyticsReportRow = AnalyticsReportRow(
    Option(row.getDimensions).map(_.asScala.toList).getOrElse(List.empty),
    row.getMetrics.asScala.flatMap(_.getValues.asScala.map(_.toFloat)).toList
  )

  def fromGA4ReportRow(row: Row): AnalyticsReportRow =
    AnalyticsReportRow(
      row.getDimensionValuesList.asScala.map(_.getValue).toList,
      row.getMetricValuesList.asScala.map(_.getValue).map(_.toFloat).toList)
}