package organizer;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class FileClassifier {
    private static final Map<String, Set<String>> CATEGORIES = Map.of(
            "Images", Set.of("jpg", "jpeg", "png", "gif", "webp", "svg"),
            "Documents", Set.of("pdf", "doc", "docx", "txt", "rtf", "odt"),
            "Videos", Set.of("mp4", "mkv", "mov", "avi", "webm"),
            "Archives", Set.of("zip", "rar", "7z", "tar", "gz"),
            "Applications", Set.of("exe", "msi", "deb", "rpm", "dmg", "appimage")
    );

    private FileClassifier(){
    }

    public static String classify(Path file){
        String extension = getExtension(file);

        return CATEGORIES.entrySet()
                .stream()
                .filter(entry -> entry.getValue().contains(extension))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("other");
    }

    private static String getExtension(Path file){
        String name = file.getFileName().toString();
        int dotIndex = name.lastIndexOf(".");

        if (dotIndex <= 0 || dotIndex == name.length() - 1) {
            return "";
        }

        return name.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

}
