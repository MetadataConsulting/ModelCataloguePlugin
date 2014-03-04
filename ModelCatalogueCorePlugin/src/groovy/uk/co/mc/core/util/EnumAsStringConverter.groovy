package uk.co.mc.core.util

import java.beans.PropertyEditorSupport

/**
 * Created by adammilward on 27/02/2014.
 */
class EnumAsStringConverter extends PropertyEditorSupport {

    @Override
    def String getAsText() {
        return value.replaceAll("\\|", " \\| ").replaceAll("\\:", " \\: ")
    }

    @Override
    void setAsText(String text) {
        this.value = text.replaceAll(" \\| ", "\\|").replaceAll(" \\: ", "\\:")
    }

}
