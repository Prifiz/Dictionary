package controller

import gui.swingui.ViewCustomizationRecord

class CustomizationXmlContentHandler implements CustomizationContentHandler {
    @Override
    List<ViewCustomizationRecord> getCustomization(String content) {
        def xml = new XmlParser().parseText(content)
        List<ViewCustomizationRecord> result = new ArrayList<>()
        xml.record.each { record ->
            String columnName = record.columnName.text()
            Boolean visible = Boolean.valueOf(record.visible.text())
            result.add(new ViewCustomizationRecord(visible, columnName, ""))
        }
        return result
    }

    @Override
    String buildFormattedString(List<ViewCustomizationRecord> customizationData) {
        def sw = new StringWriter()
        def xml = new groovy.xml.MarkupBuilder(sw)
        xml.setDoubleQuotes(true)
        xml.customizationRecords() {
            customizationData.each { customizationRecord ->
                record {
                    columnName(customizationRecord.getColumnName())
                    visible(customizationRecord.getVisible())
                }
            }
        }
        return sw.toString()
    }
}
