package com.nb.springbootmicroserviceoauth.security;

import com.nb.springbootmicroserviceoauth.service.IUsuarioService;
import com.nb.springbootmicroserviceusuarioscommons.models.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InfoAdicionalToken implements TokenEnhancer {

    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> info = new HashMap<String, Object>();

        Usuario usuario = usuarioService.findByUsername(oAuth2Authentication.getName());
        info.put("nombre", usuario.getNombre());
        info.put("apellido", usuario.getApellidos());
        info.put("email", usuario.getEmail());

        ((DefaultOAuth2AccessToken) oAuth2AccessToken).setAdditionalInformation(info);

        return oAuth2AccessToken;
    }
}
