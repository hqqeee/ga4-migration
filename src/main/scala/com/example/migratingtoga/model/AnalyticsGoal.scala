package com.example.migratingtoga.model

import com.google.analytics.admin.v1beta.ConversionEvent
import com.google.api.services.analytics.model.Goal

case class AnalyticsGoal(id: String, name: String)

object AnalyticsGoal {
  def fromGoal(goal: Goal): AnalyticsGoal =
    AnalyticsGoal(goal.getId, goal.getName)

  def fromGA4ConversionEvent(conversion: ConversionEvent): AnalyticsGoal =
    AnalyticsGoal(conversion.getName, conversion.getEventName)
}