@Database(entities = [DeviceEntity::class, ButtonEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun buttonDao(): ButtonDao
}
