package academy.gama.apialunos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import academy.gama.apialunos.ui.layout.MainScreenUI;

@SpringBootApplication
public class ApialunosApplication {
	public static void main(String[] args) {
		new MainScreenUI(MainScreenUI.getLanguage());
		 SpringApplication.run(ApialunosApplication.class, args);
	}
}