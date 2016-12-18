/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cybernostics.jsp2thymeleaf;

import com.cybernostics.jsp2thymeleaf.api.elements.TagConverterSource;
import com.cybernostics.jsp2thymeleaf.api.expressions.FunctionConverterSource;
import com.cybernostics.jsp2thymeleaf.converters.AllJstlConverters;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Maintains a pool of available converters for taglibs based on the URI. In
 * some cases different or custom URIs are used to
 *
 * @author jason
 */
public class AvailableConverters
{

    private static Map<String, TagConverterSource> availableTagConverters = new HashMap<>();
    private static Map<String, FunctionConverterSource> availableExpressionConverters = new HashMap<>();

    public static void scanForConverters()
    {
        String taglibPaths = System.getProperty("taglibs", "");
        taglibPaths = taglibPaths + ":" + AllJstlConverters.class.getPackage().getName();

        List<String> paths = Arrays.stream(taglibPaths.split(":")).filter(it -> !it.isEmpty()).collect(Collectors.toList());
        for (String path : paths)
        {

            ScanResult scanResult = new FastClasspathScanner(path).scan();

            List<String> tagConverterNames
                    = scanResult.getNamesOfAllClasses();

            tagConverterNames
                    .stream()
                    .forEach((converter -> loadAndRegister(converter)));

        }
    }

    public static void addConverter(TagConverterSource converterSource)
    {
        availableTagConverters.put(converterSource.getTaglibURI(), converterSource);
    }

    private static void addConverter(FunctionConverterSource converterSource)
    {
        availableExpressionConverters.put(converterSource.getTaglibURI(), converterSource);
    }

    public static Optional<TagConverterSource> elementConverterforUri(String uri)
    {
        return Optional.ofNullable(availableTagConverters.getOrDefault(uri, null));
    }

    public static Optional<FunctionConverterSource> functionConverterforUri(String uri)
    {
        return Optional.ofNullable(availableExpressionConverters.getOrDefault(uri, null));
    }

    public static void addUriAlias(String existingUri, String aliasUri)
    {
        if (!availableTagConverters.containsKey(existingUri))
        {
            if (availableExpressionConverters.containsKey(existingUri))
            {
                throw new IllegalArgumentException("Unknown URI:" + existingUri);
            } else
            {
                availableExpressionConverters.put(aliasUri, availableExpressionConverters.get(existingUri));
            }
        } else
        {
            availableTagConverters.put(aliasUri, availableTagConverters.get(existingUri));
        }
    }

    private static void loadAndRegister(String className)
    {
        try
        {
            final Class<?> clazz = Class.forName(className);
            if (TagConverterSource.class.isAssignableFrom(clazz) && !Modifier.isAbstract(clazz.getModifiers()))
            {
                TagConverterSource converter = (TagConverterSource) clazz.newInstance();
                addConverter(converter);
            }
            if (FunctionConverterSource.class.isAssignableFrom(clazz))
            {
                FunctionConverterSource converterSource = (FunctionConverterSource) clazz.newInstance();
                addConverter(converterSource);

            }
        } catch (Throwable ex)
        {
            Logger.getLogger(JSP2Thymeleaf.class.getName()).log(Level.INFO, null, ex.getMessage());
        }

    }

}