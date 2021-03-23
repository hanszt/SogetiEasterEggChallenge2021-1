package nl.sogeti.service;

import javafx.beans.property.StringProperty;

import java.util.List;

public interface IAboutService {

    List<AboutText> loadContent();

    class AboutText {

        private final String title;
        private final StringProperty text;

        public AboutText(String title, StringProperty text) {
            this.title = title;
            this.text = text;
        }

        public String getText() {
            return text.get();
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
