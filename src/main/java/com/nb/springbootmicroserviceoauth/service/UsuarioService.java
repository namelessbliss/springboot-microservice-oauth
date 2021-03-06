package com.nb.springbootmicroserviceoauth.service;

import brave.Tracer;
import com.nb.springbootmicroserviceoauth.clients.UsuarioFeignClient;
import com.nb.springbootmicroserviceusuarioscommons.models.entity.Usuario;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService, IUsuarioService {

    private Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioFeignClient client;

    @Autowired
    private Tracer tracer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            Usuario usuario = client.findByUsername(username);

            List<GrantedAuthority> authorities = usuario.getRoles()
                    .stream()
                    .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                    .peek(simpleGrantedAuthority -> log.info("Role: " + simpleGrantedAuthority.getAuthority()))
                    .collect(Collectors.toList());

            log.info("Usuario Autenticado: " + username);

            return new User(usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(),
                    true, true, true, authorities);
        } catch (FeignException e) {
            String mensajeError = "Error en el login, no existe  el usuario '" + username + "' en el sistema";
            log.error(mensajeError);

            tracer.currentSpan().tag("error.mensaje", mensajeError + ": " + e.getMessage());
            throw new UsernameNotFoundException(mensajeError);
        }
    }

    @Override
    public Usuario findByUsername(String username) {
        return client.findByUsername(username);
    }

    @Override
    public Usuario update(Usuario usuario, Long id) {
        return client.update(usuario, id);
    }
}
