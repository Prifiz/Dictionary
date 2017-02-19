package controller

import gui.swingui.ViewCustomizationRecord

interface CustomizationContentHandler {
    List<ViewCustomizationRecord> getCustomization(String content)
    String buildFormattedString(List<ViewCustomizationRecord> customizationData)
}