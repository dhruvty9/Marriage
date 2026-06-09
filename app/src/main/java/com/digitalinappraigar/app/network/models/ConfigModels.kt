package com.digitalinappraigar.app.network.models

import com.google.gson.annotations.SerializedName

data class ConfigOptionsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: ConfigOptionsData?
)

data class ConfigOptionsData(
    @SerializedName("genders")          val genders: List<String>?,
    @SerializedName("maritalStatuses")  val maritalStatuses: List<String>?,
    @SerializedName("eatingHabits")     val eatingHabits: List<String>?,
    @SerializedName("motherTongues")    val motherTongues: List<String>?,
    @SerializedName("heights")          val heights: List<HeightOption>?,
    @SerializedName("castes")           val castes: List<String>?,
    @SerializedName("rashis")           val rashis: List<String>?,
    @SerializedName("manglikStatuses")  val manglikStatuses: List<String>?,
    @SerializedName("bloodGroups")      val bloodGroups: List<String>?,
    @SerializedName("familyStatuses")   val familyStatuses: List<String>?,
    @SerializedName("familyTypes")      val familyTypes: List<String>?,
    @SerializedName("childrenCounts")   val childrenCounts: List<String>?,
    @SerializedName("educations")       val educations: List<String>?,
    @SerializedName("jobTypes")         val jobTypes: List<String>?,
    @SerializedName("occupations")      val occupations: List<String>?,
    @SerializedName("annualIncomes")    val annualIncomes: List<IncomeOption>?,
    @SerializedName("states")           val states: Map<String, List<String>>?
)

data class HeightOption(
    @SerializedName("label") val label: String,
    @SerializedName("value") val value: Int
)

data class IncomeOption(
    @SerializedName("label") val label: String,
    @SerializedName("value") val value: Long
)
