ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "MigratingToGA4"
  )


libraryDependencies ++= Seq(
  "com.google.apis" % "google-api-services-analyticsreporting" % "v4-rev174-1.25.0", // for UA\
  "com.google.apis" % "google-api-services-analytics" % "v3-rev169-1.25.0",
  "com.google.auth" % "google-auth-library-oauth2-http" % "1.16.1", // for getCredentials
  // for GA4
  "com.google.analytics" % "google-analytics-admin" % "0.46.0",
  "com.google.analytics" % "google-analytics-data" % "0.47.0",
)