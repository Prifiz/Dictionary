package datamodel;

import exceptions.RecordHasNotSingleThemeException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Record implements Equivalent {
    public List<Word> getWords() {
        return words;
    }

    private List<Word> words;
    private String pictureName;
    private String description;
    private Similarity similarity = Similarity.DIFFERENT;
    private static EquivalenceStrategy equivalenceStrategy;
    private static boolean strategyAlreadySet = false;

    public static void setStrategy(EquivalenceStrategy strategy) {
        if(!strategyAlreadySet) {
            equivalenceStrategy = strategy;
            strategyAlreadySet = true;
        }
    }

    private static EquivalenceStrategy getEquivalenceStrategy() {
        return EquivalenceStrategy.ALL;
    }

    public String getDescription() {
        return description;
    }

    public Similarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Similarity similarity) {
        this.similarity = similarity;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureName() {
        return pictureName;
    }

    // FIXME different topics not supported
    // word cannot belong to different topics
    public String getTopicName() {
        for(Word word : words) {
            if(word.getTheme().getName() != null) {
                return word.getTheme().getName();
            }
        }
        return StringUtils.EMPTY;
    }

    public Record(List<Word> words, String pictureName) throws RecordHasNotSingleThemeException {
        this.words = words;
        this.pictureName = pictureName;
    }

    public Record(List<Word> words, String pictureName, String description) throws RecordHasNotSingleThemeException {
        this.words = words;
        this.pictureName = pictureName;
        this.description = description;
    }

    public Record(Map<String, String> recordParams) {
//        List<Word> words = new ArrayList<>();
//        for(Language language : Language.values()) {
//            if(recordParams.containsKey(language.name())) {
//                Word word = new Word()
//            }
//        }
        // TODO
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Record record = (Record) o;

        if (words != null ? !words.equals(record.words) : record.words != null) return false;
        if (pictureName != null ? !pictureName.equals(record.pictureName) : record.pictureName != null) return false;
        if (description != null ? !description.equals(record.description) : record.description != null) return false;
        return similarity == record.similarity;
    }

    @Override
    public int hashCode() {
        int result = words != null ? words.hashCode() : 0;
        result = 31 * result + (pictureName != null ? pictureName.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (similarity != null ? similarity.hashCode() : 0);
        return result;
    }

    protected Set<String> getLanguages(List<Word> words) {
        Set<String> result = new HashSet<>();
        for(Word word : words) {
            result.add(word.getLanguage());
        }
        return result;
    }

    protected Word getByLanguage(String language, List<Word> words) {
        Word word = null;
        for(Word currentWord : words) {
            if(language.equals(currentWord.getLanguage())) {
                return currentWord;
            }
        }
        return word;
    }

    @Override
    public boolean isEquivalent(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Record record = (Record) o;

        if(record.getWords().size() != this.getWords().size()) {
            return false;
        }

        if(!getLanguages(record.getWords()).equals(getLanguages(this.getWords()))) {
            return false;
        }

        if(EquivalenceStrategy.ALL.equals(equivalenceStrategy)) {
            return this.getWords().equals(record.getWords());
        } else if(EquivalenceStrategy.ANY.equals(equivalenceStrategy)) {
            for(String language : getLanguages(record.getWords())) {
                if(!getByLanguage(language, record.getWords()).equals(getByLanguage(language, this.getWords()))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }


}
