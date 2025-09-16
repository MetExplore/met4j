package utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * The utility class used to launch the CLI from integration tests.
 */
public class IThelper {

    private static Path jarPathOverride = null;
    private static final Path JAR_PATH = resolveJarPath();

    /**
     * Resolve the path to the JAR file. It assumes the JAR is located in the "target" directory
     * relative to the current working directory, following a maven build.
     * @return path to the JAR file
     * @throws IllegalStateException if the JAR file is not found
     */
    private static Path resolveJarPath(){
        if (jarPathOverride != null) {
            return jarPathOverride;
        }
        String baseDir = System.getProperty("user.dir");
        Path jar = Paths.get(baseDir, "target", "met4j-toolbox-"+getApplicationVersion()+".jar");
        if (!Files.exists(jar)) {
            throw new IllegalStateException("JAR not found: " + jar);
        }
        return jar;
    }

    /**
     * Retrieves the application version from the package metadata.
     *
     * @return the application version as a String, or "unable to reach" if not available
     */
    public static String getApplicationVersion() {
        try {
            Properties properties = new Properties();
            properties.load(IThelper.class.getClassLoader().getResourceAsStream("project.properties"));
            return properties.getProperty("version");
        }catch (IOException e){
            System.err.println("unable to fetch version");
            return "0.0.0";
        }

    }

    /**
     * Runs the CLI with the specified arguments and captures the output.
     *
     * @param args the command-line arguments to pass to the CLI
     * @return a {@link ProcessResult} containing the exit code and standard output
     * @throws IOException          if an I/O error occurs
     * @throws InterruptedException if the process is interrupted
     */
    public static ProcessResult runCli(String... args) throws IOException, InterruptedException {
        // Build the command to execute
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-cp");
        command.add(String.valueOf(JAR_PATH));
        command.addAll(Arrays.asList(args));

        // Start the process
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        // Wait for the process to finish with a timeout
        boolean finished = process.waitFor(6, TimeUnit.MINUTES);
        if (!finished) {
            // Timeout - kill the process
            process.destroyForcibly();
            throw new RuntimeException("Process timed out");
        }

        // Capture the output
        String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(command);
        System.out.println(output);
        int exitCode = process.exitValue();

        // Return the result
        return new ProcessResult(exitCode, output);
    }

    /**
     * Copies a resource file from the project resources to a temporary directory.
     *
     * @param resourceName  the name of the resource file to copy
     * @param tempDirectory the temporary directory to copy the file to
     * @return the absolute path to the copied file
     * @throws IOException if an I/O error occurs
     */
    public static String copyProjectResource(String resourceName, Path tempDirectory) throws IOException
    {
        // Get the resource as a stream
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream stream = classLoader.getResourceAsStream(resourceName);
        if(stream == null){
            System.err.println("file not found");
            throw new IOException();
        }

        // Create a temporary file in the specified directory
        File fileList = Files.createTempFile(tempDirectory,"tmp_",resourceName).toFile();

        // Copy the resource content to the temporary file
        OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileList, true));
        byte[] bucket = new byte[32*1024];
        int bytesRead = 0;
        while(bytesRead != -1){
            bytesRead = stream.read(bucket); //-1, 0, or more
            if(bytesRead > 0){
                outStream.write(bucket, 0, bytesRead);
            }
        }

        outStream.close();
        stream.close();

        // Return the path to the temporary file
        return fileList.getAbsolutePath();

    }

    /**
     * Validates if the provided file is a well-formed XML.
     *
     * @param xmlFile the XML file to validate
     * @return true if the file is a valid XML, false otherwise
     */
    public static boolean isValidXml(File xmlFile) {
        try {
            javax.xml.parsers.DocumentBuilder builder =
                    javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
            builder.parse(xmlFile);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Allows overriding the JAR path for testing or custom setups.
     * @param path the custom path to the JAR file
     */
    public static void setJarPath(Path path) {
        jarPathOverride = path;
    }

    /**
     * A record to hold the result of a process execution, including the exit code and standard output.
     *
     * @param exitCode the exit code of the process
     * @param stdout   the standard output of the process
     */
    public record ProcessResult(int exitCode, String stdout) {}

    public static void main(String[] args) throws IOException {
        System.out.println(getApplicationVersion());
    }
}