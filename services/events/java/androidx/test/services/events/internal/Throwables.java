package androidx.test.services.events.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copy of org.junit.internal.Throwables#getTrimmedStackTrace from
 * https://github.com/junit-team/junit4/pull/1028
 *
 * <p>TODO(b/128614857): Remove once androidx.test can use JUnit 4.13 directly
 */
final class Throwables {

  private Throwables() {}

  /**
   * Gets a trimmed version of the stack trace of the given exception. Stack trace elements that are
   * below the test method are filtered out.
   *
   * @return a trimmed stack trace, or the original trace if trimming wasn't possible
   */
  public static String getTrimmedStackTrace(Throwable exception) {
    List<String> trimmedStackTraceLines = getTrimmedStackTraceLines(exception);
    if (trimmedStackTraceLines.isEmpty()) {
      return getFullStackTrace(exception);
    }

    StringBuilder result = new StringBuilder(exception.toString());
    appendStackTraceLines(trimmedStackTraceLines, result);
    appendStackTraceLines(getCauseStackTraceLines(exception), result);
    return result.toString();
  }

  private static List<String> getTrimmedStackTraceLines(Throwable exception) {
    List<StackTraceElement> stackTraceElements = Arrays.asList(exception.getStackTrace());
    int linesToInclude = stackTraceElements.size();

    State state = State.PROCESSING_OTHER_CODE;
    for (StackTraceElement stackTraceElement : asReversedList(stackTraceElements)) {
      state = state.processStackTraceElement(stackTraceElement);
      if (state == State.DONE) {
        List<String> trimmedLines = new ArrayList<String>(linesToInclude + 2);
        trimmedLines.add("");
        for (StackTraceElement each : stackTraceElements.subList(0, linesToInclude)) {
          trimmedLines.add("\tat " + each);
        }
        if (exception.getCause() != null) {
          trimmedLines.add(
              "\t... " + (stackTraceElements.size() - trimmedLines.size()) + " trimmed");
        }
        return trimmedLines;
      }
      linesToInclude--;
    }
    return Collections.emptyList();
  }

  private static List<String> getCauseStackTraceLines(Throwable exception) {
    if (exception.getCause() != null) {
      String fullTrace = getFullStackTrace(exception);
      BufferedReader reader =
          new BufferedReader(new StringReader(fullTrace.substring(exception.toString().length())));
      List<String> causedByLines = new ArrayList<String>();

      try {
        String line;
        while ((line = reader.readLine()) != null) {
          if (line.startsWith("Caused by: ")) {
            causedByLines.add(line);
            while ((line = reader.readLine()) != null) {
              causedByLines.add(line);
            }
            return causedByLines;
          }
        }
      } catch (IOException e) {
        // We should never get here, because we are reading from a StringReader
      }
    }

    return Collections.emptyList();
  }

  private static String getFullStackTrace(Throwable exception) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    exception.printStackTrace(writer);
    return stringWriter.toString();
  }

  private static void appendStackTraceLines(
      List<String> stackTraceLines, StringBuilder destBuilder) {
    for (String stackTraceLine : stackTraceLines) {
      destBuilder.append(String.format("%s%n", stackTraceLine));
    }
  }

  private static <T> List<T> asReversedList(final List<T> list) {
    return new AbstractList<T>() {

      @Override
      public T get(int index) {
        return list.get(list.size() - index - 1);
      }

      @Override
      public int size() {
        return list.size();
      }
    };
  }

  private enum State {
    PROCESSING_OTHER_CODE {
      @Override
      public State processLine(String methodName) {
        if (isTestFrameworkMethod(methodName)) {
          return PROCESSING_TEST_FRAMEWORK_CODE;
        }
        return this;
      }
    },
    PROCESSING_TEST_FRAMEWORK_CODE {
      @Override
      public State processLine(String methodName) {
        if (isReflectionMethod(methodName)) {
          return PROCESSING_REFLECTION_CODE;
        } else if (isTestFrameworkMethod(methodName)) {
          return this;
        }
        return PROCESSING_OTHER_CODE;
      }
    },
    PROCESSING_REFLECTION_CODE {
      @Override
      public State processLine(String methodName) {
        if (isReflectionMethod(methodName)) {
          return this;
        } else if (isTestFrameworkMethod(methodName)) {
          // This is here to handle TestCase.runBare() calling TestCase.runTest().
          return PROCESSING_TEST_FRAMEWORK_CODE;
        }
        return DONE;
      }
    },
    DONE {
      @Override
      public State processLine(String methodName) {
        return this;
      }
    };

    /** Processes a stack trace element method name, possibly moving to a new state. */
    protected abstract State processLine(String methodName);

    /** Processes a stack trace element, possibly moving to a new state. */
    public final State processStackTraceElement(StackTraceElement element) {
      return processLine(element.getClassName() + "." + element.getMethodName() + "()");
    }
  }

  private static final String[] TEST_FRAMEWORK_METHOD_NAME_PREFIXES = {
    "org.junit.runner.",
    "org.junit.runners.",
    "org.junit.experimental.runners.",
    "org.junit.internal.",
    "junit.",
  };

  private static final String[] TEST_FRAMEWORK_TEST_METHOD_NAME_PREFIXES = {
    "org.junit.internal.StackTracesTest",
  };

  private static boolean isTestFrameworkMethod(String methodName) {
    return isMatchingMethod(methodName, TEST_FRAMEWORK_METHOD_NAME_PREFIXES)
        && !isMatchingMethod(methodName, TEST_FRAMEWORK_TEST_METHOD_NAME_PREFIXES);
  }

  private static final String[] REFLECTION_METHOD_NAME_PREFIXES = {
    "sun.reflect.",
    "java.lang.reflect.",
    // ANDROID-CHANGED - filter jdk 11
    "jdk.internal.reflect.",
    "org.junit.rules.RunRules.<init>(",
    "org.junit.rules.RunRules.applyAll(", // calls TestRules
    "org.junit.runners.BlockJUnit4ClassRunner.withMethodRules(", // calls MethodRules
    "junit.framework.TestCase.runBare(", // runBare() directly calls setUp() and tearDown()
  };

  private static boolean isReflectionMethod(String methodName) {
    return isMatchingMethod(methodName, REFLECTION_METHOD_NAME_PREFIXES);
  }

  private static boolean isMatchingMethod(String methodName, String[] methodNamePrefixes) {
    for (String methodNamePrefix : methodNamePrefixes) {
      if (methodName.startsWith(methodNamePrefix)) {
        return true;
      }
    }

    return false;
  }
}
