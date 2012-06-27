package com.cloudmine.coderunner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SnippetEntryPoint {
	String snippetName() default "";
}
