import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BasketDao {
    @Query("SELECT * FROM basket_table")
    fun getAllItems(): Flow<List<ActiviteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ActiviteItem)

    @Update
    suspend fun update(item: ActiviteItem)

    @Query("DELETE FROM basket_table WHERE name = :name")
    suspend fun deleteByName(name: String)

    @Query("DELETE FROM basket_table")
    suspend fun clearBasket()
}