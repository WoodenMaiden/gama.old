/*******************************************************************************************************
 *
 * msi.gaml.species.ISpecies.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.species;

import java.util.Collection;
import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.population.IPopulationSet;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.ITypeProvider;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IAddressableContainer;
import msi.gama.util.IList;
import msi.gaml.architecture.IArchitecture;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Containers;
import msi.gaml.statements.ActionStatement;
import msi.gaml.statements.IExecutable;
import msi.gaml.statements.IStatement;
import msi.gaml.statements.UserCommandStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import msi.gaml.variables.IVariable;
import one.util.streamex.StreamEx;

/**
 * Written by drogoul Modified on 25 avr. 2010
 *
 * @todo Description
 *
 */
@vars ({ @variable (
		name = ISpecies.ACTIONS,
		type = IType.LIST,
		of = IType.STRING,
		doc = @doc ("A list of the names of the actions defined in this species")),
		@variable (
				name = ISpecies.ASPECTS,
				type = IType.LIST,
				of = IType.STRING,
				doc = @doc ("A list of the names of the aspects defined in this species")),
		@variable (
				name = IKeyword.ATTRIBUTES,
				type = IType.LIST,
				of = IType.STRING,
				doc = @doc ("A list of the names of the attributes of this species")),
		@variable (
				name = IKeyword.PARENT,
				type = IType.SPECIES,
				doc = @doc ("The parent (if any) of this species")),
		@variable (
				name = IKeyword.NAME,
				type = IType.STRING,
				doc = @doc ("The name of the species")),
		@variable (
				name = ISpecies.SUBSPECIES,
				type = IType.LIST,
				of = IType.SPECIES,
				doc = @doc ("A list of the names of subspecies of this species")),
		@variable (
				name = ISpecies.MICROSPECIES,
				type = IType.LIST,
				of = IType.STRING,
				doc = @doc ("A list of the names of the micro-species declared inside this species")),
		@variable (
				name = ISpecies.POPULATION,
				type = IType.LIST,
				of = ITypeProvider.CONTENT_TYPE_AT_INDEX + 1,
				doc = @doc ("The population that corresponds to this species in an instance of its host")) })
public interface ISpecies
		extends ISymbol, IAddressableContainer<Integer, IAgent, Integer, IAgent>, IPopulationSet<IAgent> {

	public static final String stepActionName = "_step_";
	public static final String initActionName = "_init_";
	public static final String POPULATION = "population";
	public static final String SUBSPECIES = "subspecies";
	public static final String MICROSPECIES = "microspecies";
	public static final String ACTIONS = "actions";
	public static final String ASPECTS = "aspects";

	public abstract IExpression getFrequency();

	public abstract IExpression getSchedule();

	public abstract IExpression getConcurrency();

	public abstract boolean extendsSpecies(final ISpecies s);

	public abstract boolean isGrid();

	public abstract boolean isGraph();

	/**
	 * Return all the direct subspecies of this species, properly typed for GAMA
	 * 
	 * @return
	 */

	public abstract IList<ISpecies> getSubSpecies(IScope scope);

	@SuppressWarnings ("unchecked")
	@getter (SUBSPECIES)
	@doc ("Returns all the direct subspecies names of this species")
	default IList<String> getSubSpeciesNames(final IScope scope) {
		return StreamEx.of(getSubSpecies(scope)).map((each) -> each.getName())
				.toCollection(Containers.listOf(Types.STRING));
	}

	@Override
	@getter (IKeyword.NAME)
	public abstract String getName();

	/**
	 * Returns all the micro-species. Micro-species includes: 1. the "direct" micro-species; 2. the micro-species of the
	 * parent-species.
	 *
	 * @return
	 */
	public abstract IList<ISpecies> getMicroSpecies();

	/**
	 * Returns a micro-species with the specified name or null otherwise.
	 *
	 * @param microSpeciesName
	 * @return a species or null
	 */
	public abstract ISpecies getMicroSpecies(String microSpeciesName);

	/**
	 * Verifies if this species has micro-species or not.
	 *
	 * @return true if this species has micro-species false otherwise
	 */
	public abstract boolean hasMicroSpecies();

	/**
	 * Verifies of the specified species is a micro-species of this species of not.
	 *
	 * @param species
	 * @return
	 */
	public abstract boolean containMicroSpecies(ISpecies species);

	/**
	 * Returns the parent species.
	 *
	 * @return
	 */
	@getter (IKeyword.PARENT)
	@doc ("Returns the direct parent of the species. Experiments, models and species with no explicit parents will return nil")
	public abstract ISpecies getParentSpecies();

	/**
	 * Verifies that if this species is the peer species of other species.
	 *
	 * @param other
	 * @return
	 */
	public abstract boolean isPeer(ISpecies other);

	public abstract List<ISpecies> getSelfWithParents();

	public abstract Collection<UserCommandStatement> getUserCommands();

	// Huynh Quang Nghi 29/01/13
	public abstract <T extends IStatement> T getStatement(Class<T> clazz, String name);

	public abstract IStatement.WithArgs getAction(final String name);

	@getter (ACTIONS)
	@doc ("retuns the list of actions defined in this species (incl. the ones inherited from its parent)")
	default IList<String> getActionNames(final IScope scope) {
		return GamaListFactory.create(scope, Types.STRING,
				StreamEx.of(getActions()).map((each) -> each.getName()).toList());
	}

	public Collection<ActionStatement> getActions();

	public abstract IExecutable getAspect(final String n);

	public Collection<? extends IExecutable> getAspects();

	@getter (ASPECTS)
	@doc ("retuns the list of aspects defined in this species")
	public abstract IList<String> getAspectNames();

	public abstract IArchitecture getArchitecture();

	public abstract String getArchitectureName();

	public abstract ISpecies getMacroSpecies();

	public abstract String getParentName();

	public abstract IVariable getVar(final String n);

	public abstract Collection<String> getVarNames();

	/**
	 * Similar to getVarNames(), but returns a correctly initialized IList of attribute names
	 * 
	 * @param scope
	 * @return the list of all the attributes defined in this species
	 */
	@getter (IKeyword.ATTRIBUTES)
	@doc ("retuns the list of attributes defined in this species (incl. the ones inherited from its parent)")
	default IList<String> getAttributeNames(final IScope scope) {
		return GamaListFactory.create(scope, Types.STRING, getVarNames());
	}

	public abstract Collection<IVariable> getVars();

	public abstract boolean hasAspect(final String n);

	public abstract boolean hasVar(final String name);

	public abstract void setMacroSpecies(final ISpecies macroSpecies);

	public abstract boolean isMirror();

	public abstract Boolean implementsSkill(String skill);

	@getter (MICROSPECIES)
	@doc ("Returns all the direct microspecies names of this species")
	public abstract Collection<String> getMicroSpeciesNames();

	public abstract boolean isInitOverriden();

	public abstract boolean isStepOverriden();

	/**
	 * Returns the population of agents that belong to this species and that are hosted in the same host
	 * 
	 * @param scope
	 * @return
	 *
	 */
	@Override
	@getter (POPULATION)
	@doc ("Returns the population of agents that belong to this species")
	public abstract IPopulation<? extends IAgent> getPopulation(IScope scope);

	public abstract void addTemporaryAction(ActionStatement a);

	public abstract Collection<IStatement> getBehaviors();

	public abstract void removeTemporaryAction();

	@Override
	public abstract SpeciesDescription getDescription();

}