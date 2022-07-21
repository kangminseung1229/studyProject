package com.study.inf.modules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;


@SpringBootTest
public class ArchUnitTestCode {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";

    @Test
    @DisplayName("아키텍처 테스트")
    public void arch() {

        JavaClasses javaClasses = new ClassFileImporter().importPackages("com.study.inf");

        ArchRule studyPackageRule = classes().that().resideInAPackage("..modules.study..")
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage(STUDY, EVENT);

        ArchRule eventPackageRule = classes().that().resideInAPackage(EVENT)
                .should().accessClassesThat().resideInAnyPackage(STUDY, ACCOUNT, EVENT);

        ArchRule accountPackageRule = classes().that().resideInAPackage(ACCOUNT)
                .should().accessClassesThat().resideInAnyPackage(TAG, ZONE, ACCOUNT);

        ArchRule cycleCheck = slices().matching("com.study.inf.modules.(*)..")
                .should().beFreeOfCycles();

        studyPackageRule.check(javaClasses);

    }

}