package org.wso2.carbon.eventprocessing.executiongenerator.internal.processing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.RegistryType;
import org.wso2.carbon.eventprocessing.executiongenerator.DomainConfigInfoDTO;
import org.wso2.carbon.eventprocessing.executiongenerator.ExecutionGeneratorAdminService;
import org.wso2.carbon.eventprocessing.executiongenerator.internal.util.ExecutionGeneratorConstants;
import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;

/*
 class that access wso2 carbon registry to save, delete and get content there
 */
public class ConfigInformationCollector {

    private Registry registry;
    public static ConfigInformationCollector instance =null;
    final Log log = LogFactory.getLog(ExecutionGeneratorAdminService.class);
    /**
     * constructor
     */
    private ConfigInformationCollector() {

        CarbonContext cCtx = CarbonContext.getCurrentContext();
        registry = (Registry) cCtx.getRegistry(RegistryType.SYSTEM_CONFIGURATION);
        String registryType = RegistryType.SYSTEM_CONFIGURATION.toString();
        if (registryType != null) {
            registry = (Registry) cCtx.getRegistry(RegistryType.valueOf(registryType));
        }
    }

    /**
     * save template configuration xml files in wso2 carbon registry
     *
     * @param fileName    template configuration name
     * @param fileContent xml content
     * @param description template configuration description
     * @param type        template configuration type
     */
    public void saveTemplateConfig(String fileName, String fileContent, String description, String type) {

        try {
            Resource resource = registry.newResource();
            resource.setContent(fileContent);
            resource.setProperty("name", fileName);
            resource.setProperty("description", description);
            resource.setProperty("type", type);
            String resourcePath = ExecutionGeneratorConstants.TEMPLATE_CONFIG_PATH + "/" + fileName + ".xml";
            registry.put(resourcePath, resource);
        } catch (RegistryException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * get template configuration content when the configuration name is given
     *
     * @param configName template configuration name
     * @return template configuration xml content as a string value
     */
    public String getTemplateConfig(String configName) {

        Resource config_file;
        String configContent = "";
        try {
            config_file = registry.get(ExecutionGeneratorConstants.TEMPLATE_CONFIG_PATH + "/" + configName + ".xml");
            configContent = new String((byte[]) config_file.getContent());

        } catch (org.wso2.carbon.registry.core.exceptions.RegistryException e) {
            log.error(e.getMessage(), e);
        }
        return configContent;
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
           log.error("Exception at getting all template Configuration: "+e);
        }
        return domainConfigInfo;
    }

    /**
     * delete template configuration when the name of configuration is given
     *
     * @param configName template configuration name
     */
    public void deleteTemplateConfig(String configName){

        try {
            registry.delete(ExecutionGeneratorConstants.TEMPLATE_CONFIG_PATH + "/" + configName + ".xml");
        } catch (Exception e) {
           log.error("Exception at deleting template configuration: "+e);
        }
    }

    public static ConfigInformationCollector getInstance(){
        if(instance == null){
            instance=new ConfigInformationCollector();
        }
        return instance;
    }
}
