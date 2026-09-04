package com.api.ecommerce.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.api.ecommerce.model.Categoria;
import com.api.ecommerce.model.Etiqueta;
import com.api.ecommerce.model.Producto;
import com.api.ecommerce.repository.CategoriaRepository;
import com.api.ecommerce.repository.EtiquetaRepository;
import com.api.ecommerce.repository.ProductoRepository;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(ProductoRepository prodRepo, CategoriaRepository catRepo,
            EtiquetaRepository etiqRepo) {
        return args -> {
            Categoria catJerseys = catRepo.save(new Categoria("Jerseys"));
            Categoria catZapatillas = catRepo.save(new Categoria("Zapatillas"));

            Etiqueta tagOferta = etiqRepo.save(new Etiqueta("Oferta"));
            Etiqueta tagNuevo = etiqRepo.save(new Etiqueta("Nuevo"));
            Etiqueta tagExclusivo = etiqRepo.save(new Etiqueta("Exclusivo"));

            prodRepo.save(new Producto("Jersey LeBron James #23 Lakers", 129.99, catJerseys,
                    List.of(tagNuevo, tagExclusivo)));
            prodRepo.save(new Producto("Zapatillas Nike Luka 2", 140.00, catZapatillas, List.of(tagOferta)));
            prodRepo.save(new Producto("Jersey Stephen Curry #30 Warriors", 110.00, catJerseys, List.of(tagOferta)));
        };
    }
}
