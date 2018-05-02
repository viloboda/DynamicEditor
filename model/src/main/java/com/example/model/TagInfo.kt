package com.example.model

import java.util.ArrayList

class TagInfo(// Ключ тега (аля, schedule или contact)
        var Key: String) {

    // Выбран ли тег
    var IsChecked: Boolean = false

    companion object {

        fun getCheckedTags(tagInfos: List<TagInfo>): String {
            val result = ArrayList<String>()

            for (tagInfo in tagInfos) {
                if (tagInfo.IsChecked) {
                    result.add(tagInfo.Key)
                }
            }

            return result.joinToString(";")
        }
    }
}
