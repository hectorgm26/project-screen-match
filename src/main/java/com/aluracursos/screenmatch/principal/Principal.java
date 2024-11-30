package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&INSERTAR_TU_API_KEY_AQUI";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series; // VARIABLE GLOBAL PARA UTILIZARSE EN CUALQUIER PARTE DEL PROGRAMA
    Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulos
                    5 - Top 5 series mejor valoradas
                    6 - Buscar series por genero/categoria
                    7 - Filtrar series por número máximo de temporadas y evaluación mínima
                    8 - Buscar episodios por su titulo
                    9 - TOP 5 episodios mejor valorados por serie
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {

        mostrarSeriesBuscadas(); // Le mostramos al usuario las series que ha buscado, para que elija una de ellas
        System.out.println("Escribe el nombre de la serie de la que deseas buscar episodios");
        String nombreSerie = teclado.nextLine();

        // USAREMOS UN OPTIONAL PARA LA BUSQUEDA, PORQUE PUEDE QUE NO EXISTA LA SERIE QUE SE BUSCA
        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase())) // BUSCAMOS LA SERIE QUE COINCIDA CON EL NOMBRE QUE HA ESCRITO EL USUARIO, CON EL FIN DE SOLO MOSTRAR LOS EPISODIOS DE ESA SERIE
                .findFirst();

        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();

            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);

                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }

            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream(). // flatmap sirve para aplanar la lista de episodios, entiendiendose por aplanar, que se convierte en una lista de episodios
                            map(e -> new Episodio(d.numero(), e))) // map sirve para transformar un objeto en otro, en este caso, transformamos un objeto de tipo DatosEpisodio en un objeto de tipo Episodio
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);

        }


    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        repositorio.save(serie);

        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {

        System.out.println("Escribe el nombre de la serie que deseas buscar");
        String nombreSerie = teclado.nextLine();

        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es :" + serieBuscada.get());
        } else {
            System.out.println("No se ha encontrado la serie buscada");
        }

    }

    private void buscarTop5Series() {

        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();

        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + ", Evaluación: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escriba el genero/categoria de la serie que desea buscar");
        String genero = teclado.nextLine();

        Categoria categoria = Categoria.fromEspanol(genero);

        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero + " son: ");
        seriesPorCategoria.forEach(System.out::println);
    }

    private void filtrarSeriesPorTemporadaYEvaluacion() {

        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        int totalTemporadas = teclado.nextInt();
        teclado.nextLine();

        System.out.println("¿Com evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();

        // el lessThanEqual es para indicar que queremos que sea menor o igual al valor que hemos indicado,
        // y el greaterThanEqual es para indicar que queremos que sea mayor o igual al valor que hemos indicado
        // esto porque buscamos todas las series que tengan un número máximo de temporadas y una evaluación mínima
        List<Serie> filtroSeries = repositorio.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion); // SE CAMBIO EL METODO A UNA NATIVE QUERY
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

    private void buscarEpisodiosPorTitulo() {
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        String nombreEpisodio = teclado.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s, Titulo: %s, Temporada: %d, Episodio: %d, Evaluación: %.2f, Fecha de lanzamiento: %s %n",
                        e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion(), e.getFechaDeLanzamiento()));
    }

    private void buscarTop5Episodios() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s - Titulo: %s - Temporada: %s - Episodio: %d - Evaluación: %.2f -  Fecha de lanzamiento: %s %n",
                            e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion(), e.getFechaDeLanzamiento()));
        }


    }
}

