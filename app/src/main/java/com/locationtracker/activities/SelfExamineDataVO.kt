package com.locationtracker.activities

class SelfExamineDataVO {
    var ageRangeSelection: Int = -1
    var havingTempAbove38CSelection: Int = -1
    var havingDryCoughSelection: Int = -1
    var havingTroubleInBreathing: Int = -1
    var symptomSelection: ArrayList<Int> = ArrayList()
    var medicalBackgroundSelection: ArrayList<Int> = ArrayList()
    var wentToCovidOutBreakPlacesSelection: Int = -1
    var havingAnyCOVIDPatientInVincinitySelection: Int = -1
    var livingInTheHighlyInfectedArea: Int = -1
    var workTypeSelection: Int = -1


    private fun getAgeRange(): String {
        return when (ageRangeSelection) {
            1 -> "under 18"
            2 -> "between 18 and 60"
            3 -> "60 and older"
            else -> "not available"
        }
    }

    private fun havingFever(): Boolean {
        return havingTempAbove38CSelection == 1
    }

    private fun getDryCoughType(): String {
        return when (havingDryCoughSelection) {
            1 -> "not at all"
            2 -> "a little bit"
            3 -> "severely"
            else -> "not available"
        }
    }

    private fun hasDryCough(): Boolean {
        return when (havingDryCoughSelection) {
            1 -> false
            2 -> true
            3 -> true
            else -> false
        }
    }

    private fun hasTroubleInBreathing(): Boolean {
        return when (havingTroubleInBreathing) {
            1 -> false
            2 -> true
            3 -> true
            else -> false
        }
    }

    private fun hasTroubleInBreathingHeavily(): Boolean {
        return when (havingTroubleInBreathing) {
            1 -> false
            2 -> false
            3 -> true
            else -> false
        }
    }


