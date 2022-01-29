/*******************************************************************************************************
 *
 * ClassicalSEIREquations.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.classicalEquations.epidemiology;

import java.util.ArrayList;
import java.util.List;

import msi.gaml.compilation.GAML;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.expressions.data.ListExpression;
import ummisco.gaml.extensions.maths.ode.statements.SingleEquationStatement;

// SEIR (with demography) equation is defined by
// diff(S,t) = mu * N - beta * S * I / N - mu * S ;
// diff(E,t) = beta * S * I / N - mu * E - sigma * E ;
// diff(I,t) = sigma * E - mu * I - gamma * I;
// diff(R,t) = gamma * I - mu * R ;
//
// It is called using
// equation eqSEIR type: SIRS with_vars: [S,E,I,R,t] with_params: [N,beta,gamma,sigma,mu]

/**
 * The Class ClassicalSEIREquations.
 */
public class ClassicalSEIREquations {
	
	/** The parent desc. */
	private final IDescription parentDesc;

	/**
	 * Instantiates a new classical SEIR equations.
	 *
	 * @param p the p
	 */
	public ClassicalSEIREquations(final IDescription p) {
		parentDesc = p;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public IDescription getDescription() {
		return parentDesc;
	}

	/**
	 * Seir.
	 *
	 * @param with_vars the with vars
	 * @param with_params the with params
	 * @return the list
	 */
	public List<SingleEquationStatement> SEIR(final ListExpression with_vars, final ListExpression with_params) {
		if (with_vars == null || with_params == null) return null;
		final ArrayList<SingleEquationStatement> cmd = new ArrayList<>();
		final IExpression[] v = with_vars.getElements();
		final IExpression[] p = with_params.getElements();

		final StatementDescription stm = new StatementDescription("=", getDescription(), false, null, null, null);

		// diff(S,t) = mu * N - beta * S * I / N - mu * S ;
		// diff(E,t) = beta * S * I / N - mu * E - sigma * E ;
		// diff(I,t) = sigma * E - mu * I - gamma * I;
		// diff(R,t) = gamma * I - mu * R ;
		//
		// equation eqSEIR type: SIRS with_vars: [S,E,I,R,t] with_params:
		// [N,beta,gamma,sigma,mu]

		final SingleEquationStatement eq1 = new SingleEquationStatement(stm);
		eq1.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[0].literalValue() + "," + v[4].literalValue() + ")", getDescription()));
		eq1.setExpression(GAML.getExpressionFactory()
				.createExpr("( " + p[4].literalValue() + " * " + p[0].literalValue() + " ) + " + "(- "
						+ p[1].literalValue() + " * " + v[0].literalValue() + " * " + v[2].literalValue() + " / "
						+ p[0].literalValue() + ") + " + "(- " + p[4].literalValue() + " * " + v[0].literalValue()
						+ " )", getDescription()));
		// eq1.establishVar();
		cmd.add(eq1);

		final SingleEquationStatement eq2 = new SingleEquationStatement(stm);
		eq2.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[1].literalValue() + "," + v[4].literalValue() + ")", getDescription()));
		eq2.setExpression(GAML.getExpressionFactory()
				.createExpr("(" + p[1].literalValue() + " * " + v[0].literalValue() + " * " + v[2].literalValue()
						+ " / " + p[0].literalValue() + ") + " + "( - " + p[4].literalValue() + " * "
						+ v[1].literalValue() + ") + " + "( - " + p[3].literalValue() + " * " + v[1].literalValue()
						+ ")", getDescription()));
		// eq2.establishVar();
		cmd.add(eq2);

		final SingleEquationStatement eq3 = new SingleEquationStatement(stm);
		eq3.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[2].literalValue() + "," + v[4].literalValue() + ")", getDescription()));
		eq3.setExpression(GAML.getExpressionFactory()
				.createExpr("(" + p[3].literalValue() + " * " + v[1].literalValue() + ") + " + "(- "
						+ p[4].literalValue() + " * " + v[2].literalValue() + ") + " + "(- " + p[2].literalValue()
						+ " * " + v[2].literalValue() + ")", getDescription()));
		// eq3.establishVar();
		cmd.add(eq3);

		final SingleEquationStatement eq4 = new SingleEquationStatement(stm);
		eq4.setFunction(GAML.getExpressionFactory()
				.createExpr("diff(" + v[3].literalValue() + "," + v[4].literalValue() + ")", getDescription()));
		eq4.setExpression(GAML.getExpressionFactory().createExpr("(" + p[2].literalValue() + " * " + v[2].literalValue()
				+ ") + " + "(- " + p[4].literalValue() + " * " + v[3].literalValue() + ")", getDescription()));
		// eq4.establishVar();
		cmd.add(eq4);

		return cmd;
	}

}
