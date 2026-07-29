package organizer;

import java.nio.file.Path;

public record FileMove(Path source, Path destination) {
}