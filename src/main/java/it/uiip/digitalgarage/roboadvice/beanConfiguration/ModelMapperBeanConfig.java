package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of the {@link ModelMapper} bean.
 */
@Configuration
public class ModelMapperBeanConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // For the mapping from the User to the UserDTO I do not set the password in the UserDTO
        PropertyMap<User, UserDTO> userToUserDTO = new PropertyMap<User, UserDTO>() {
            protected void configure() {
                map().setPassword(null);
            }
        };
        modelMapper.addMappings(userToUserDTO);

        return modelMapper;
    }
}
