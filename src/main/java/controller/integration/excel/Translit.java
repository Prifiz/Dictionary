package controller.integration.excel;

public class Translit {
    public static String cyr2lat(char ch){
        switch (ch){
            case 'А': return "A";
            case 'Б': return "B";
            case 'В': return "V";
            case 'Г': return "G";
            case 'Д': return "D";
            case 'Е': return "E";
            case 'Ё': return "JE";
            case 'Ж': return "ZH";
            case 'З': return "Z";
            case 'И': return "I";
            case 'Й': return "Y";
            case 'К': return "K";
            case 'Л': return "L";
            case 'М': return "M";
            case 'Н': return "N";
            case 'О': return "O";
            case 'П': return "P";
            case 'Р': return "R";
            case 'С': return "S";
            case 'Т': return "T";
            case 'У': return "U";
            case 'Ф': return "F";
            case 'Х': return "KH";
            case 'Ц': return "C";
            case 'Ч': return "CH";
            case 'Ш': return "SH";
            case 'Щ': return "JSH";
            case 'Ъ': return "HH";
            case 'Ы': return "IH";
            case 'Ь': return "JH";
            case 'Э': return "EH";
            case 'Ю': return "JU";
            case 'Я': return "JA";
            default: return String.valueOf(ch);
        }
    }

    public static String getTranslitedRusToEngWord(String russian) {
        StringBuilder stringBuilder = new StringBuilder();
        for(char russianSymbol : russian.toUpperCase().toCharArray()) {
            stringBuilder.append(cyr2lat(russianSymbol));
        }
        return stringBuilder.toString().toLowerCase();
    }
}
