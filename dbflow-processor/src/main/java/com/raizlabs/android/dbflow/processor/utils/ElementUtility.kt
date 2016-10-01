package com.raizlabs.android.dbflow.processor.utils

import com.raizlabs.android.dbflow.annotation.ColumnIgnore
import com.raizlabs.android.dbflow.processor.ClassNames
import com.raizlabs.android.dbflow.processor.model.ProcessorManager
import java.util.*
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

/**
 * Description:
 */
object ElementUtility {

    /**
     * @param element
     * *
     * @param manager
     * *
     * @return real full-set of elements, including ones from super-class.
     */
    fun getAllElements(element: TypeElement, manager: ProcessorManager): List<Element> {
        val elements = ArrayList(manager.elements.getAllMembers(element))
        var superMirror: TypeMirror? = null
        var typeElement: TypeElement? = element
        while (typeElement?.superclass.let { superMirror = it; it != null }) {
            typeElement = manager.typeUtils.asElement(superMirror) as TypeElement?
            typeElement?.let {
                val superElements = manager.elements.getAllMembers(typeElement)
                superElements.forEach { if (!elements.contains(it)) elements += it }
            }
        }
        return elements
    }

    fun isInSamePackage(manager: ProcessorManager, elementToCheck: Element, original: Element): Boolean {
        return manager.elements.getPackageOf(elementToCheck).toString() == manager.elements.getPackageOf(original).toString()
    }

    fun isPackagePrivate(element: Element): Boolean {
        return !element.modifiers.contains(Modifier.PUBLIC) && !element.modifiers.contains(Modifier.PRIVATE)
                && !element.modifiers.contains(Modifier.STATIC)
    }

    fun isValidAllFields(allFields: Boolean, element: Element): Boolean {
        return allFields && element.kind.isField &&
                !element.modifiers.contains(Modifier.STATIC) &&
                !element.modifiers.contains(Modifier.FINAL) &&
                element.getAnnotation(ColumnIgnore::class.java) == null &&
                element.asType().toString() != ClassNames.MODEL_ADAPTER.toString()
    }
}
