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
      property.getProperty, // todo test, get format,
      // here is the same as for accountSummery, there is no method to get Id like in the UA, so we use getProperty method that will return id in the format properties/{property_id}
      property.getDisplayName,
      profiles = List.empty // Here is the first important change, in the Universal Analytics we have hierarchy like Account -> Properties -> Views,
      // but in Google Analytics 4 we have Account -> Properties -> Data Streams, although data stream is not equivalent to views (check https://www.clickinsight.ca/blog/ga4-universal-analytics-views-not-ga4-data-streams)
      // for more info, in our use case, we can use it as views. The problem here is that we cannot get datastreams from PropertySummery directly, we need to make API call to get them,
      // so we just left empty list here and enrich it with the data in the class where we do api calls.
    )
}