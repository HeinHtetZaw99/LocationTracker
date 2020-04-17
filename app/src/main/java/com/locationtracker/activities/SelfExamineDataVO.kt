package com.locationtracker.activities

import com.appbase.showLogD

class SelfExamineDataVO() {
     var ageRangeSelection: Int = -1
     var havingTempAbove38CSelection: Int = -1
     var havingDryCoughSelection: Int = -1
     var havingTroubleInBreathing: Int = -1
     var symptomSelection: ArrayList<Int> = ArrayList()
     var medicalBackgroundSelection: ArrayList<Int> = ArrayList()
     var wentToCovidOutBreakPlacesSelection: Int = -1
     var havingAnyCOVIDPatientInVincinitySelection: Int = -1
     var workTypeSelection: Int = -1


    fun getAgeRange(): String {
        return when (ageRangeSelection) {
            1 -> "under 18"
            2 -> "between 18 and 60"
            3 -> "60 and older"
            else -> "not available"
        }
    }

    fun havingFever(): Boolean {
        return havingTempAbove38CSelection == 1
    }

    fun getDryCoughType(): String {
        return when (havingDryCoughSelection) {
            1 -> "not at all"
            2 -> "a little bit"
            3 -> "severely"
            else -> "not available"
        }
    }

    fun hasDryCough(): Boolean {
        return when (havingDryCoughSelection) {
            1 -> false
            2 -> true
            3 -> true
            else -> false
        }
    }

    fun hasTroubleInBreathing(): Boolean {
        return when (havingTroubleInBreathing) {
            1 -> false
            2 -> true
            3 -> true
            else -> false
        }
    }

    fun getSymptom(index: Int): String {
        return when (index) {
            1 -> "lethargy"
            2 -> "sneezing"
            3 -> "severely"
            4 -> "headache"
            5 -> "sore throat"
            6 -> "diarrhea"
            7 -> "vomiting"
            8 -> "anosmia" // lack of sense of smell
            9 -> "ageusia" // lack of sense of taste
            10 -> "myalgia" // muscle pain
            11 -> "rhinorrhea" // runny nose
            else -> "none"
        }
    }

    fun getMedicalBackground(index: Int): String {
        return when (index) {
            1 -> "diabetes"
            2 -> "dilated cardiomyopathy" //weaken heart
            3 -> "immunodeficiency" //low immune system
            4 -> "obesity" //fatter , fattest
            5 -> "having lung related diseases"
            6 -> "hypertension"
            7 -> "going under chemotherapy"
            8 -> "going on kidney detoxification"
            9 -> "pregnancy"
            else -> "none"
        }
    }

    fun hadGoneToCOVIDOutbreakPlaces(): Boolean {
        return wentToCovidOutBreakPlacesSelection == 1
    }

    fun hasCovidPatientAround(): Boolean {
        return havingAnyCOVIDPatientInVincinitySelection == 1
    }

    fun getWorkType(): String {
        return if (workTypeSelection == 1)
            "still going to work"
        else
            "work from home"
    }


    //    private var
    //command cold
    /*(Page 1→1 or 2 or 3) & (Page 2→2) &
    (Page 3→1) &
    (Page 4→1) &
    (Page 5→2 or 4 or 10) &
    (Page 6→1 or 2 or 3 or 4 or 5 or 6 or 7 or 8 or 9 or 10) & (Page 7→2) &
    (Page 8→2) &
    (Page 9→1 or 2)*/
/*
covid
  (Page 1→1 or 2 or 3) &
  ( (Page 2→1) or (Page 3→2 or 3) or (Page 4→2 or 3) ) & (Page 5→1 or 3 or 4 or 5 or 6 or 7 or 8 or 9 or 11) &
  (Page 6→1 or 2 or 3 or 4 or 5 or 6 or 7 or 8 or 9 or 10) & (Page 7→1) &
  (Page 8→1) &
  (Page 9→1 or 2)*/

    fun getFinalizedResult(): FinalResult {
        val covidSymptomMatchArray: ArrayList<Boolean> = arrayListOf(
            havingFever(),
            hasDryCough(),
            hasTroubleInBreathing(),
            hasCovidPatientAround(),
            hadGoneToCOVIDOutbreakPlaces()
        )

        /* if (havingFever() && hasDryCough() && hasTroubleInBreathing() && hasCovidPatientAround() && hadGoneToCOVIDOutbreakPlaces())
             return FinalResult.COVID*/
        var covidPercentage = 0.0
        var fluPercentage = 0.0

        covidSymptomMatchArray.forEach {
            if (it)
                covidPercentage += 0.2
        }

        symptomSelection.forEach {
            if (it == 2 || it == 4 || it == 10){
                fluPercentage += 0.33
            }
        }
        showLogD(generateReport())
        showLogD("covidPercentage = $covidPercentage && fluPercentage = $fluPercentage")

        return when {
            covidPercentage > fluPercentage -> FinalResult.COVID
            fluPercentage > 0.6 -> FinalResult.SeasonalFlu
            else -> FinalResult.HomeStay
        }
    }


    fun generateReport() : String{
        return " ------------Report ------------\n" +
                "age range : ${getAgeRange()} \n" +
                " hasFever : ${havingFever()} \n " +
                " hasDryCough : ${hasDryCough()} \n " +
                " hasTroubleInBreathing : ${hasTroubleInBreathing()} \n" +
                " symptoms : \n" +
                " ${symptomSelection.forEach { 
                    getSymptom(it) +"\n"
                }} \n" +
                " medical background : \n " +
                " ${medicalBackgroundSelection.forEach {
                    getMedicalBackground(it) +"\n"
                }} \n" +
                " wentToCOVIDOutBreakPlaces : ${hadGoneToCOVIDOutbreakPlaces()} \n" +
                " haveAnyCovidPatientsAround : ${hasCovidPatientAround()} \n" +
                " workType : ${getWorkType()} \n" +
                "-------------------------------------------"
    }

    sealed class FinalResult {
        object COVID : FinalResult()
        object SeasonalFlu : FinalResult()
        object HomeStay : FinalResult()
    }
}