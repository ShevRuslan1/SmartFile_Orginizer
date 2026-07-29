package organizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class FileOrganizer {

    public List<FileMove> createPlan(Path folder) throws IOException {
        validateFolder(folder);

        List<FileMove> plan = new ArrayList<>();

        try (Stream<Path> files = Files.list(folder)) {
            files.filter(Files::isRegularFile)
                    .forEach(file -> {
                        String category = FileClassifier.classify(file);
                        Path destination = folder
                                .resolve(category)
                                .resolve(file.getFileName());

                        plan.add(new FileMove(file, destination));
                    });
        }

        return plan;
    }

    public void executePlan(List<FileMove> plan) throws IOException {
        for (FileMove move : plan) {
            Files.createDirectories(move.destination().getParent());

            Path safeDestination = findAvailableName(move.destination());

            Files.move(
                    move.source(),
                    safeDestination,
                    StandardCopyOption.ATOMIC_MOVE
            );
        }
    }

    private Path findAvailableName(Path destination) {
        if (!Files.exists(destination)) {
            return destination;
        }

        String fileName = destination.getFileName().toString();
        String baseName = fileName;
        String extension = "";

        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        int counter = 1;

        while (true) {
            Path candidate = destination.getParent()
                    .resolve(baseName + " (" + counter + ")" + extension);

            if (!Files.exists(candidate)) {
                return candidate;
            }

            counter++;
        }
    }

    private void validateFolder(Path folder) {
        if (!Files.exists(folder)) {
            throw new IllegalArgumentException("Folder does not exist.");
        }

        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("The selected path is not a folder.");
        }
    }
}