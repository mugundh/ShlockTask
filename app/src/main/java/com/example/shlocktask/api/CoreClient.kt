


import com.example.shlocktask.model.InspectionList
import retrofit2.Call
import retrofit2.http.GET

interface CoreClient {

    @GET("bVZYphlszm?indent=2")
    fun coreDetails(): Call<InspectionList?>?
}