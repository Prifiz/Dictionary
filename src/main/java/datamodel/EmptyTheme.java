package datamodel;

import org.apache.commons.lang3.StringUtils;

public class EmptyTheme extends Theme {
    public EmptyTheme() {
        super(StringUtils.EMPTY, "DefaultEmptyTheme");
    }
}
