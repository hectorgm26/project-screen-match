package com.aluracursos.screenmatch.repository;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {

    Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);

    List<Serie> findTop5ByOrderByEvaluacionDesc();

    List<Serie> findByGenero(Categoria categoria);

    // List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, Double evaluacion);
    // metodo para obtener todas las series que tengan un número máximo de temporadas y una evaluación mínima


    // ESTO ES UNA QUERY NATIVA DE SQL, QUE SE EJECUTA EN LA BASE DE DATOS, NO EN JAVA
    // ES FIJA, YA QUE MEDIANTE LA CONSULTA SE LE INDICA QUE QUEREMOS QUE NOS DEVUELVA TODAS LAS SERIES QUE TENGAN UN NÚMERO MÁXIMO DE TEMPORADAS Y UNA EVALUACIÓN MÍNIMA
    // YA NO IMPORTANDO QUE VALORES LE PASEMOS, SIEMPRE NOS DEVOLVERÁ LAS SERIES QUE CUMPLAN CON LAS CONDICIONES DE LA CONSULTA
    // @Query(value = "select * from series where total_temporadas <= ?1 and evaluacion >= ?2", nativeQuery = true)
    // List<Serie> seriesPorTemporadaYEvaluacion();

    // JPQL
    @Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.evaluacion >= :evaluacion")
    List<Serie> seriesPorTemporadaYEvaluacion(int totalTemporadas, Double evaluacion);

    // EXPLICACION DE ESTA QUERY PASO A PASO:
    // SELECT e FROM Serie s: seleccionamos todos los episodios de la tabla de serie
    // JOIN s.episodios e: unimos la tabla de episodios con la tabla de series
    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
    List<Episodio> episodiosPorNombre(String nombreEpisodio);

    @Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s = :serie ORDER BY e.evaluacion DESC LIMIT 5")
    List<Episodio> top5Episodios(Serie serie);

}
/*
LAS QUERYS NATIVAS TIENEN LA DESVENTAJA DE QUE SI SE QUIERE MIGRAR A OTRO MOTOR, PUEDEN NO SER COMPATIBLES
Y ES POR ESO QUE COMO ALTERANTIVA EXISTE JPQL, QUE ES UN LENGUAJE DE CONSULTA QUE SE PARECE A SQL, PERO QUE ES INDEPENDIENTE DEL MOTOR DE BASE DE DATOS, Y QUE SE PUEDE UTILIZAR EN JPA
QUE PERMITE TRABAJAR CON ENTIDADES Y NO CON TABLAS

Para trabajar con JPQL, se debe indicar en la consulta del select el cambio de * por s, que representara el alias de la entidad con la cual estamos trabajando
y no trabajamos con el nombre de la tabla, sino con el nombre de la entidad y clase
Y PARA QUE SE DISTINGA ENTRE EL ATRIBUTO DE LA CLASE Y EL VALOR PASADO POR CONSTRUCTOR, SE COLOCA : ANTES DEL NOMBRE DEL ATRIBUTO QUE SE QUIERE PASAR POR PARAMETRO
 */
