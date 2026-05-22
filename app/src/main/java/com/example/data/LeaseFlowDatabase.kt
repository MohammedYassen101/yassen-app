package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LeaseFlowDao {
    @Query("SELECT * FROM properties")
    fun getAllPropertiesFlow(): Flow<List<PropertyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProperty(property: PropertyEntity)

    @Query("SELECT * FROM tenants")
    fun getAllTenantsFlow(): Flow<List<TenantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTenant(tenant: TenantEntity)

    @Delete
    suspend fun deleteTenant(tenant: TenantEntity)

    @Query("SELECT * FROM contracts")
    fun getAllContractsFlow(): Flow<List<ContractEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContract(contract: ContractEntity)

    @Query("SELECT * FROM returned_cheques")
    fun getAllReturnedChequesFlow(): Flow<List<ReturnedChequeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReturnedCheque(cheque: ReturnedChequeEntity)

    @Query("SELECT * FROM maintenance_requests")
    fun getAllMaintenanceRequestsFlow(): Flow<List<MaintenanceRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceRequest(request: MaintenanceRequestEntity)

    @Query("SELECT * FROM activity_logs ORDER BY id DESC")
    fun getAllActivityLogsFlow(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    @Query("DELETE FROM activity_logs")
    suspend fun clearLogs()
}

@Database(
    entities = [
        PropertyEntity::class,
        TenantEntity::class,
        ContractEntity::class,
        ReturnedChequeEntity::class,
        MaintenanceRequestEntity::class,
        ActivityLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LeaseFlowDatabase : RoomDatabase() {
    abstract val dao: LeaseFlowDao

    companion object {
        @Volatile
        private var INSTANCE: LeaseFlowDatabase? = null

        fun getInstance(context: Context): LeaseFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeaseFlowDatabase::class.java,
                    "leaseflow_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
