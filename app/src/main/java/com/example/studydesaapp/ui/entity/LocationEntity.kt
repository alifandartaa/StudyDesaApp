package com.example.studydesaapp.ui.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationEntity(
    val name: String,
    val faculty: String,
    val descrole: String,
    val problem: String,
    val sector: String,
    val work_activities: String,
    val tourist_site: String,
    val latitude: String,
    val longitude: String,
    val currentdate: String,
    val phone: String,
    val photo: String,
) : Parcelable