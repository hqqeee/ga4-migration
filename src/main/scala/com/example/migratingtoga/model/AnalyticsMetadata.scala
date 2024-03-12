package com.example.migratingtoga.model

import com.example.migratingtoga.model.MetadataType.Value
import com.google.analytics.data.v1beta.Metadata
import com.google.api.services.analytics.model.Column

case class AnalyticsMetadata(id: String, metadataType: MetadataType.Value, group: String, name: String)

object AnalyticsMetadata {
  def fromUAColumn(metadata: Column): AnalyticsMetadata = {
    val attributes = metadata.getAttributes

    AnalyticsMetadata(
      metadata.getId,
      if (attributes.get("type") == "DIMENSION") MetadataType.Dimension else MetadataType.Metric,
      attributes.get("group"),
      attributes.get("uiName")
    )
  }
}

object MetadataType extends Enumeration {
  type MetadataType = Value
  val Dimension, Metric = Value
}

