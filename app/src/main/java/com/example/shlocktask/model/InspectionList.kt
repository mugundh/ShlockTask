package com.example.shlocktask.model

import com.google.gson.annotations.SerializedName

import kotlin.collections.ArrayList

class InspectionList {


    var data: List<Employee> =
        ArrayList<Employee>()


    @SerializedName("GetInspectionsResult")
    var getInspectionsResult: String? = null

    @SerializedName("recordCount")
    var recordCount: Int? = null


    class Employee {
        var id: String? = null
        var employee_name: String? = null
        var employee_salary: String? = null
        var employee_age: String? = null
        var profile_image: String? = null

    }
    class InspectionItem
    {
        var hdn_ScheduleID: String? = null
        var hdn_ScheduleCode: String? = null
        var LTE_INSP_Label1: String? = null
        var LTE_INSP_Label2: String? = null
        var LTE_INSP_Label3: String? = null
        var LTE_INSP_Label5: String? = null
    }

}