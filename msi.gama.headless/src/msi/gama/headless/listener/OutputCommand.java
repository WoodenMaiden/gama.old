/*******************************************************************************************************
 *
 * OutputCommand.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.0).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.listener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.geotools.feature.SchemaException;
import org.java_websocket.WebSocket;

import msi.gama.headless.core.GamaServerMessageType;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.SaveStatement;
import msi.gaml.statements.save.GeoJSonSaver;
import ummisco.gama.dev.utils.DEBUG;

/**
 * The Class OutputCommand.
 */
public class OutputCommand implements ISocketCommand {

	@Override
	public CommandResponse execute(final WebSocket socket, final IMap<String, Object> map) {

		final String exp_id = map.get("exp_id") != null ? map.get("exp_id").toString() : "";
		final Object species = map.get("species");
		final String socket_id =
				map.get("socket_id") != null ? map.get("socket_id").toString() : "" + socket.hashCode();
		final GamaWebSocketServer gamaWebSocketServer = (GamaWebSocketServer) map.get("server");
		DEBUG.OUT("output");
		DEBUG.OUT(exp_id);

		if (exp_id == "" || species == null) return new CommandResponse(GamaServerMessageType.MalformedRequest,
				"For 'output', mandatory parameters are: 'exp_id' and 'species' ", map, false);

		var gama_exp = gamaWebSocketServer.get_listener().getExperiment(socket_id, exp_id);
		if (gama_exp == null || gama_exp.getSimulation() == null)
			return new CommandResponse(GamaServerMessageType.UnableToExecuteRequest,
					"Unable to find the experiment or simulation", map, false);
		final boolean wasPaused = gama_exp.controller.isPaused();
		gama_exp.controller.directPause();
		// IList<? extends IShape> agents =
		// gama_exp.getSimulation().getSimulation().getPopulationFor(species.toString());

		@SuppressWarnings ("unchecked") final IList<String> ll =
				map.get("attributes") != null ? (IList<String>) map.get("attributes") : GamaListFactory.EMPTY_LIST;
		final String crs = map.get("crs") != null ? map.get("crs").toString() : "";
		String res = "";
		GamaServerMessageType status = GamaServerMessageType.CommandExecutedSuccessfully;
		try {
			// res = buildGeoJSon(gama_exp.getSimulation().getExperimentPlan().getAgent().getScope(), agents, ll, crs);
			final SpeciesDescription spec = gama_exp.getSimulation().getSimulation()
					.getPopulationFor(species.toString()).getSpecies().getDescription();
			res = buildGeoJSon(gama_exp.getSimulation().getExperimentPlan().getAgent().getScope(), spec, ll, crs);
		} catch (Exception ex) {
			res = ex.getMessage();
			status = GamaServerMessageType.RuntimeError;
		}

		if (!wasPaused) { gama_exp.controller.userStart(); }
		return new CommandResponse(status, res, map, true);
	}

	/**
	 * Builds the geo J son.
	 *
	 * @param scope
	 *            the scope
	 * @param agents
	 *            the agents
	 * @param filterAttr
	 *            the filter attr
	 * @param gis_code
	 *            the gis code
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws SchemaException
	 *             the schema exception
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public String buildGeoJSon(final IScope scope, final SpeciesDescription species, final IList<String> filterAttr,
			final String gis_code) throws GamaRuntimeException {
		final GeoJSonSaver gjsoner = new GeoJSonSaver();
		try {
			Arguments args = new Arguments();
			for (final String var : species.getAttributeNames()) {
				if (!SaveStatement.NON_SAVEABLE_ATTRIBUTE_NAMES.contains(var) && filterAttr.contains(var)) {
					args.put(var, species.getVarExpr(var, false));
				}
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gjsoner.save(scope, species.getSpeciesExpr(), baos, gis_code, args);
			return baos.toString(StandardCharsets.UTF_8);

		} catch (final GamaRuntimeException e) {
			throw e;
		} catch (final Throwable e) {
			throw GamaRuntimeException.create(e, scope);
		}
	}

}
