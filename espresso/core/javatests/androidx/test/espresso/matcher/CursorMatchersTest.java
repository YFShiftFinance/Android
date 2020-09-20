/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.test.espresso.matcher;

import static androidx.test.espresso.matcher.CursorMatchers.withRowBlob;
import static androidx.test.espresso.matcher.CursorMatchers.withRowDouble;
import static androidx.test.espresso.matcher.CursorMatchers.withRowFloat;
import static androidx.test.espresso.matcher.CursorMatchers.withRowInt;
import static androidx.test.espresso.matcher.CursorMatchers.withRowLong;
import static androidx.test.espresso.matcher.CursorMatchers.withRowShort;
import static androidx.test.espresso.matcher.CursorMatchers.withRowString;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.rules.ExpectedException.none;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Build;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class CursorMatchersTest {

  private static final int ROW_ID = 0;

  private static final short SHORT_VALUE = Short.MAX_VALUE;
  private static final Matcher<Short> SHORT_VALUE_MATCHER = is(SHORT_VALUE);

  private static final int INTEGER_VALUE = Integer.MAX_VALUE;
  private static final Matcher<Integer> INTEGER_VALUE_MATCHER = is(INTEGER_VALUE);

  private static final long LONG_VALUE = Long.MAX_VALUE;
  private static final Matcher<Long> LONG_VALUE_MATCHER = is(LONG_VALUE);

  private static final float FLOAT_VALUE = Float.MAX_VALUE;
  private static final Matcher<Float> FLOAT_VALUE_MATCHER = is(FLOAT_VALUE);

  private static final double DOUBLE_VALUE = Double.MAX_VALUE;
  private static final Matcher<Double> DOUBLE_VALUE_MATCHER = is(DOUBLE_VALUE);

  private static final String STRING_VALUE = "string value";
  private static final Matcher<String> STRING_VALUE_MATCHER = is(STRING_VALUE);

  private static final byte[] BLOB_VALUE = STRING_VALUE.getBytes();
  private static final Matcher<byte[]> BLOB_VALUE_MATCHER = is(BLOB_VALUE);

  private static final String COLUMN_ID = "_id";
  private static final String COLUMN_SHORT = "_column_short";
  private static final String COLUMN_INT = "_column_int";
  private static final String COLUMN_LONG = "_column_long";
  private static final String COLUMN_FLOAT = "_column_float";
  private static final String COLUMN_DOUBLE = "_column_double";
  private static final String COLUMN_STR = "_column_str";
  private static final String COLUMN_BLOB = "_column_blob";

  private static final String[] COLUMN_NAMES =
      new String[] {
        COLUMN_ID,
        COLUMN_SHORT,
        COLUMN_INT,
        COLUMN_LONG,
        COLUMN_FLOAT,
        COLUMN_DOUBLE,
        COLUMN_STR,
        COLUMN_BLOB
      };
  private static final Object[] COLUMN_VALUES =
      new Object[] {
        ROW_ID,
        SHORT_VALUE,
        INTEGER_VALUE,
        LONG_VALUE,
        FLOAT_VALUE,
        DOUBLE_VALUE,
        STRING_VALUE,
        BLOB_VALUE
      };

  private Cursor cursor;

  @Rule public ExpectedException expectedException = none();

  @Before
  public void setUp() throws Exception {
    cursor = makeCursor(COLUMN_NAMES, COLUMN_VALUES);
    cursor.moveToFirst();
  }

  @Test
  public void checkPreconditions() {
    assertNotNull(cursor);
  }

  @Test
  public void withRowShort_columnIndexAndValue() {
    assertTrue(withRowShort(1, SHORT_VALUE).matches(cursor));
  }

  @Test
  public void withRowShort_columnIndexAndValueMatcher() {
    assertTrue(withRowShort(1, SHORT_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowShort_columnNameAndValue() {
    assertTrue(withRowShort(COLUMN_SHORT, SHORT_VALUE).matches(cursor));
  }

  @Test
  public void withRowShort_columnNameAndValueMatcher() {
    assertTrue(withRowShort(COLUMN_SHORT, SHORT_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowShort_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowShort(is(COLUMN_SHORT), SHORT_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowShort_ValueDoesNotMatch() {
    assertFalse(withRowShort(COLUMN_SHORT, (short) -1).matches(cursor));
  }

  @Test
  public void withRowInt_columnIndexAndValue() {
    assertTrue(withRowInt(2, INTEGER_VALUE).matches(cursor));
  }

  @Test
  public void withRowInt_columnIndexAndValueMatcher() {
    assertTrue(withRowInt(2, INTEGER_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowInt_columnNameAndValue() {
    assertTrue(withRowInt(COLUMN_INT, INTEGER_VALUE).matches(cursor));
  }

  @Test
  public void withRowInt_columnNameAndValueMatcher() {
    assertTrue(withRowInt(COLUMN_INT, INTEGER_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowInt_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowInt(is(COLUMN_INT), INTEGER_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowInt_ValueDoesNotMatch() {
    assertFalse(withRowInt(COLUMN_INT, -1).matches(cursor));
  }

  @Test
  public void withRowLong_columnIndexAndValue() {
    assertTrue(withRowLong(3, LONG_VALUE).matches(cursor));
  }

  @Test
  public void withRowLong_columnIndexAndValueMatcher() {
    assertTrue(withRowLong(3, LONG_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowLong_columnNameAndValue() {
    assertTrue(withRowLong(COLUMN_LONG, LONG_VALUE).matches(cursor));
  }

  @Test
  public void withRowLongTesting() {
    assertTrue(withRowLong(is(COLUMN_LONG), LONG_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowLong_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowLong(COLUMN_LONG, LONG_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowLong_ValueDoesNotMatch() {
    assertFalse(withRowLong(COLUMN_LONG, -1).matches(cursor));
  }

  @Test
  public void withRowFloat_columnIndexAndValue() {
    assertTrue(withRowFloat(4, FLOAT_VALUE).matches(cursor));
  }

  @Test
  public void withRowFloat_columnIndexAndValueMatcher() {
    assertTrue(withRowFloat(4, FLOAT_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowFloat_columnNameAndValue() {
    assertTrue(withRowFloat(COLUMN_FLOAT, FLOAT_VALUE).matches(cursor));
  }

  @Test
  public void withRowFloat_columnNameAndValueMatcher() {
    assertTrue(withRowFloat(COLUMN_FLOAT, FLOAT_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowFloat_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowFloat(is(COLUMN_FLOAT), FLOAT_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowFloat_ValueDoesNotMatch() {
    assertFalse(withRowFloat(COLUMN_FLOAT, -1f).matches(cursor));
  }

  @Test
  public void withRowDouble_columnIndexAndValue() {
    assertTrue(withRowDouble(5, DOUBLE_VALUE).matches(cursor));
  }

  @Test
  public void withRowDouble_columnIndexAndValueMatcher() {
    assertTrue(withRowDouble(5, DOUBLE_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowDouble_columnNameAndValue() {
    assertTrue(withRowDouble(COLUMN_DOUBLE, DOUBLE_VALUE).matches(cursor));
  }

  @Test
  public void withRowDouble_columnNameAndValueMatcher() {
    assertTrue(withRowDouble(COLUMN_DOUBLE, DOUBLE_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowDouble_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowDouble(is(COLUMN_DOUBLE), DOUBLE_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowDouble_ValueDoesNotMatch() {
    assertFalse(withRowDouble(COLUMN_DOUBLE, -1d).matches(cursor));
  }

  @Test
  public void withRowString_columnIndexAndValue() {
    assertTrue(withRowString(6, STRING_VALUE).matches(cursor));
  }

  @Test
  public void withRowString_columnIndexAndValueMatcher() {
    assertTrue(withRowString(6, STRING_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowString_columnNameAndValue() {
    assertTrue(withRowString(COLUMN_STR, STRING_VALUE).matches(cursor));
  }

  @Test
  public void withRowString_columnNameAndValueMatcher() {
    assertTrue(withRowString(COLUMN_STR, STRING_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowString_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowString(is(COLUMN_STR), STRING_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowString_ValueDoesNotMatch() {
    assertFalse(withRowString(COLUMN_STR, "does not match").matches(cursor));
  }

  @Test
  public void withRowBlob_columnIndexAndValue() {
    assertTrue(withRowBlob(7, BLOB_VALUE).matches(cursor));
  }

  @Test
  public void withRowBlob_columnIndexAndValueMatcher() {
    assertTrue(withRowBlob(7, BLOB_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowBlob_columnNameAndValue() {
    assertTrue(withRowBlob(COLUMN_BLOB, BLOB_VALUE).matches(cursor));
  }

  @Test
  public void withRowBlob_columnNameAndValueMatcher() {
    assertTrue(withRowBlob(COLUMN_BLOB, BLOB_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowBlob_columnNameMatcherAndValueMatcher() {
    assertTrue(withRowBlob(is(COLUMN_BLOB), BLOB_VALUE_MATCHER).matches(cursor));
  }

  @Test
  public void withRowBlob_ValueDoesNotMatch() {
    assertFalse(withRowBlob(COLUMN_BLOB, "does not match".getBytes()).matches(cursor));
  }

  @Test
  public void wrongNegativeColumnIndex() {
    expectedException.expect(IllegalArgumentException.class);
    withRowInt(-1, INTEGER_VALUE_MATCHER).matches(cursor);
  }

  @Test
  public void mergeCursor() {
    Cursor c1 = makeCursor(new String[] {"one", "two"}, new Object[] {1, 2});
    Cursor c2 = makeCursor(new String[] {"three"}, new Object[] {3});
    MergeCursor mergeCursor = new MergeCursor(new Cursor[] {c1, c2});
    mergeCursor.moveToFirst();
    try {
      withRowInt("three", 3).matches(mergeCursor);
      fail("expected previous line to throw an exception.");
    } catch (IllegalArgumentException expected) {
    }
    // override the default behavior and now it should just return false
    assertFalse(withRowInt("three", 3).withStrictColumnChecks(false).matches(mergeCursor));
    mergeCursor.moveToLast();
    assertTrue(withRowInt("three", 3).matches(mergeCursor));
    try {
      withRowInt(3, 3).matches(mergeCursor);
      fail("expected previous line to throw an exception.");
    } catch (IllegalArgumentException expected) {
    }
    // Trying to match on an out of bounds column with checks off shouldn't throw an exception
    assertFalse(withRowInt(3, 3).withStrictColumnChecks(false).matches(mergeCursor));
  }

  @After
  public void tearDown() throws Exception {
    cursor.close();
  }

  /** Returns an {@link MatrixCursor} populated with fake data. */
  private static Cursor makeCursor(String[] columnNames, Object[] values) {
    assertTrue(columnNames.length == values.length);
    MatrixCursor cursorStub;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      cursorStub = newMatrixCursorV10(columnNames);
    } else {
      cursorStub = newMatrixCursorV14(columnNames);
    }
    cursorStub.addRow(values);
    return cursorStub;
  }

  private static MatrixCursor newMatrixCursorV10(String[] columnNames) {
    return new MatrixCursorCompat(columnNames);
  }

  private static MatrixCursor newMatrixCursorV14(String[] columnNames) {
    return new MatrixCursor(columnNames);
  }

  /**
   * This test uses {@link MatrixCursor} as data source. {@link MatrixCursor#getBlob(int)} support
   * was added in ICS. This class enabled getBlob() for API levels < 14.
   */
  private static class MatrixCursorCompat extends MatrixCursor {

    private static final String TAG = "MatrixCursorCompat";

    public MatrixCursorCompat(String[] columnNames) {
      super(columnNames);
    }

    @Override
    public byte[] getBlob(int columnIndex) {
      Object value = get(columnIndex);
      return (byte[]) value;
    }

    /**
     * {@link MatrixCursor} internally uses get(int) to read data from its internal data structure
     * (Object[]). As this method is private we need to use reflection in order to invoke it.
     */
    private byte[] get(int columnIndex) {
      final String instanceMethod = "get";
      byte[] blob = new byte[0];
      try {
        Method getInstanceMethod =
            this.getClass().getSuperclass().getDeclaredMethod(instanceMethod, Integer.TYPE);
        getInstanceMethod.setAccessible(true);
        blob = (byte[]) getInstanceMethod.invoke(this, columnIndex);
      } catch (NoSuchMethodException nsme) {
        Log.e(TAG, String.format("could not find method: %s", instanceMethod), nsme);
      } catch (IllegalAccessException iae) {
        Log.e(
            TAG,
            String.format(
                "reflective setup failed using obj: %s method: %s",
                getClass().getSimpleName(), instanceMethod),
            iae);
      } catch (IllegalArgumentException iae) {
        Log.e(
            TAG,
            String.format(
                "reflective setup failed using obj: %s method: %s",
                getClass().getSimpleName(), instanceMethod),
            iae);
      } catch (InvocationTargetException ite) {
        Log.e(
            TAG,
            String.format(
                "reflective setup failed using obj: %s method: %s",
                getClass().getSimpleName(), instanceMethod),
            ite);
      }
      return blob;
    }
  }
}
