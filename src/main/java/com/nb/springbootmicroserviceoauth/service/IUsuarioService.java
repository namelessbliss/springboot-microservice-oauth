package com.nb.springbootmicroserviceoauth.service;

import com.nb.springbootmicroserviceusuarioscommons.models.entity.Usuario;

public interface IUsuarioService {

    public Usuario findByUsername(String username);

    public Usuario update(Usuario usuario, Long id);
}
