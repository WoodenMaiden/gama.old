/*******************************************************************************************************
 *
 * DormandPrince54Solver.java, in ummisco.gaml.extensions.maths, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gaml.extensions.maths.ode.utils.solver;

import org.apache.commons.math3.ode.nonstiff.DormandPrince54Integrator;

import msi.gama.util.IList;
import msi.gama.util.IMap;

/**
 * The Class DormandPrince54Solver.
 */
public class DormandPrince54Solver extends Solver {

	/**
	 * Instantiates a new dormand prince 54 solver.
	 *
	 * @param minStep the min step
	 * @param maxStep the max step
	 * @param scalAbsoluteTolerance the scal absolute tolerance
	 * @param scalRelativeTolerance the scal relative tolerance
	 * @param integrated_val the integrated val
	 */
	public DormandPrince54Solver(final double minStep, final double maxStep, final double scalAbsoluteTolerance,
			final double scalRelativeTolerance, final IMap<String, IList<Double>> integrated_val) {
		super((minStep + maxStep) / 2,
				new DormandPrince54Integrator(minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance),
				integrated_val);
	}

}