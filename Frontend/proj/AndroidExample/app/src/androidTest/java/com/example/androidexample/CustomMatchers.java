package com.example.androidexample;
import android.view.View;
import android.view.ViewGroup;
import org.hamcrest.TypeSafeMatcher;

public class CustomMatchers {
    public static TypeSafeMatcher<View> hasNoChildren() {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                return view instanceof ViewGroup && ((ViewGroup) view).getChildCount() == 0;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("has no children");
            }
        };
    }

    public static TypeSafeMatcher<View> withListSize(final int size) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                return view instanceof ViewGroup && ((ViewGroup) view).getChildCount() == size;
            }

            @Override
            public void describeTo(org.hamcrest.Description description) {
                description.appendText("with list size: " + size);
            }
        };
    }

}