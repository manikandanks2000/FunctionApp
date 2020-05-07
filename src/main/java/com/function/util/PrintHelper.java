package com.function.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.microsoft.azure.storage.StorageException;


public final class PrintHelper {

  /**
   * Prints out the sample start information .
   */
  static void printSampleStartInfo(String sampleName) {
      System.out.println(String.format(
              "%s samples starting...",
              sampleName));
  }

  /**
   * Prints out the sample complete information .
   */
  static void printSampleCompleteInfo(String sampleName) {
      System.out.println(String.format(
              "%s samples completed.",
              sampleName));
  }

  /**
   * Print the exception stack trace
   *
   * @param t Exception to be printed
   */
  public static void printException(Throwable t) {

      StringWriter stringWriter = new StringWriter();
      PrintWriter printWriter = new PrintWriter(stringWriter);
      t.printStackTrace(printWriter);
      if (t instanceof StorageException) {
          if (((StorageException) t).getExtendedErrorInformation() != null) {
              System.out.println(String.format("\nError: %s", ((StorageException) t).getExtendedErrorInformation().getErrorMessage()));
          }
      }
      System.out.println(String.format("Exception details:\n%s", stringWriter.toString()));
  }
}