    private fun getMedicalBackground(index: Int): String {
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

    private fun getSymptom(index: Int): String {
        return when (index) {
            1 -> "lethargy"
            2 -> "sneezing"
            3 -> "headache"
            4 -> "sore throat"
            5 -> "diarrhea"
            6 -> "vomiting"
            7 -> "anosmia" // lack of sense of smell
            8 -> "ageusia" // lack of sense of taste
            9 -> "myalgia" // muscle pain
            10 -> "rhinorrhea" // runny nose
            else -> "none"
        }
    }

    private fun hasCommonFluSymptoms() =
        symptomSelection.contains(2) || symptomSelection.contains(4) || symptomSelection.contains(10)

    private fun hasAdvancedCommonFluSymptoms() =
        symptomSelection.contains(1) || symptomSelection.contains(2) || symptomSelection.contains(3) || symptomSelection.contains(
            4
        ) || symptomSelection.contains(5) || symptomSelection.contains(6) || symptomSelection.contains(
            9
        ) || symptomSelection.contains(10)

    private fun hadGoneToCOVIDOutbreakPlaces(): Boolean {
        return wentToCovidOutBreakPlacesSelection == 1
    }

    private fun hasBeenLivingInHighlyInfectedAreas(): Boolean {
        return livingInTheHighlyInfectedArea == 1
    }

    private fun hasCovidPatientAround(): Boolean {
        return havingAnyCOVIDPatientInVincinitySelection == 1
    }

    private fun hasLostOfSmell(): Boolean = symptomSelection.contains(7)

    private fun hasLostOfTaste(): Boolean = symptomSelection.contains(8)

    private fun getWorkType(): String {
        return if (workTypeSelection == 1)
            "still going to work"
        else
            "work from home"
    }


    fun getFinalizedResult(): FinalResult {
        /*  val covidSymptomMatchArray: ArrayList<Boolean> = arrayListOf(
              havingFever() ||
                      hasDryCough() ||
                      hasTroubleInBreathing() ||
                      hasLostOfSmell() ||
                      hasLostOfTaste(),
              hasCovidPatientAround() ||
                      hadGoneToCOVIDOutbreakPlaces() ||
                      hasBeenLivingInHighlyInfectedAreas()
          )*/


        /* val fluSymptomMatchArray: ArrayList<Boolean> = arrayListOf(
             !havingFever(),
             !hasDryCough(),
             !hasTroubleInBreathing(),
             hasCommonFluSymptoms(),
             !hasCovidPatientAround(),
             !hadGoneToCOVIDOutbreakPlaces(),
             !hasBeenLivingInHighlyInfectedAreas()
         )*/


        /*val totallyFineMatchArray: ArrayList<Boolean> = arrayListOf(
            !havingFever(),
            !hasDryCough(),
            !hasTroubleInBreathing(),
            !hasCommonFluSymptoms(),
            !hasCovidPatientAround(),
            !hadGoneToCOVIDOutbreakPlaces(),
            !hasBeenLivingInHighlyInfectedAreas()
        )*/

        val isCovidSuspected = ((havingFever() ||
                hasDryCough() ||
                hasTroubleInBreathing() ||
                hasLostOfSmell() ||
                hasLostOfTaste()) && (
                hasCovidPatientAround() ||
                        hadGoneToCOVIDOutbreakPlaces() ||
                        hasBeenLivingInHighlyInfectedAreas())) ||
                ((hasTroubleInBreathingHeavily() ||
                        hasLostOfSmell() ||
                        hasLostOfTaste()) && !hasCovidPatientAround() &&
                        !hadGoneToCOVIDOutbreakPlaces() &&
                        !hasBeenLivingInHighlyInfectedAreas())

        val isFluSuspected = !havingFever() &&
                !hasDryCough() &&
                !hasTroubleInBreathing() &&
                hasCommonFluSymptoms() &&
                !hasCovidPatientAround() &&
                !hadGoneToCOVIDOutbreakPlaces() &&
                !hasBeenLivingInHighlyInfectedAreas()

        val isHomeIsolationNeeded =
            ((((havingFever()) || hasDryCough() || hasAdvancedCommonFluSymptoms() || hasTroubleInBreathing()) && (!hasCovidPatientAround() &&
                    !hadGoneToCOVIDOutbreakPlaces() &&
                    !hasCovidPatientAround() &&
                    !hasBeenLivingInHighlyInfectedAreas())) ||
                    (!havingFever() && !hasDryCough() && !hasTroubleInBreathing() && (hasAdvancedCommonFluSymptoms() || !hasAdvancedCommonFluSymptoms()) && (hasCovidPatientAround() ||
                            hadGoneToCOVIDOutbreakPlaces() ||
                            hasBeenLivingInHighlyInfectedAreas())))

        return when {
            isCovidSuspected -> FinalResult.COVID
            isFluSuspected -> FinalResult.SeasonalFlu
            isHomeIsolationNeeded -> FinalResult.HomeStay
            else -> FinalResult.TotallyFine
        }
    }


    fun generateReport(): String {
        return " ------------Report ------------\n" +
                " age range : ${getAgeRange()} \n" +
                " hasFever : ${havingFever()} \n " +
                " hasDryCough : ${hasDryCough()} \n " +
                " hasTroubleInBreathing : ${hasTroubleInBreathing()} \n" +
                " symptoms : \n" +
                " ${getSymptomsList()} \n" +
                " medical background : \n " +
                " ${getMedicalBackgroundList()} \n" +
                " wentToCOVIDOutBreakPlaces : ${hadGoneToCOVIDOutbreakPlaces()} \n" +
                " haveAnyCovidPatientsAround : ${hasCovidPatientAround()} \n" +
                " workType : ${getWorkType()} \n" +
                " Lost of Smell and Taste : ${hasLostOfSmell()} && ${hasLostOfTaste()} " +
                " hasCommonFluSymptoms : ${hasCommonFluSymptoms()}" +
                " hasBeenLivingInHighlyInflectedPlaces : ${hasBeenLivingInHighlyInfectedAreas()}" +
                "-------------------------------------------"
    }

    private fun getSymptomsList(): List<String> {
        val convertedList = ArrayList<String>()
        symptomSelection.forEach {
            convertedList.add(getSymptom(it) + "\n")
        }
        return convertedList
    }

    private fun getMedicalBackgroundList(): List<String> {
        val convertedList = ArrayList<String>()
        medicalBackgroundSelection.forEach {
            convertedList.add(getMedicalBackground(it) + "\n")
        }
        return convertedList
    }

    sealed class FinalResult(val data: String) {
        object COVID : FinalResult("COVID_SUSPECTED_PATIENT")
        object SeasonalFlu : FinalResult("PATIEN_WITH_FLU_SYMPTOMS")
        object HomeStay : FinalResult("MAN_NEEDS_TO_STAY_HOME")
        object TotallyFine : FinalResult("A GOOD MAN")
    }
}