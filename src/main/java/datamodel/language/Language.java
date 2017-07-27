package datamodel.language;

public enum Language {
    ENGLISH, GERMAN, RUSSIAN;

    public static Language getByName(String name) throws IllegalArgumentException {
        for(Language language : values()) {
            if(name.toUpperCase().equals(language.toString())) {
                return language;
            }
        }
        throw new IllegalArgumentException("No such language!");
    }

    public static boolean isLanguage(String name) {
        boolean result = false;
        for(Language language : values()) {
            if(name.toUpperCase().equals(language.toString())) {
                return true;
            }
        }
        return result;
    }
}
