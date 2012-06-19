package org.chorusbdd.chorus.core.interpreter.scanner.filter;

/**
 * Created by IntelliJ IDEA.
 * User: Nick Ebbutt
 * Date: 19/06/12
 * Time: 08:18
 *
 * Construct filters for handler class search, current rules are:
 *
 * Process for each class found on the classpath until that class is 'denied' by a rule
 *
 * 1. Deny chorus packages by package prefix, apart from special Chorus handlers package
 * 2. If the user specified package prefixes, Deny any non-chorus packages not matching those specified
 * 3. Deny all classes except those with @Handler annotation
 */
public class HandlerClassFilterFactory {

    /**
     * @return a ClassFilter chain to use when discovering Handler classes in the classpath
     *
     * @param userSpecifiedHandlerPackagePrefixes, any handler package prefixes specified by user
     */
    public ClassFilter createClassFilters(String[] userSpecifiedHandlerPackagePrefixes) {

        ClassFilter handlerAnnotationFilter = new HandlerAnnotationFilter();

        //if user has specified package prefixes, restrict to those
        ClassFilter packagePrefixFilter = new PackagePrefixFilter(
            handlerAnnotationFilter,
            userSpecifiedHandlerPackagePrefixes.length == 0 ?
                    PackagePrefixFilter.ANY_PACKAGE : userSpecifiedHandlerPackagePrefixes
        );

         //always permit built in handlers, deny other chorus packages
        ClassFilter builtInHandlerClassFilter = new BuiltInHandlerClassFilter(packagePrefixFilter);
        return builtInHandlerClassFilter;
    }

}
