/*
 * Sonar CodeNarc Converter
 * Copyright (C) 2011 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.groovy.codenarc;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codenarc.rule.AbstractRule;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

public class Converter {

  private Set<Class> duplications = new HashSet<Class>();

  private Properties props = new Properties();
  private PrintStream out;
  private static int count;

  public Converter() throws Exception {
    out = new PrintStream(new File("/tmp/rules.xml"));
    String version = IOUtils.toString(Converter.class.getResourceAsStream("/codenarc-version.txt"));
    out.println("<!-- Generated using CodeNarc " + version + " -->");
    props.load(Converter.class.getResourceAsStream("/codenarc-base-messages.properties"));
  }

  private String priority(int priority) {
    switch (priority) {
      case 1:
        return "INFO";
      case 2:
        return "MINOR";
      case 3:
        return "MAJOR";
      default:
        throw new RuntimeException("Should never happen");
    }
  }

  private void rule(Class<? extends AbstractRule> ruleClass, String since) throws Exception {
    if (duplications.contains(ruleClass)) {
      System.out.println("Duplicated rule " + ruleClass.getName());
    } else {
      duplications.add(ruleClass);
    }
    AbstractRule rule = ruleClass.newInstance();
    String key = ruleClass.getCanonicalName();
    String configKey = StringUtils.removeEnd(ruleClass.getSimpleName(), "Rule");
    String name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(configKey), ' ');
    String priorityStr = priority(rule.getPriority());
    String description = props.getProperty(configKey + ".description.html");

    SortedSet<String> params = new TreeSet<String>();

    // extract params
    String[] params1 = StringUtils.substringsBetween(description, "${", "}");
    if (params1 != null) {
      for (String param : params1) {
        description = StringUtils.remove(description, " (${" + param + "})");
        param = StringUtils.removeStart(param, "rule.");
        params.add(param);
      }
    }

    String[] params2 = StringUtils.substringsBetween(description, "<em>", "</em> property");
    if (params2 != null) {
      params.addAll(Arrays.asList(params2));
    }

    // output
    if (since != null) {
      out.println("  <!-- since " + since + " -->");
    }
    out.println("  <rule key=\"" + key + "\"" + " priority=\"" + priorityStr + "\">");
    out.println("    <name><![CDATA[" + name + "]]></name>");
    out.println("    <configKey><![CDATA[" + configKey + "]]></configKey>");
    out.println("    <description><![CDATA[" + description + "]]></description>");

    if (params != null) {
      for (String param : params) {
        out.println("    <param key=\"" + param + "\"/>");
      }
    }

    out.println("  </rule>");
    out.println();
    count++;
  }

  private void startSet(String name) {
    out.println("  <!-- " + name + " rules -->");
    out.println();
  }

  private void start() {
    out.println("<rules>");
    out.println();
  }

  private void end() {
    out.println("</rules>");
    out.flush();
    out.close();
  }

  private static final String VERSION_0 = null;
  private static final String VERSION_0_11 = "0.11";
  private static final String VERSION_0_12 = "0.12";
  private static final String VERSION_0_13 = "0.13";
  private static final String VERSION_0_14 = "0.14";

  public static void main(String[] args) throws Exception {
    Converter converter = new Converter();
    converter.start();

    converter.startSet("basic");
    converter.rule(org.codenarc.rule.basic.AddEmptyStringRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.AssignmentInConditionalRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.BigDecimalInstantiationRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.BooleanGetBooleanRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.BooleanMethodReturnsNullRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.BrokenOddnessCheckRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.CloneableWithoutCloneRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.ConfusingTernaryRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.basic.CompareToWithoutComparableRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.basic.ConsecutiveLiteralAppendsRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.ConsecutiveStringConcatenationRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.ConstantIfExpressionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.ConstantTernaryExpressionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.DeadCodeRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.DoubleNegativeRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.DuplicateCaseStatementRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.EmptyCatchBlockRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyElseBlockRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyFinallyBlockRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyForStatementRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyIfStatementRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyInstanceInitializerRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.EmptyMethodRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.EmptyStaticInitializerRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.EmptySwitchStatementRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptySynchronizedStatementRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyTryBlockRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EmptyWhileStatementRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.EqualsAndHashCodeRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.ExplicitArrayListInstantiationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToAndMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToCompareToMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToDivMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToEqualsMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToGetAtMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToLeftShiftMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToMinusMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToMultiplyMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToModMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToOrMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToPlusMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToPowerMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToRightShiftMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitCallToXorMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitGarbageCollectionRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.basic.ExplicitHashMapInstantiationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitHashSetInstantiationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitLinkedListInstantiationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitStackInstantiationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ExplicitTreeSetInstantiationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.GroovyLangImmutableRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.GStringAsMapKeyRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.IntegerGetIntegerRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.InvertedIfElseRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.RemoveAllOnSelfRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ReturnFromFinallyBlockRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.basic.ReturnsNullInsteadOfEmptyArrayRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.basic.ReturnsNullInsteadOfEmptyCollectionRule.class, VERSION_0_11);
    // removed in 0.14
    // converter.rule(org.codenarc.rule.basic.SerialVersionUIDRule.class, VERSION_0_11);
    // converter.rule(org.codenarc.rule.basic.SerializableClassMustDefineSerialVersionUIDRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.basic.SimpleDateFormatMissingLocaleRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.basic.ThrowExceptionFromFinallyBlockRule.class, VERSION_0);

    converter.rule(org.codenarc.rule.basic.ExplicitLinkedHashMapInstantiationRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.DuplicateMapKeyRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.DuplicateSetValueRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.EqualsOverloadedRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.ForLoopShouldBeWhileLoopRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.ClosureAsLastMethodParameterRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.ClassForNameRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.ComparisonOfTwoConstantsRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.basic.ComparisonWithSelfRule.class, VERSION_0_14);

    // new ruleset in 0.14 - serialization
    converter.startSet("serialization");
    converter.rule(org.codenarc.rule.serialization.SerialVersionUIDRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.serialization.SerializableClassMustDefineSerialVersionUIDRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.serialization.SerialPersistentFieldsRule.class, VERSION_0_14);

    converter.startSet("braces");
    converter.rule(org.codenarc.rule.braces.IfStatementBracesRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.braces.ElseBlockBracesRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.braces.ForStatementBracesRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.braces.WhileStatementBracesRule.class, VERSION_0);

    converter.startSet("concurrency");
    converter.rule(org.codenarc.rule.concurrency.BusyWaitRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.DoubleCheckedLockingRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.InconsistentPropertyLockingRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.InconsistentPropertySynchronizationRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.NestedSynchronizationRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.StaticCalendarFieldRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.StaticDateFormatFieldRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.StaticMatcherFieldRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedMethodRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedOnGetClassRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedOnBoxedPrimitiveRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedOnStringRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedOnThisRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedReadObjectMethodRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.SynchronizedOnReentrantLockRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.SystemRunFinalizersOnExitRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.ThreadGroupRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.ThreadLocalNotStaticFinalRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.ThreadYieldRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.UseOfNotifyMethodRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.concurrency.VolatileArrayFieldRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.concurrency.VolatileLongOrDoubleFieldRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.concurrency.WaitOutsideOfWhileLoopRule.class, VERSION_0_13);

    converter.rule(org.codenarc.rule.concurrency.StaticConnectionRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.concurrency.StaticSimpleDateFormatFieldRule.class, VERSION_0_14);

    converter.startSet("design");
    converter.rule(org.codenarc.rule.design.AbstractClassWithoutAbstractMethodRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.design.CloseWithoutCloseableRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.design.ConstantsOnlyInterfaceRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.design.EmptyMethodInAbstractClassRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.design.ImplementationAsTypeRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.design.FinalClassWithProtectedMemberRule.class, VERSION_0_12);

    converter.rule(org.codenarc.rule.design.PublicInstanceFieldRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.design.StatelessSingletonRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.design.AbstractClassWithPublicConstructorRule.class, VERSION_0_14);

    converter.startSet("dry");
    converter.rule(org.codenarc.rule.dry.DuplicateNumberLiteralRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.dry.DuplicateStringLiteralRule.class, VERSION_0_11);

    converter.startSet("exceptions");
    converter.rule(org.codenarc.rule.exceptions.CatchArrayIndexOutOfBoundsExceptionRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.exceptions.CatchErrorRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.CatchExceptionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.CatchIllegalMonitorStateExceptionRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.exceptions.CatchIndexOutOfBoundsExceptionRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.exceptions.CatchNullPointerExceptionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.CatchRuntimeExceptionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.CatchThrowableRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.ConfusingClassNamedExceptionRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.exceptions.ExceptionExtendsErrorRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.exceptions.MissingNewInThrowStatementRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.exceptions.ReturnNullFromCatchBlockRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.exceptions.ThrowErrorRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.ThrowExceptionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.ThrowNullPointerExceptionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.ThrowRuntimeExceptionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.ThrowThrowableRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.exceptions.SwallowThreadDeathRule.class, VERSION_0_14);

    converter.startSet("generic");
    converter.rule(org.codenarc.rule.generic.IllegalRegexRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.generic.RequiredRegexRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.generic.RequiredStringRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.generic.StatelessClassRule.class, VERSION_0);

    converter.rule(org.codenarc.rule.generic.IllegalPackageReferenceRule.class, VERSION_0_14);

    converter.startSet("grails");
    converter.rule(org.codenarc.rule.grails.GrailsPublicControllerMethodRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.grails.GrailsSessionReferenceRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.grails.GrailsServletContextReferenceRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.grails.GrailsStatelessServiceRule.class, VERSION_0);

    converter.startSet("imports");
    converter.rule(org.codenarc.rule.imports.DuplicateImportRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.imports.ImportFromSamePackageRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.imports.UnnecessaryGroovyImportRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.imports.UnusedImportRule.class, VERSION_0);

    converter.rule(org.codenarc.rule.imports.ImportFromSunPackagesRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.imports.MisorderedStaticImportsRule.class, VERSION_0_14);

    converter.startSet("junit");
    converter.rule(org.codenarc.rule.junit.ChainedTestRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.junit.CoupledTestCaseRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.junit.JUnitAssertAlwaysFailsRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.JUnitAssertAlwaysSucceedsRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.JUnitPublicNonTestMethodRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.JUnitSetUpCallsSuperRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.JUnitStyleAssertionsRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.JUnitTearDownCallsSuperRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.JUnitUnnecessarySetUpRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.JUnitUnnecessaryTearDownRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.junit.UseAssertEqualsInsteadOfAssertTrueRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.UseAssertFalseInsteadOfNegationRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.UseAssertTrueInsteadOfAssertEqualsRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.UseAssertTrueInsteadOfNegationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.junit.UseAssertNullInsteadOfAssertEqualsRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.UseAssertSameInsteadOfAssertTrueRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.JUnitFailWithoutMessageRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.junit.JUnitTestMethodWithoutAssertRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.junit.UnnecessaryFailRule.class, VERSION_0_13);

    converter.rule(org.codenarc.rule.junit.SpockIgnoreRestUsedRule.class, VERSION_0_14);

    converter.startSet("logging");
    converter.rule(org.codenarc.rule.logging.LoggerForDifferentClassRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.logging.LoggingSwallowsStacktraceRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.logging.LoggerWithWrongModifiersRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.logging.MultipleLoggersRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.logging.PrintlnRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.logging.PrintStackTraceRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.logging.SystemErrPrintRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.logging.SystemOutPrintRule.class, VERSION_0);

    converter.startSet("naming");
    converter.rule(org.codenarc.rule.naming.AbstractClassNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.ClassNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.ConfusingMethodNameRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.naming.FieldNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.InterfaceNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.MethodNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.ObjectOverrideMisspelledMethodNameRule.class, VERSION_0_11);
    converter.rule(org.codenarc.rule.naming.PackageNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.ParameterNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.PropertyNameRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.naming.VariableNameRule.class, VERSION_0);

    converter.startSet("size");
    converter.rule(org.codenarc.rule.size.AbcComplexityRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.size.ClassSizeRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.size.CyclomaticComplexityRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.size.MethodCountRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.size.MethodSizeRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.size.NestedBlockDepthRule.class, VERSION_0);

    converter.startSet("unnecessary");
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryBigDecimalInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryBigIntegerInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryBooleanExpressionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryBooleanInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryCallForLastElementRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryCallToSubstringRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryCatchBlockRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryCollectCallRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryCollectionCallRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryConstructorRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryDefInMethodDeclarationRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryDoubleInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryFloatInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryGetterRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryGStringRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryIfStatementRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryInstantiationToGetClassRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryIntegerInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryLongInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryModOneRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryObjectReferencesRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryNullCheckRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryNullCheckBeforeInstanceOfRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryOverridingMethodRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryPublicModifierRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryReturnKeywordRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessarySelfAssignmentRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessarySemicolonRule.class, VERSION_0_13);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryStringInstantiationRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryTernaryExpressionRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryTransientModifierRule.class, VERSION_0_13);

    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryFinalOnPrivateMethodRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryElseStatementRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryParenthesesForMethodCallWithClosureRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.unnecessary.UnnecessaryPackageReferenceRule.class, VERSION_0_14);

    converter.startSet("unused");
    converter.rule(org.codenarc.rule.unused.UnusedArrayRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unused.UnusedObjectRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unused.UnusedPrivateFieldRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unused.UnusedPrivateMethodParameterRule.class, VERSION_0_12);
    converter.rule(org.codenarc.rule.unused.UnusedPrivateMethodRule.class, VERSION_0);
    converter.rule(org.codenarc.rule.unused.UnusedVariableRule.class, VERSION_0);

    // new ruleset in 0.14 - jdbc
    converter.startSet("jdbc");
    converter.rule(org.codenarc.rule.jdbc.DirectConnectionManagementRule.class, VERSION_0_14);

    // new ruleset in 0.14 - security
    converter.startSet("security");
    converter.rule(org.codenarc.rule.security.NonFinalSubclassOfSensitiveInterfaceRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.InsecureRandomRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.FileCreateTempFileRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.SystemExitRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.ObjectFinalizeRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.JavaIoPackageAccessRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.UnsafeArrayDeclarationRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.PublicFinalizeMethodRule.class, VERSION_0_14);
    converter.rule(org.codenarc.rule.security.NonFinalPublicFieldRule.class, VERSION_0_14);

    converter.end();

    System.out.println(count + " rules processed");
  }
}
