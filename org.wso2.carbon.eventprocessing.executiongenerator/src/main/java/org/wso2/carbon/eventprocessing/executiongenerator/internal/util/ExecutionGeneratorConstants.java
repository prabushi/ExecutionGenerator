package org.wso2.carbon.eventprocessing.executiongenerator.internal.util;

import org.wso2.carbon.eventprocessing.executiongenerator.internal.processing.ConfigInformationCollector;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.processing.DomainInformationCollector;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.processing.GenerateExecutionPlan;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.processing.ReadTemplateStructures;
import org.wso2.carbon.utils.CarbonUtils;

import java.io.File;

/**
 * class that specifies path information
 */
public class ExecutionGeneratorConstants {

    static String fileSeparator = File.separator;

    public static String carbon_home = CarbonUtils.getCarbonHome();

    public static final String TEMPLATE_DOMAIN_PATH = carbon_home + fileSeparator +"repository"+ fileSeparator +"conf"+ fileSeparator +"cep"+ fileSeparator +"TemplateDomain";

    public static final String TEMPLATE_CONFIG_PATH = fileSeparator +"repository"+ fileSeparator +"components"+ fileSeparator +"org.wso2.carbon.execution_generator"+ fileSeparator +"TemplateConfig";

    public static final DomainInformationCollector domainInformationCollector=DomainInformationCollector.getInstance();

    public static final ConfigInformationCollector configInformationCollector = ConfigInformationCollector.getInstance();

    public static final ReadTemplateStructures readTemplateStructures = ReadTemplateStructures.getInstance();

    public static final GenerateExecutionPlan GENERATE_EXECUTION_PLAN = GenerateExecutionPlan.getInstance();
}
