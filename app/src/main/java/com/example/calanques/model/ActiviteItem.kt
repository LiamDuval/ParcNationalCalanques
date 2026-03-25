import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "basket_table")
data class ActiviteItem(
    @PrimaryKey(autoGenerate = true)
    val idActivite: Int = 0,
    val id: Int,
    val name: String,
    val price: Double,
    val nbPlace: Int,
    val date: String,
    val hourly: String
)