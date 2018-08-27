/*******************************************************************************************************
 *
 * msi.gaml.compilation.IAgentConstructor.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.compilation;

import java.util.HashMap;
import java.util.Map;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.runtime.exceptions.GamaRuntimeException;

/**
 * Written by drogoul Modified on 20 ao�t 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
@FunctionalInterface
public interface IAgentConstructor<T extends IAgent> {

	public static class Minimal implements IAgentConstructor<MinimalAgent> {

		/**
		 * Method createOneAgent()
		 * 
		 * @see msi.gaml.compilation.IAgentConstructor#createOneAgent(msi.gama.metamodel.population.IPopulation)
		 */

		@Override
		public MinimalAgent createOneAgent(final IPopulation manager, final int index) throws GamaRuntimeException {
			return new MinimalAgent(manager, index);
		}

	}

	public static class Gaml implements IAgentConstructor<GamlAgent> {

		@Override
		public GamlAgent createOneAgent(final IPopulation manager, final int index) throws GamaRuntimeException {
			return new GamlAgent(manager, index);
		}

	}

	public static Map<Class<? extends IAgent>, IAgentConstructor<? extends IAgent>> CONSTRUCTORS =
			new HashMap<Class<? extends IAgent>, IAgentConstructor<? extends IAgent>>() {

				{
					put(GamlAgent.class, new Gaml());
					put(MinimalAgent.class, new Minimal());
				}
			};

	public <T extends IAgent> T createOneAgent(IPopulation<T> manager, int index) throws GamaRuntimeException;

}
