package org.wso2.carbon.eventprocessing.executiongenerator.internal.util;

import org.wso2.carbon.utils.CarbonUtils;

/**
 * class that specifies path information
 */
public class ExecutionGeneratorConstants {

    public static String carbon_home = CarbonUtils.getCarbonHome();

    public static final String TEMPLATE_DOMAIN_PATH = carbon_home + "/repository/conf/cep/TemplateDomain";

    public static final String TEMPLATE_CONFIG_PATH = "/repository/components/org.wso2.carbon.execution_generator/TemplateConfig";

}
