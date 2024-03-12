package com.example.migratingtoga.model

import com.google.analytics.data.v1beta.BatchRunReportsRequest
import com.google.api.services.analyticsreporting.v4.model.GetReportsRequest

import scala.jdk.CollectionConverters.SeqHasAsJava

case class AnalyticsRequest(token: String, propertyId: String, viewId: String, requests: List[AnalyticsReportRequest]) {
  def toUAGetReportsRequest: GetReportsRequest = {
    new GetReportsRequest()
      .setReportRequests(requests.map(request => request.toGoogleRequest(viewId)).asJava)
  }

  def toGA4BatchRunReportsRequest: BatchRunReportsRequest = {
    BatchRunReportsRequest
      .newBuilder
      .setProperty(propertyId)
      .addAllRequests(requests.map(requests => requests.toGA4Requests(propertyId, viewId)).asJava)
      .build
  }
}