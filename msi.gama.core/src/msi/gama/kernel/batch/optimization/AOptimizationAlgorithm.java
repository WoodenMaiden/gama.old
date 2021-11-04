/*******************************************************************************************************
 *
 * msi.gama.kernel.batch.ParamSpaceExploAlgorithm.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8.1)
 * 
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.kernel.batch.optimization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.batch.IExploration;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.kernel.experiment.ParameterAdapter;
import msi.gama.kernel.experiment.ParametersSet;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.GAMA.InScope;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.ISymbol;
import msi.gaml.compilation.Symbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.IType;

/**
 * The Class AOptimizationAlgorithm.
 */
@inside (
		kinds = { ISymbolKind.EXPERIMENT })
public abstract class AOptimizationAlgorithm extends Symbol implements IExploration {

	public final static short C_MAX = 0, C_MIN = 1, C_MEAN = 2;
	public final static String[] COMBINATIONS = new String[] { "maximum", "minimum", "average" };
	static { AbstractGamlAdditions._constants(COMBINATIONS); }
	
	// private ContinuousUniformGenerator randUniform;
	protected HashMap<ParametersSet, Double> testedSolutions;
	protected IExpression fitnessExpression;
	protected boolean isMaximize;
	protected BatchAgent currentExperiment;
	// protected IScope scope;
	protected ParametersSet bestSolution = null;
	protected Double bestFitness = null;
	protected short combination;

	protected abstract ParametersSet findBestSolution(IScope scope) throws GamaRuntimeException;

	@Override
	public void initializeFor(final IScope scope, final BatchAgent agent) throws GamaRuntimeException {
		currentExperiment = agent;
		// this.scope = scope;
	}

	// protected ContinuousUniformGenerator getRandUniform() {
	// if ( randUniform == null ) {
	// randUniform = scope.getRandom().createUniform(0., 1.);
	// }
	// return randUniform;
	// }

	protected void initializeTestedSolutions() {
		testedSolutions = new HashMap<ParametersSet, Double>();
	}

	protected void initParams() {
		GAMA.run(new InScope.Void() {

			@Override
			public void process(final IScope scope) {
				initParams(scope);
			}
		});
	}

	protected void initParams(final IScope scope) {}

	public AOptimizationAlgorithm(final IDescription desc) {
		super(desc);
		initializeTestedSolutions();
		fitnessExpression = getFacet(IKeyword.MAXIMIZE, IKeyword.MINIMIZE);
		isMaximize = hasFacet(IKeyword.MAXIMIZE);
		final String ag = getLiteral(IKeyword.AGGREGATION);
		combination = IKeyword.MAX.equals(ag) ? C_MAX : IKeyword.MIN.equals(ag) ? C_MIN : C_MEAN;
		bestFitness = isMaximize ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
	}

	@Override
	public void run(final IScope scope) {
		try {
			findBestSolution(scope);
		} catch (final GamaRuntimeException e) {
			GAMA.reportError(scope, e, false);
		}
	}

	// @Override
	// public void start() {
	// new Thread(this, getName() + " thread").start();
	// }

	@Override
	public void setChildren(final Iterable<? extends ISymbol> commands) {}

	@Override
	public void addParametersTo(final List<IParameter.Batch> params, final BatchAgent agent) {
		params.add(new ParameterAdapter("Calibration method", IExperimentPlan.BATCH_CATEGORY_NAME, IType.STRING) {

			@Override
			public Object value() {
				@SuppressWarnings ("rawtypes") final List<Class> classes = Arrays.asList(CLASSES);
				final String methodName = IKeyword.METHODS[classes.indexOf(AOptimizationAlgorithm.this.getClass())];
				final String fit = fitnessExpression == null ? "" : "fitness = "
						+ (isMaximize ? " maximize " : " minimize ") + fitnessExpression.serialize(false);
				final String sim = fitnessExpression == null ? ""
						: (combination == C_MAX ? " max " : combination == C_MIN ? " min " : " average ") + "of "
								+ agent.getSeeds().length + " simulations";
				return "Method " + methodName + " | " + fit + " | " + "compute the" + sim + " for each solution";
			}

		});
	}
	
	@Override
	public boolean isFitnessBased() { return true; }
	
	@Override
	public IExpression getOutputs() { return getFitnessExpression(); }

	// ------------
	// OPTIMIZATION
	
	/**
	 * Return the best fitness of the experiment
	 * @return Double
	 */
	public Double getBestFitness() { return bestFitness; }

	/**
	 * Return the expression that characterizes the fitness computation
	 * @return IExpression
	 */
	public IExpression getFitnessExpression() { return fitnessExpression; }

	/**
	 * Return the set of parameter @ParametersSet attached to the best fitness
	 * @return ParametersSet
	 */
	public ParametersSet getBestSolution() { return bestSolution; }

	/**
	 * If the fitness should maximize (or minimize) the corresponding value
	 * @return boolean
	 */
	public boolean getIsMaximize() { return this.isMaximize; }
	
	/**
	 * Returns the way to combine replication fitness (either min, max or mean)
	 * @return short
	 */
	public short getCombination() { return combination; }

	public boolean isMaximize() { return isMaximize; }
	
	public String getCombinationName() { return COMBINATIONS[combination]; }
	
	protected void setBestSolution(final ParametersSet bestSolution) {
		// scope.getGui().debug("ParamSpaceExploAlgorithm.setBestSolution : " +
		// bestSolution);
		this.bestSolution = new ParametersSet(bestSolution);
	}

	protected void setBestFitness(final Double bestFitness) {
		// scope.getGui().debug("ParamSpaceExploAlgorithm.setBestFitness : " +
		// bestFitness);
		this.bestFitness = bestFitness;
	}

	public void updateBestFitness(final ParametersSet solution, final Double fitness) {
		if (fitness == null)
			return;
		Double best = getBestFitness();
		if (bestSolution == null || (isMaximize() ? fitness > best : fitness < best)) {
			setBestFitness(fitness);
			setBestSolution(solution);
		}
	}
}
