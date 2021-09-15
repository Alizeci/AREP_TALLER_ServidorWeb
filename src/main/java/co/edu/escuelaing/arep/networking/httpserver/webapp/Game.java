package co.edu.escuelaing.arep.networking.httpserver.webapp;

import java.util.concurrent.ThreadLocalRandom;

import co.edu.escuelaing.arep.networking.httpserver.myspring.Component;
import co.edu.escuelaing.arep.networking.httpserver.myspring.Service;

/**
 * POJO Game, con sus características básicas
 * @author Alejandra Izquierdo
 *
 */
@Component
public class Game {
	
	@Service(uri = "/randomsentence")
	public String random() {
		String[] juegos = {"Fall Guys", "Age of Empires II", "Valorant", "Mario Bros", "Halo MCC", "Minecraft"};
        int indiceAleatorio = numeroAleatorioEnRango(0, juegos.length - 1);
        String juegoAleatorio = juegos[indiceAleatorio];
		return juegoAleatorio;
	}
	
	public int numeroAleatorioEnRango(int minimo, int maximo) {       
        return ThreadLocalRandom.current().nextInt(minimo, maximo + 1);
    }

}
