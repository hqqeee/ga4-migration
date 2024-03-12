package com.example.migratingtoga.model

import com.google.analytics.admin.v1beta.PropertySummary
import com.google.api.services.analytics.model.WebPropertySummary

import scala.jdk.CollectionConverters.IterableHasAsScala

case class AnalyticsProperty(id: String, name: String, profiles: List[AnalyticsProfile])

object AnalyticsProperty {
  def fromUAWebPropertySummery(property: WebPropertySummary): AnalyticsProperty =
    AnalyticsProperty(
      property.getId,
      property.getName,
      property.getProfiles.asScala.toList.map(AnalyticsProfile.fromProfileSummary)
    )

  def fromGA4PropertySummery(property: PropertySummary): AnalyticsProperty =
    AnalyticsProperty(
      property.getProperty,
      property.getDisplayName,
      profiles = List.empty
    )
}