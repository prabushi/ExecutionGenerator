package org.wso2.carbon.eventprocessing.executiongenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.util.ExecutionGeneratorConstants;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

/*
 class that access wso2 carbon registry to save, delete and get content there
 */
public class ExecutionGenerator {

    private Registry registry;

    /**
     * constructor
     */
    public ExecutionGenerator() {

        CarbonContext cCtx = CarbonContext.getCurrentContext();
        registry = (Registry) cCtx.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
        String registryType = RegistryType.SYSTEM_CONFIGURATION.toString();
        if (registryType != null) {
            registry = (Registry) cCtx.getRegistry(RegistryType.valueOf(registryType));
        }
    }


    /**
     * get all template configurations' details as a DomainConfigInfoDTO objects array
     *
     * @return DomainConfigInfoDTO objects array
     */
    public DomainConfigInfoDTO[] getAllTemplateConfig() {
        DomainConfigInfoDTO[] domainConfigInfo = null;

        try {

            Resource resp = registry.get(ExecutionGeneratorConstants.TEMPLATE_CONFIG_PATH);
            org.wso2.carbon.registry.api.Collection collection = (org.wso2.carbon.registry.api.Collection) resp;
            String[] resourcePathList = collection.getChildren();
            domainConfigInfo = new DomainConfigInfoDTO[resourcePathList.length];
            Resource configFile;
            for (int i = 0; i < resourcePathList.length; i++) {
                configFile = registry.get(resourcePathList[i]);

                DomainConfigInfoDTO domainConfigObj = new DomainConfigInfoDTO(configFile.getProperty("name"), configFile.getProperty("type"), configFile.getProperty("description"));
                domainConfigInfo[i] = domainConfigObj;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return domainConfigInfo;
    }

    /**
     * delete template configuration when the name of configuration is given
     *
     * @param configName template configuration name
     */
    public void deleteTemplateConfig(String configName) {

        try {
            registry.delete(ExecutionGeneratorConstants.TEMPLATE_CONFIG_PATH + "/" + configName + ".xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
