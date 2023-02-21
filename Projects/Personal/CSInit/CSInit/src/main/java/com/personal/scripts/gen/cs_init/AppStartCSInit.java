package com.personal.scripts.gen.cs_init;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

final class AppStartCSInit {

	private AppStartCSInit() {
	}

	public static void main(
			final String[] args) {

		final Instant start = Instant.now();

		if (args.length < 1) {

			final String helpMessage = createHelpMessage();
			System.err.println("ERROR - insufficient arguments" + System.lineSeparator() + helpMessage);
			System.exit(-1);
		}

		if ("-help".equals(args[0])) {

			final String helpMessage = createHelpMessage();
			System.out.println(helpMessage);
			System.exit(0);
		}

		final String csProjectPathString = args[0];

		System.out.println(" --> C# project initializer starting");

		final Path csProjectPath = Paths.get(csProjectPathString).toAbsolutePath().normalize();
		System.out.println(" --> project path:" + System.lineSeparator() + csProjectPath);

		final String projectName = csProjectPath.getFileName().toString();
		if (projectName.isBlank()) {

			System.err.println("ERROR - project name cannot be computed from project path");
			System.exit(2);
		}
		System.out.println(" --> project name: " + projectName);

		try {
			createFile(".vscode/launch.json", csProjectPath, projectName);
			createFile(".vscode/tasks.json", csProjectPath, projectName);
			createFile("AppStartPROJECT_NAME.cs", csProjectPath, projectName);
			createFile("PROJECT_NAME.csproj", csProjectPath, projectName);
			createFile("REPLACE_WITH_BLANK.gitignore", csProjectPath, projectName);

		} catch (final Exception exc) {
			System.err.println("ERROR - failed to initialize the C# project");
			exc.printStackTrace();
		}

		final Duration executionTime = Duration.between(start, Instant.now());
		System.out.println("done; execution time: " + durationToString(executionTime));
	}

	private static String createHelpMessage() {

		return "usage: cs_init <folder_path>";
	}

	private static void createFile(
			final String resourceFilePathString,
			final Path csProjectPath,
			final String projectName) throws Exception {

		String outputRelativePathString = resourceFilePathString.replace("PROJECT_NAME", projectName);
		outputRelativePathString = outputRelativePathString.replace("REPLACE_WITH_BLANK", "");
		final Path outputFilePath = csProjectPath.resolve(outputRelativePathString);

		System.out.println("creating file:" + System.lineSeparator() + outputFilePath);

		Files.createDirectories(outputFilePath.getParent());

		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				Objects.requireNonNull(Thread.currentThread().getContextClassLoader()
						.getResourceAsStream(resourceFilePathString))));
				PrintStream printStream = new PrintStream(
						new BufferedOutputStream(Files.newOutputStream(outputFilePath)))) {

			String line;
			while ((line = bufferedReader.readLine()) != null) {

				line = line.replace("%PROJECT_NAME%", projectName);
				printStream.print(line);
				printStream.println();
			}
		}
	}

	private static String durationToString(
			final Duration duration) {

		final StringBuilder stringBuilder = new StringBuilder();
		final long allSeconds = duration.get(ChronoUnit.SECONDS);
		final long hours = allSeconds / 3600;
		if (hours > 0) {
			stringBuilder.append(hours).append("h ");
		}

		final long minutes = (allSeconds - hours * 3600) / 60;
		if (minutes > 0) {
			stringBuilder.append(minutes).append("m ");
		}

		final long nanoseconds = duration.get(ChronoUnit.NANOS);
		final double seconds = allSeconds - hours * 3600 - minutes * 60 +
				nanoseconds / 1_000_000_000.0;
		stringBuilder.append(doubleToString(seconds)).append('s');

		return stringBuilder.toString();
	}

	private static String doubleToString(
			final double d) {

		final String str;
		if (Double.isNaN(d)) {
			str = "";

		} else {
			final String format;
			format = "0.000";
			final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
			str = decimalFormat.format(d);
		}
		return str;
	}
}
