package com.example.migratingtoga.model

import com.google.analytics.admin.v1beta.DataStream
import com.google.api.services.analytics.model.ProfileSummary

case class AnalyticsProfile(id: String, name: String)

object AnalyticsProfile {
  def fromProfileSummary(profile: ProfileSummary): AnalyticsProfile =
    AnalyticsProfile(
      profile.getId,
      profile.getName
    )

  def fromDataStream(dataStream: DataStream): AnalyticsProfile =
    AnalyticsProfile(
      """\d+$""".r.findFirstIn(dataStream.getName).get, // It return name in format: properties/{property_id}/dataStreams/{stream_id} and can serv as unique identifier
      dataStream.getDisplayName
    )
}
