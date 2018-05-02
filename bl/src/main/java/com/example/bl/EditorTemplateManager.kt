package com.example.bl

import com.example.model.EditObjectTemplateDto
import com.example.model.ObjectType

interface EditorTemplateManager {
    fun getEditTemplate(objectType: ObjectType): EditObjectTemplateDto
}

