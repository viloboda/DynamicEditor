package com.example.model

import com.google.gson.annotations.SerializedName

class EditObjectTemplateDto : TemplateDto {

    @SerializedName("items")
    var Items: List<ItemDto> = mutableListOf()

    constructor()

    constructor(name: String, items: List<ItemDto>) : super(name) {
        this.Items = items
    }

    override fun isDefault(): Boolean {
        return true
    }

    fun getTemplateItem(fieldCode: String): EditObjectTemplateDto.ItemDto? {
        for (item in Items) {
            if (item.FieldCode == fieldCode)
                return item
        }

        return null
    }

    open class ItemDto {
        @SerializedName("field_code")
        var FieldCode: String = ""

        @SerializedName("expandable")
        var Expandable = true

        @SerializedName("expanded")
        var Expanded = true

        @SerializedName("show_header")
        var ShowHeader = false

        @SerializedName("show_dash_line")
        var ShowDashLine = false

        @SerializedName("bold_caption")
        var BoldCaption = false

        @SerializedName("caption")
        var Caption: String? = null

        @SerializedName("group_name")
        var GroupName: String? = null

        @SerializedName("layout_orientation")
        var LayoutOrientation: String? = null

        // Тип контрола, которым будем редаетировать значение, FieldConfiguration.CONTROL_TYPE...
        @SerializedName("edit_control_type")
        var EditControlType: Int = 0

        constructor() {
            this.Expandable = true
            this.Expanded = true
        }

        constructor(fieldCode: String) {
            this.FieldCode = fieldCode
        }

        constructor(fieldCode: String, expandable: Boolean) {
            this.FieldCode = fieldCode
            this.Expandable = expandable
        }

        constructor(fieldCode: String, expandable: Boolean, expanded: Boolean) {
            this.FieldCode = fieldCode
            this.Expandable = expandable
            this.Expanded = expanded
        }

        override fun toString(): String {
            return FieldCode
        }
    }
}


