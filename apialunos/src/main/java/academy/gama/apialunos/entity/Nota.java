package academy.gama.apialunos.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import academy.gama.apialunos.enums.StatusAprovacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Nota {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private double nota_um;
	
	@Column
	private double nota_dois;
	
	@Column
	private double nota_apresentacao;
	
	@Column
	private double nota_trabalho;
	
	@Column
	private double media_conceito;
	
	@Column
	private StatusAprovacao statusAprovacao;
	
	@OneToOne
	private Aluno aluno;
	
	@OneToOne
	private Disciplina disciplina;

	public double getMedia_conceito() {
//		return Math.round(media_conceito);
		return media_conceito;
	}

	private void setMedia_conceito() {
		float num = 4;
		this.media_conceito = (nota_um + nota_dois + nota_apresentacao + nota_trabalho) / num;
	}
	
	private void setStatusAprovacao() {
		if (this.media_conceito >= 7) {
			statusAprovacao = StatusAprovacao.APROVADO;
		} else if (this.media_conceito < 4) {
			statusAprovacao = StatusAprovacao.REPROVADO;
		} else {
			statusAprovacao = StatusAprovacao.RECUPERACAO;
		}
	}
	
	public void resolverNotasEConceito() {
		setMedia_conceito();
		setStatusAprovacao();
	}
	
}
