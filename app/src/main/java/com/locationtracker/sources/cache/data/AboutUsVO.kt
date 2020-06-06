package com.locationtracker.sources.cache.data

class AboutUsVO(var name : String = "",
                var title : String = "") {


    companion object{
        fun getDevTeamList(): List<AboutUsVO>{
            val devTeam = ArrayList<AboutUsVO>()
            devTeam.add(AboutUsVO("Ko Hein Htet Zaw" , "BE.CEIT"))
            devTeam.add(AboutUsVO("Ko Sai Han Ba" , "BE.CEIT"))
            devTeam.add(AboutUsVO("Mg Aung Myo Htet" , "Final Year Mechatronics"))
            devTeam.add(AboutUsVO("Ma Khin Su Mon" , "Final Year Mechatronics"))
            devTeam.add(AboutUsVO("Mg Soe Thet Paing" , "Final Year Mechatronics"))
            devTeam.add(AboutUsVO("Mg Kyaw Zayar Tun" , "Final Year Mechatronics"))
            return devTeam
        }

        fun getTechnicalAdvisorList(): List<AboutUsVO>{
            return arrayListOf(AboutUsVO("Ko Min Thway Kyaw",
                "(CEO of Virtual-Web)\nBE Mechatronics, W.Y.T.U"))
        }

        fun getSpecialThanksList(): List<AboutUsVO>{
            val devTeam = ArrayList<AboutUsVO>()
            devTeam.add(AboutUsVO("Ko Myat Min Soe" , "CEO of DevHouse Myanmar"))
            devTeam.add(AboutUsVO("Mg Phone Thiha Kyaw" , "Final Year Mechatronics"))
            devTeam.add(AboutUsVO("Mg Sai Htet Moe Swe" , "Final Year Mechatronics"))
            devTeam.add(AboutUsVO("Ma Su Nandar Linn" , "Final Year CEIT"))
            devTeam.add(AboutUsVO("Ko Aung Kyaw Sint" , "BArch YTU"))
//            devTeam.add(AboutUsVO("U Zaw Naing" , "Mandalay Technology"))
            devTeam.add(AboutUsVO("U Aung Khaing" , "Echo-Tech Construction Co., Ltd"))
            devTeam.add(AboutUsVO("Rector, HoDs, teachers and students (YTU)" , ""))
            devTeam.add(AboutUsVO("Medical Officers and Staffs of Waibargi Specialist Hospital" , ""))
            return devTeam
        }
    }
}