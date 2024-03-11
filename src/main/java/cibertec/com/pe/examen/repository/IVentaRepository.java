package cibertec.com.pe.examen.repository;

import cibertec.com.pe.examen.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVentaRepository extends JpaRepository<Venta, Integer> {
}
