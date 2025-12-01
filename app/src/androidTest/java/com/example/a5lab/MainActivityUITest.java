package com.example.a5lab;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testAllMainViewsAreVisible() {
        onView(withId(R.id.etFilter))
                .check(matches(isDisplayed()));

        onView(withId(R.id.emptyView))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        onView(withId(R.id.lvCurrencies))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }


    @Test
    public void testTypingFilterTextShowsEmptyViewInitially() {
        onView(withId(R.id.etFilter))
                .perform(typeText("usd"), closeSoftKeyboard());
        onView(isRoot()).perform(waitFor(1500));
        onView(withId(R.id.emptyView)).check(matches(isDisplayed()));
    }

    @Test
    public void testToggleListVisibilityProgrammatically() {
        rule.getActivity().runOnUiThread(() -> {
            rule.getActivity().findViewById(R.id.lvCurrencies)
                    .setVisibility(android.view.View.VISIBLE);
        });
        onView(isRoot()).perform(waitFor(500));
        onView(withId(R.id.lvCurrencies)).check(matches(isDisplayed()));
    }

    @Test
    public void testListViewBecomesVisibleAfterDataLoad() {
        onView(isRoot()).perform(waitFor(4000));
        onView(withId(R.id.lvCurrencies)).check(matches(isDisplayed()));
    }

    public static ViewAction waitFor(long millis) {
        return new ViewAction() {
            @Override
            public Matcher<android.view.View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, android.view.View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }
}
