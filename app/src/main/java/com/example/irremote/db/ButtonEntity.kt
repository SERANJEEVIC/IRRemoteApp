@Entity(tableName = "buttons",
        foreignKeys = [ForeignKey(entity = DeviceEntity::class,
                                  parentColumns = ["id"],
                                  childColumns = ["deviceId"],
                                  onDelete = ForeignKey.CASCADE)])
data class ButtonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deviceId: Long,
    val label: String,
    val freq: Int,
    val patternJson: String, // store pattern as JSON array string
    val createdAt: Long = System.currentTimeMillis()
)
