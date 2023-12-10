package com.w2c.kural.database

import android.text.TextUtils
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity
data class Kural(
    @PrimaryKey
    @SerializedName("Number") val number: Int,
    @SerializedName("Line1") val line1: String,
    @SerializedName("Line2") val line2: String,
    val mv: String,
    val sp: String,
    val mk: String,
    val explanation: String,
    val couplet: String,
    val transliteration1: String,
    val transliteration2: String,
    var favourite: Int = 0
) : Serializable {
    val translation: String
        get() = transliteration1 + transliteration2
    val tamilTranslation: String
        get() = if (TextUtils.isEmpty(line1) && TextUtils.isEmpty(line2)) {
            ""
        } else if (TextUtils.isEmpty(line1)) {
            line2
        } else if (TextUtils.isEmpty(line2)) {
            line1
        } else {
            "$line1\n$line2"
        }
}