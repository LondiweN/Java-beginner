package com.oracle.stcurr.ide.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author mheimer
 */
public class LessonReader {

    private static final Logger logger = Logger.getLogger("com.oracle.stcurr.ide.web");
    private Path basePath;
    private SortedMap<String, SortedMap<String, Map<Path, String>>> chapterExercises = new TreeMap<>();
    private SortedMap<String, SortedMap<String, Map<Path, String>>> chapterSolutions = new TreeMap<>();
    private Map<String, Map<String, String>> readmes = new HashMap<>();

    public LessonReader() {
        Path userDocLabsPath = Paths.get(System.getProperty("user.home"), "Documents", "labs");
        Path userLabsPath = Paths.get(System.getProperty("user.home"), "labs");
        Path rootLabsPath = new File("/labs").toPath();
        if (Files.exists(userDocLabsPath) && Files.isDirectory(userDocLabsPath)) {
            init(userDocLabsPath);
        } else if (Files.exists(userLabsPath) && Files.isDirectory(userLabsPath)) {
            init(userLabsPath);
        } else if (Files.exists(rootLabsPath) && Files.isDirectory(rootLabsPath)) {
            init(rootLabsPath);
        } else {
            init(Paths.get(System.getProperty("user.home")));
        }
    }

    // completes the loading enough that we have a list of chapters and their exercises
    // exercise files are not load unless read with getExerciseFiles or getSolutionFiles
    public LessonReader(Path basePath) {
        init(basePath);
    }
    
    private void init(Path basePath) {
        logger.log(Level.INFO, "Using base path : {0}", basePath.toAbsolutePath().toString());
        this.basePath = basePath;
        if (basePathExists()) {
            try (DirectoryStream<Path> chapterDS = Files.newDirectoryStream(basePath, "[0-9][0-9]-*");) {
                for (Path chapterPath : chapterDS) {
                    SortedMap<String, Map<Path, String>> exercises = new TreeMap<>();
                    SortedMap<String, Map<Path, String>> solutions = new TreeMap<>();
                    readmes.put(chapterPath.getFileName().toString(), new HashMap<String, String>());
                    chapterExercises.put(chapterPath.getFileName().toString(), exercises);
                    chapterSolutions.put(chapterPath.getFileName().toString(), solutions);
                    Path exercisesPath = chapterPath.resolve("Exercises");
                    if (Files.exists(exercisesPath) && Files.isDirectory(exercisesPath)) {
                        try (DirectoryStream<Path> chapterExerciseDS = Files.newDirectoryStream(exercisesPath, "Exercise[0-9]*");) {
                            for (Path chapterExercisePath : chapterExerciseDS) {
                                Map<Path, String> exerciseFiles = new HashMap<>();
                                Map<Path, String> solutionFiles = new HashMap<>();
                                exercises.put(chapterExercisePath.getFileName().toString(), exerciseFiles);
                                solutions.put(chapterExercisePath.getFileName().toString(), solutionFiles);
                            }
                        } catch (IOException e) {
                            logger.log(Level.WARNING, "Problem reading exercises", e);
                        }
                    }
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Problem reading exercises", e);
            }
        }
    }

    private void loadFiles(String chapterName, String exerciseName) {
        Map<Path, String> exFiles = chapterExercises.get(chapterName).get(exerciseName);
        Map<Path, String> solFiles = chapterSolutions.get(chapterName).get(exerciseName);
        Path chapterExercisePath = basePath.resolve(chapterName).resolve("Exercises").resolve(exerciseName);
        try (DirectoryStream<Path> exerciseFileDS = Files.newDirectoryStream(chapterExercisePath)) {
            for (Path exerciseFilePath : exerciseFileDS) {
                if (Files.isRegularFile(exerciseFilePath)) {
                    if (exerciseFilePath.toString().endsWith(".java")) {
                        exFiles.put(exerciseFilePath.toAbsolutePath(), getFileContent(exerciseFilePath));
                    } else if (exerciseFilePath.getFileName().toString().equalsIgnoreCase("readme.txt")) {
                        readmes.get(chapterName).put(exerciseName, "<pre>" + StringEscapeUtils.escapeHtml4(getFileContent(exerciseFilePath)) + "</pre>");
                    } else if (exerciseFilePath.getFileName().toString().equalsIgnoreCase("readme.html")) {
                        readmes.get(chapterName).put(exerciseName, getFileContent(exerciseFilePath));
                    }
                } else if (Files.isDirectory(exerciseFilePath)) {
                    if (exerciseFilePath.endsWith("Solution")) {
                        solFiles.putAll(findAllJavaFiles(exerciseFilePath));
                    } else {
                        exFiles.putAll(findAllJavaFiles(exerciseFilePath));
                    }
                }

            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Problem load exercise files", e);
        }
    }

    public SortedMap<String, SortedMap<String, Map<Path, String>>> getChapterSolutions() {
        return chapterSolutions;
    }

    public SortedMap<String, SortedMap<String, Map<Path, String>>> getChapterExercises() {
        return chapterExercises;
    }

    public Map<Path, String> getExerciseFiles(String chapterName, String exerciseName) {
        return lazyLoad(chapterExercises, chapterName, exerciseName);
    }

    public Map<Path, String> getSolutionFiles(String chapterName, String exerciseName) {
        return lazyLoad(chapterSolutions, chapterName, exerciseName);
    }
    
    public String getReadme(String chapterName, String exerciseName) {
        lazyLoad(chapterExercises, chapterName, exerciseName);
        return readmes.get(chapterName).get(exerciseName);
    }
    
    private Map<Path, String> lazyLoad(SortedMap<String, SortedMap<String, Map<Path, String>>> chapters, String chapterName, String exerciseName) {
        Map<Path, String> files = chapters.get(chapterName).get(exerciseName);
        if (files.size() > 0) {
            return files;
        } else {
            loadFiles(chapterName, exerciseName);
        }
        return files;
    }

    public final boolean basePathExists() {
        return Files.exists(basePath) && Files.isDirectory(basePath);
    }

    private String getFileContent(Path file) {
        StringBuilder sb = new StringBuilder();
        Charset charset = Charset.defaultCharset();
        try (BufferedReader reader = Files.newBufferedReader(file, charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Problem reading file", e);
        }
        return sb.toString();
    }

    private Map<Path, String> findAllJavaFiles(Path path) {
        Map<Path, String> javaFiles = new HashMap<>();
        findAllJavaFiles(javaFiles, path);
        return javaFiles;
    }

    private void findAllJavaFiles(Map<Path, String> javaFiles, Path path) {
        if (Files.isRegularFile(path) && path.toString().endsWith(".java")) {
            javaFiles.put(path.toAbsolutePath(), getFileContent(path));
        }
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path subPath : ds) {
                    findAllJavaFiles(javaFiles, subPath);
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "Problem reading exercises", e);
            }
        }
    }
}
