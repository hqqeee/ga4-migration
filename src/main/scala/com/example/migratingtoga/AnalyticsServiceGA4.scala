package com.example.migratingtoga

import com.example.migratingtoga.model.{AnalyticsAccount, AnalyticsGoal, AnalyticsMetadata, AnalyticsProfile, AnalyticsReport, AnalyticsRequest, MetadataType}
import com.google.analytics.admin.v1beta.{AnalyticsAdminServiceClient, AnalyticsAdminServiceSettings, ListAccountSummariesRequest}
import com.google.analytics.data.v1beta.{BetaAnalyticsDataClient, BetaAnalyticsDataSettings}
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.json.gson.GsonFactory
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.UserCredentials

import java.io.{FileInputStream, InputStreamReader}
import scala.jdk.CollectionConverters.IterableHasAsScala
import scala.jdk.CollectionConverters.ListHasAsScala
import scala.util.Using

class AnalyticsServiceGA4 extends AnalyticsService {
  // Replace "<client_secret_file_location>" with the actual path to your client secret JSON file
  private val keyFileLocation = "conf/client_secrets.json"
  private val jsonFactory = GsonFactory.getDefaultInstance
  private val clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(new FileInputStream(keyFileLocation)))

  override def getAccounts(token: String): List[AnalyticsAccount] = {
    val listAccountSummariesRequest = ListAccountSummariesRequest
      .newBuilder
      .build()
    Using(getAdminClient(token))(adminClient =>
      adminClient
        .listAccountSummaries(listAccountSummariesRequest).iterateAll.asScala.toList
        .map(AnalyticsAccount.fromGA4AccountSummary)
        .map(accountSummery =>
          accountSummery.copy(properties = accountSummery.properties.map(property =>
            property.copy(profiles = adminClient.listDataStreams(property.id).iterateAll.asScala.toList
              .map(AnalyticsProfile.fromDataStream)))))).get
  }

  override def getMetadata(token: String, propertyId: String): List[AnalyticsMetadata] =
    Using(getDataClient(token))(dataClient => {
      val metadata = dataClient.getMetadata(s"$propertyId/metadata")
      metadata.getMetricsList.asScala
        .map(metrics => AnalyticsMetadata(metrics.getApiName, MetadataType.Metric, metrics.getCategory, metrics.getUiName)).toList ++
        metadata.getDimensionsList.asScala
          .map(dimension => AnalyticsMetadata(dimension.getApiName, MetadataType.Dimension, dimension.getCategory, dimension.getUiName)).toList
    }).get

  override def getGoals(token: String, accountId: String, propertyId: String, viewId: String): List[AnalyticsGoal] =
    Using(getAdminClient(token))(adminClient =>
      adminClient.listConversionEvents(propertyId).iterateAll.asScala.toList
      .map(conversion => AnalyticsGoal(conversion.getEventName, conversion.getEventName))).get

  override def getReports(request: AnalyticsRequest): List[AnalyticsReport] =
    AnalyticsReport.fromGA4BatchRunReport(Using(getDataClient(request.token)) { dataClient =>
      dataClient.batchRunReports(request.toGA4BatchRunReportsRequest)
    }.get)


  private def getCredentials(token: String) =
    UserCredentials.newBuilder()
      .setClientId(clientSecrets.getDetails.getClientId)
      .setClientSecret(clientSecrets.getDetails.getClientSecret)
      .setRefreshToken(token)
      .build


  private def getAdminClient(token: String) = {
    val analyticsAdminServiceSettings =
      AnalyticsAdminServiceSettings
        .newBuilder
        .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials(token)))
        .build
    AnalyticsAdminServiceClient.create(analyticsAdminServiceSettings)
  }

  private def getDataClient(token: String) = {
    val betaAnalyticsDataClient = BetaAnalyticsDataSettings
      .newBuilder
      .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials(token)))
      .build()
    BetaAnalyticsDataClient.create(betaAnalyticsDataClient)
  }
}
