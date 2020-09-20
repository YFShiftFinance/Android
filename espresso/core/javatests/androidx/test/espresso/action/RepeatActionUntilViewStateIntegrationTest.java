/*
 * Copyright (C) 2016 The Android Open Source Project
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

package androidx.test.espresso.action;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.repeatedlyUntil;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.rules.ExpectedException.none;

import androidx.test.espresso.PerformException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.ui.app.R;
import androidx.test.ui.app.SwipeActivity;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/** Integration tests for repeat action until view state. */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class RepeatActionUntilViewStateIntegrationTest {

  @Rule public ExpectedException expectedException = none();

  @Rule
  public ActivityScenarioRule<SwipeActivity> activityTestRule =
      new ActivityScenarioRule<>(SwipeActivity.class);

  @Test
  public void performingActionRepeatedlyOnViewBringsItToDesiredState() {
    onView(withId(R.id.vertical_pager))
        .check(matches(hasDescendant(withText("Position #0"))))
        .perform(repeatedlyUntil(swipeUp(), hasDescendant(withText("Position #2")), 10))
        .check(matches(hasDescendant(withText("Position #2"))));
  }

  @Test
  public void performingActionOnAlreadyAchievedViewStateHasNoEffect() {
    onView(withId(R.id.vertical_pager))
        .check(matches(hasDescendant(withText("Position #0"))))
        .perform(repeatedlyUntil(swipeUp(), hasDescendant(withText("Position #0")), 10))
        .check(matches(hasDescendant(withText("Position #0"))));
  }

  @Test
  public void performingActionOnViewWithUnreachableViewStateFailsAfterGivenNoOfAttempts() {
    final int maxAttempts = 2;
    expectedException.expect(
        new CustomTypeSafeMatcher<PerformException>(
            "PerformException " + "with expected cause and action description") {
          @Override
          protected boolean matchesSafely(PerformException performException) {

            return performException
                    .getCause()
                    .getMessage()
                    .equals("Failed to achieve view state " + "after " + maxAttempts + " attempts")
                && performException
                    .getActionDescription()
                    .equals("fast swipe until: has descendant: with text: is \"Position #200\"");
          }
        });
    onView(withId(R.id.vertical_pager))
        .check(matches(hasDescendant(withText("Position #0"))))
        .perform(repeatedlyUntil(swipeUp(), hasDescendant(withText("Position #200")), maxAttempts));
  }
}
