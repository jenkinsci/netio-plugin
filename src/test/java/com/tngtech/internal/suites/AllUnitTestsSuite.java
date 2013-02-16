package com.tngtech.internal.suites;


import com.tngtech.internal.helpers.IntegrationTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Categories.ExcludeCategory(IntegrationTest.class)
@Suite.SuiteClasses({AllTestsSuite.class})
public class AllUnitTestsSuite {
}