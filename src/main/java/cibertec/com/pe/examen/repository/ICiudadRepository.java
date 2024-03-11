package cibertec.com.pe.examen.repository;

import cibertec.com.pe.examen.model.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICiudadRepository extends JpaRepository<Ciudad, String> {

}
