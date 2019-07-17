package us.gonet.appmoviesgithub.model

import com.google.gson.annotations.SerializedName

class Credits {
    @SerializedName("cast")
    var cast: List<Cast>? = null
}