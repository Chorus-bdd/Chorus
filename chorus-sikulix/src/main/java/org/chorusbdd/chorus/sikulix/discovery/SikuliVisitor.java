package org.chorusbdd.chorus.sikulix.discovery;

import org.chorusbdd.chorus.logging.ChorusLog;
import org.chorusbdd.chorus.logging.ChorusLogFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * A FileVisitor for locating the Sikuli's
 *
 * @author Stephen Lake
 */
class SikuliVisitor implements FileVisitor<Path> {

	private ChorusLog log = ChorusLogFactory.getLog(this.getClass());

	private final SikuliDiscovery result;
	private final Path baseDirectory;

	SikuliVisitor(SikuliDiscovery result, Path baseDirectory) {
		this.result = result;
		this.baseDirectory = baseDirectory;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if (dir.toString().endsWith(".sikuli")) {
			Path absoluteDirPath = dir.toAbsolutePath().normalize();
			// we make the assumption that the class name follows the Sikuli convention of naming the main
			// class file the same as the sikuli directory, but without the postfix.
			// i.e.  c:/sikulis/rfq/control.sikuli will have a class and file name of control.
			Path mostNestedDirectory = absoluteDirPath.subpath(absoluteDirPath.getNameCount() - 1, absoluteDirPath.getNameCount());
			String className = mostNestedDirectory.toString().replace(".sikuli", "");

			List<String> packageName = new ArrayList<>();
			for (Path path : baseDirectory.relativize(absoluteDirPath)) {
				packageName.add(path.toString().replace(".sikuli", ""));
			}

			result.addSikuliPackage(new SikuliPackage(className, packageName));
			result.addSikulRoot(absoluteDirPath.getParent());

			return FileVisitResult.SKIP_SUBTREE;
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		log.error("Cannot visit path: " + file);
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
}