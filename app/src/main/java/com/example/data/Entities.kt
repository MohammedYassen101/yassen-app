package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameAr: String,
    val addressEn: String,
    val addressAr: String,
    val totalUnits: Int,
    val type: String, // Residential, Commercial, Mixed
    val imageUrl: String
)

@Entity(tableName = "tenants")
data class TenantEntity(
    @PrimaryKey val id: String,
    val nameEn: String,
    val nameAr: String,
    val email: String,
    val phone: String,
    val nationalId: String,
    val status: String // Active, Pending, Terminated
)

@Entity(tableName = "contracts")
data class ContractEntity(
    @PrimaryKey val id: String,
    val contractNumber: String,
    val tenantId: String,
    val propertyId: String,
    val unitNumber: String,
    val startDate: String,
    val endDate: String,
    val annualRent: Double,
    val numberOfCheques: Int,
    val securityDeposit: Double,
    val status: String // Active, Expired, Pending
)

@Entity(tableName = "returned_cheques")
data class ReturnedChequeEntity(
    @PrimaryKey val id: String,
    val contractId: String,
    val tenantName: String,
    val chequeNumber: String,
    val bankName: String,
    val amount: Double,
    val bounceDate: String,
    val status: String, // Pending Follow-up, Settle, Legal
    val imageUrl: String
)

@Entity(tableName = "maintenance_requests")
data class MaintenanceRequestEntity(
    @PrimaryKey val id: String,
    val propertyName: String,
    val unitNumber: String,
    val titleEn: String,
    val titleAr: String,
    val priority: String, // High, Medium, Low
    val status: String, // New, In Progress, Completed
    val details: String,
    val assignee: String
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val user: String,
    val details: String,
    val timestamp: String
)
