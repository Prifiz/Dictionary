package datamodel;

import exceptions.RecordHasNotSingleThemeException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class Record {
    public List<Word> getWords() {
        return words;
    }

    private List<Word> words;
    private String pictureName;
    private String description;
    private Similarity similarity = Similarity.DIFFERENT;

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
}
