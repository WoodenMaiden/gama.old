/**
 *  SIR_split_in_agents_multiple_strains.gaml
 *  Author: tri and nghi
 *  Description: This model is an extension of the model SIR_split_in_agents.gaml.
 *  It creates several agents of one compartment type (here typically several I_agt).
 */
model SIR_split_in_agents_multiple_strains

global {
	// Parameters
	int number_S <- 495 ; // The number of susceptible
	int number_I <- 5 ; // The number of infected
	int number_R <- 0 ; // The number of removed 

	float _beta <- 1.0 ; // The parameter Beta
	float _delta <- 0.01 ; // The parameter Delta
	
	// Global variables
	int strain_number <- 2;
	int N <- number_S + number_I * strain_number + number_R;	
	float hKR4 <- 0.01;
	
	init {
		create S_agt {
			Ssize <- number_S;
		}

		create I_agt number : strain_number {
			Isize <- number_I;
			self.beta <- _beta; 
			self.delta <- _delta; 
		}

		create R_agt {
			Rsize <- number_R;
		}

		create my_SIR_maths {
			self.Sm <- number_S;
			self.Im <- number_I * strain_number;
			self.Rm <- number_R;
		}

		write 'Basic Reproduction Number (R0): ' + string(_beta * number_S / (_delta));
	}

}

entities {
	species S_agt {
		float t;		
		float Ssize;
		
		equation evol simultaneously : [I_agt, R_agt] {
			diff(self.Ssize, t) = (- sum(I_agt accumulate [each.beta * each.Isize]) * self.Ssize / N);
		}

		solve evol method : "rk4" step : hKR4 ;
	}

	species I_agt {
		float t;		
		float Isize;
		 
		float beta;
		float delta;
		
		equation evol simultaneously : [S_agt, R_agt] {
			diff(self.Isize, t) = (beta * first(S_agt).Ssize * self.Isize / N) - (delta * self.Isize);
		}
	}

	species R_agt {
		float t;		
		float Rsize;

		equation evol simultaneously : [I_agt] {
			diff(self.Rsize, t) = (sum(I_agt collect (each.delta * each.Isize)));
		}
	}

	species my_SIR_maths {
		float t;
		float Im;
		float Sm;
		float Rm;
		
		equation SIR {
			diff(self.Sm, t) = (-_beta * Sm * Im / N);
			diff(self.Im, t) = (_beta * Sm * Im / N) - (_delta * Im);
			diff(self.Rm, t) = (_delta * Im);
		}

		solve SIR method : "rk4" step : hKR4;
	}

}

experiment Simulation type : gui {
	parameter 'Number of Susceptible' type: int var: number_S <- 495 category: "Initial population"; 
	parameter 'Number of Infected'    type: int var: number_I <- 5   category: "Initial population";
	parameter 'Number of Removed'     type: int var: number_R <- 0   category: "Initial population";

	parameter 'Beta (S->I)'  type: float var: _beta <- 1.0   category: "Parameters";
	parameter 'Delta (I->R)' type: float var: _delta <- 0.01 category: "Parameters";	
	
	output {
		display chart_3system_eq refresh_every : 1 {
			chart 'Split system' type : series background : rgb('lightGray') {
				data 'susceptible' value : first(S_agt).Ssize color : rgb('green');
				data 'infected0' value : first(I_agt).beta * first(I_agt).Isize color : rgb('white');
				data 'infected1' value : last(I_agt).beta * last(I_agt).Isize color : rgb('yellow');
				data 'i1+i2' value : sum(I_agt accumulate (each.beta * each. Isize)) color : rgb ( 'red' ) ;				
				data 'recovered' value : first(R_agt).Rsize color : rgb('blue');
			}

		}

		display chart_1system_eq refresh_every : 1 {
			chart 'unified system' type : series background : rgb('lightGray') {
				data 'susceptible_maths' value : first(my_SIR_maths).Sm color : rgb('green');
				data 'infected_maths' value : first(my_SIR_maths).Im color : rgb('red');
				data 'recovered_maths' value : first(my_SIR_maths).Rm color : rgb('blue');
			}
		}
	}
}


