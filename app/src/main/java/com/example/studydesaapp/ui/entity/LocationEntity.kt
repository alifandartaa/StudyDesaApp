package com.example.studydesaapp.ui.entity

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationEntity(
    val name: String,
    val descrole: String,
    val info_location: String,
    val latitude: String,
    val longitude: String,
    val phone: String,
    val photo: String,
) : Parcelable