package com.backend.repositorios;

import com.backend.entidades.Artista;
import com.backend.entidades.Biografia;
import com.backend.entidades.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Optional;

@Repository
public interface PostRepositorio extends JpaRepository<Post, Long> {

    Optional<ArrayList<Post>> findAllByBiografia(Biografia biografia);


}
