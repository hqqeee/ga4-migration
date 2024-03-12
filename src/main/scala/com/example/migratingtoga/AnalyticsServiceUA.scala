package com.example.migratingtoga

import com.example.migratingtoga.model.{AnalyticsAccount, AnalyticsGoal, AnalyticsMetadata, AnalyticsReport, AnalyticsRequest}
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.auth.oauth2.UserCredentials
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.analytics.Analytics
import com.google.api.services.analytics.model.AccountSummaries
import com.google.api.services.analyticsreporting.v4.AnalyticsReporting
import com.google.api.services.analyticsreporting.v4.model.GetReportsResponse
import com.google.auth.http.HttpCredentialsAdapter

import java.io.{FileInputStream, InputStreamReader}
import scala.jdk.CollectionConverters.ListHasAsScala

class AnalyticsServiceUA extends AnalyticsService {

  // Replace "<application_name>" with the name of your application
  private val applicationName = "<application_name>"
  private val jsonFactory = GsonFactory.getDefaultInstance
  // Replace "<client_secret_file_location>" with the actual path to your client secret JSON file
  private val keyFileLocation = "conf/client_secrets.json"
  private val httpTransport = GoogleNetHttpTransport.newTrustedTransport
  private val clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(new FileInputStream(keyFileLocation)))


  def getAccounts(token: String): List[AnalyticsAccount] = {
    val accountSummaries: AccountSummaries = getAnalytics(token)
      .management()
      .accountSummaries()
      .list()
      .execute()
    accountSummaries.getItems.asScala.toList
      .map(AnalyticsAccount.fromUAAccountSummary)
  }


  def getMetadata(token: String, propertyId: String): List[AnalyticsMetadata] = {
    val metadataList = getAnalytics(token).metadata()
      .columns()
      .list("ga")
      .execute()
      .getItems
    metadataList.asScala.toList.map(AnalyticsMetadata.fromUAColumn)
  }

  def getGoals(token: String, accountId: String, propertyId: String, viewId: String): List[AnalyticsGoal] = {
    val goals = getAnalytics(token).management()
      .goals()
      .list(accountId, propertyId, viewId)
      .execute()
      .getItems

    goals.asScala.toList.map(AnalyticsGoal.fromGoal)
  }

  def getReports(request: AnalyticsRequest): List[AnalyticsReport] = {
    getAnalyticsReporting(request.token)
      .reports
      .batchGet(request.toUAGetReportsRequest).execute
      .getReports.asScala.map(AnalyticsReport.fromUAReport).toList
  }

  private def getAnalytics(token: String) =
    new Analytics.Builder(httpTransport, jsonFactory, getGoogleCredential(token))
      .setApplicationName(applicationName)
      .build()

  private def getAnalyticsReporting(token: String) =
    new AnalyticsReporting.Builder(httpTransport, jsonFactory, getGoogleCredential(token))
      .setApplicationName(applicationName)
      .build


  private def getGoogleCredential(token: String): HttpRequestInitializer = {
    val credential = UserCredentials.newBuilder()
      .setClientId(clientSecrets.getDetails.getClientId)
      .setClientSecret(clientSecrets.getDetails.getClientSecret)
      .setRefreshToken(token)
      .build
    new HttpCredentialsAdapter(credential)
  }
}