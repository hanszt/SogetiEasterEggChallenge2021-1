package nl.sogeti.service;

import nl.sogeti.model.appearance.Resource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class ThemeService implements IThemeService {

    private static final String RELATIVE_STYLE_SHEET_RESOURCE_DIR = "/css";

    private static final Logger LOGGER = LogManager.getLogger(ThemeService.class);

    private final StringProperty styleSheet = new SimpleStringProperty();
    private final ObjectProperty<Resource> currentTheme = new SimpleObjectProperty<>();

    private final Set<Resource> themes;

    public ThemeService() {
        this.themes = scanForThemeStyleSheets();
        currentTheme.addListener((observableValue, theme, newTheme) -> loadThemeStyleSheet(newTheme));
    }

    private Set<Resource> scanForThemeStyleSheets() {
        Set<Resource> themeSet = new TreeSet<>(Set.of(DEFAULT_THEME));
        URL url = getClass().getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR);
        if (url != null) {
            File styleDirectory = new File(url.getFile());
            if (styleDirectory.isDirectory()) {
                String[] fileNames = styleDirectory.list();
                for (String fileName : fileNames) {
                    String themeName = extractThemeName(fileName);
                    themeSet.add(new Resource(themeName, fileName));
                }
            }
        } else LOGGER.error("Stylesheet resource folder not found...");
        return themeSet;
    }

    private String extractThemeName(String fileName) {
        String themeName = fileName
                .replace("style-", "")
                .replace('-', ' ')
                .replace(".css", "");
        themeName = themeName.substring(0, 1).toUpperCase() + themeName.substring(1).toLowerCase();
        return themeName;
    }

    private void loadThemeStyleSheet(Resource theme) {
        URL styleSheetUrl = getClass()
                .getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR + "/" + theme.getFileName());
        if (styleSheetUrl != null) {
            LOGGER.debug(styleSheetUrl);
            this.styleSheet.set(styleSheetUrl.toExternalForm());
            LOGGER.debug(styleSheetUrl.toExternalForm());
        } else LOGGER.error("stylesheet of " + theme.getName() + " could not be loaded...");
    }

    public StringProperty styleSheetProperty() {
        return styleSheet;
    }

    public ObjectProperty<Resource> currentThemeProperty() {
        return currentTheme;
    }

    public Set<Resource> getThemes() {
        return Collections.unmodifiableSet(themes);
    }

}
