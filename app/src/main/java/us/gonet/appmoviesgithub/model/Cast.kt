package us.gonet.appmoviesgithub.model

import com.google.gson.annotations.SerializedName

class Cast(
    @field:SerializedName("cast_id")
    var castId: Int, @field:SerializedName("character")
    var character: String?, @field:SerializedName("name")
    var name: String?, @field:SerializedName("profile_path")
    var profilePath: String?) {

    override fun toString(): String {
        return "Cast{" +
                "castId=" + castId +
                ", character='" + character + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", profilePath='" + profilePath + '\''.toString() +
                '}'.toString()
    }
}