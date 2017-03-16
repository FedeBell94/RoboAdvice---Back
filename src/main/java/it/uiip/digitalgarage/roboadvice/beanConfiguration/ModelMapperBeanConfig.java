package it.uiip.digitalgarage.roboadvice.beanConfiguration;

import it.uiip.digitalgarage.roboadvice.businesslogic.model.dto.UserDTO;
import it.uiip.digitalgarage.roboadvice.persistence.model.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperBeanConfig {

    @Bean
    public ModelMapper modelMapper(){
        ModelMapper modelMapper = new ModelMapper();

        PropertyMap<User, UserDTO> userToUserDTO = new PropertyMap<User, UserDTO>() {
            protected void configure() {
                map().setPassword(null);
            }
        };
        modelMapper.addMappings(userToUserDTO);

        return modelMapper;
    }
}
