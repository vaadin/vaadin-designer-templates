package com.vaadin.designer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class StyleCopy {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println("Usage: <template src dir> <template theme dir> <destination dir>");
			System.exit(0);
		}

		File htmlDir = new File(args[0]);
		File scssDir = new File(args[1]);
		File destinationDir = new File(args[2]);

		if (!htmlDir.exists() || !scssDir.exists() || !destinationDir.exists()) {
			System.out.println("One of the argument directories doesn't exist.");
		}

		Collection<File> htmlFiles = FileUtils.listFiles(htmlDir, new String[] { "html" }, false);

		for (File htmlFile : htmlFiles) {
			File scssFile = new File(scssDir.getAbsolutePath() + "/"
					+ htmlFile.getName().substring(0, htmlFile.getName().lastIndexOf('.')) + ".scss");
			List<String> lines = new ArrayList<>();
			if (scssFile.exists()) {
				try {
					LineIterator htmlIterator = FileUtils.lineIterator(htmlFile);
					LineIterator scssIterator = FileUtils.lineIterator(scssFile);
					while (htmlIterator.hasNext()) {
						String htmlLine = htmlIterator.next();
						if (htmlLine.contains("<head>")) {
							lines.add("<head>");
							lines.add("<style>");
							while (scssIterator.hasNext()) {
								lines.add(scssIterator.next());
							}
							lines.add("</style>");
						} else {
							lines.add(htmlLine);
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (lines.isEmpty()) {
				System.out.println(String.format("Not writing SCSS for %s. Nothing to write.", htmlFile.getName(),
						scssFile.getName()));
			}

			File destinationFile = new File(destinationDir.getAbsolutePath() + "/" + htmlFile.getName());
			if (destinationFile.exists()) {
				if (!destinationFile.delete()) {
					System.out.println("Failed to delete existing file " + destinationFile.getName());
					continue;
				}
			}

			if (lines.isEmpty() && htmlFile.exists()) {
				try {
					FileUtils.copyFile(htmlFile, destinationFile);
					System.out.println("Wrote " + destinationFile.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					destinationFile.createNewFile();
					FileUtils.writeLines(destinationFile, lines);
					System.out.println("Wrote " + destinationFile.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
