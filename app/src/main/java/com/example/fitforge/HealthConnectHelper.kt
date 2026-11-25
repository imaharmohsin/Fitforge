package com.example.fitforge

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

object HealthConnectHelper {

    private fun getStartOfDay(): Instant {
        return LocalDate.now()
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
    }


    fun getGrantedPermissions(client: HealthConnectClient): Set<String> {
        return runBlocking(Dispatchers.IO) {
            try {
                client.permissionController.getGrantedPermissions()
            } catch (e: Exception) {
                emptySet()
            }
        }
    }

    // ✅ Steps
    fun getSteps(client: HealthConnectClient, callback: (Long) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(
                            getStartOfDay(), Instant.now()
                        )
                    )
                )
                val steps = response[StepsRecord.COUNT_TOTAL] ?: 0L
                callback(steps)
            } catch (e: Exception) {
                callback(0L)
            }
        }
    }

    // ✅ Heart Rate
    fun getHeartRate(client: HealthConnectClient, callback: (Long) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        recordType = HeartRateRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(
                            Instant.now().minus(1, ChronoUnit.HOURS),
                            Instant.now()
                        )
                    )
                )
                val lastBpm = response.records
                    .lastOrNull()
                    ?.samples
                    ?.lastOrNull()
                    ?.beatsPerMinute ?: 0L
                callback(lastBpm)
            } catch (e: Exception) {
                callback(0L)
            }
        }
    }

    // ✅ Calories
    fun getCalories(client: HealthConnectClient, callback: (Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(TotalCaloriesBurnedRecord.ENERGY_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(
                            getStartOfDay(), Instant.now()
                        )
                    )
                )
                val calories = response[TotalCaloriesBurnedRecord.ENERGY_TOTAL]
                    ?.inKilocalories ?: 0.0
                callback(calories)
            } catch (e: Exception) {
                callback(0.0)
            }
        }
    }

    // ✅ Distance
    fun getDistance(client: HealthConnectClient, callback: (Double) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.aggregate(
                    AggregateRequest(
                        metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(
                            getStartOfDay(), Instant.now()
                        )
                    )
                )
                val distance = response[DistanceRecord.DISTANCE_TOTAL]
                    ?.inMeters ?: 0.0
                callback(distance)
            } catch (e: Exception) {
                callback(0.0)
            }
        }
    }

    // ✅ Sleep
    fun getSleep(client: HealthConnectClient, callback: (Long) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.readRecords(
                    ReadRecordsRequest(
                        recordType = SleepSessionRecord::class,
                        timeRangeFilter = TimeRangeFilter.between(
                            Instant.now().minus(12, ChronoUnit.HOURS),
                            Instant.now()
                        )
                    )
                )
                val totalMinutes = response.records.sumOf { session ->
                    val duration = session.endTime.epochSecond -
                            session.startTime.epochSecond
                    duration / 60
                }
                callback(totalMinutes)
            } catch (e: Exception) {
                callback(0L)
            }
        }
    }
}