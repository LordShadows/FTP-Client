package javacode.implementationclasses;

import java.io.IOException;
import javacode.controllers.MainWindowController;
import javafx.application.Platform;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;


public class FTPUtil {

	public static void removeDirectory(MainWindowController MWC, String parentDir, String currentDir) throws IOException {
		String dirToList = parentDir;
		if (!currentDir.equals("")) {
			dirToList += "\\" + currentDir;
		}

		FTPFile[] subFiles = MWC.FTP.listFiles(dirToList + "\\");

		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				String currentFileName = aFile.getName();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					continue;
				}
				String filePath = parentDir + "\\" + currentDir + "\\"
						+ currentFileName;
				if (currentDir.equals("")) {
					filePath = parentDir + "\\" + currentFileName;
				}

				if (aFile.isDirectory()) {
					removeDirectory(MWC, dirToList, currentFileName);
				} else {
					boolean deleted = MWC.FTP.deleteFile(filePath);
					if (deleted) {
						Platform.runLater(() -> MWC.updateDeleteProgress(currentFileName, aFile.getSize()));
					} else {
						MWC.showError("Не удается удалить файл.", "По каким-то причинам невозможно удалить файл.");
						break;
					}
				}
			}
			if (!MWC.FTP.removeDirectory(dirToList)) {
				MWC.showError("Не удается удалить файл.", "По каким-то причинам невозможно удалить файл.");
			}
		}
	}

	public static long calculateDirectoryInfo(FTPClient ftpClient, String parentDir, String currentDir) throws IOException {
		long totalSize = 0;

		String dirToList = parentDir;
		if (!currentDir.equals("")) {
			dirToList += "\\" + currentDir;
		}

		FTPFile[] subFiles = ftpClient.listFiles(dirToList + "\\");

		if (subFiles != null && subFiles.length > 0) {
            for (FTPFile aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    continue;
                }
                if (aFile.isDirectory()) {
                    totalSize += calculateDirectoryInfo(ftpClient, dirToList, currentFileName);
                } else {
                    totalSize += aFile.getSize();
                }
            }
        }

		return totalSize;
	}
}