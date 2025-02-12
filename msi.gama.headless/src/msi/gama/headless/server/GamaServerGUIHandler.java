/*******************************************************************************************************
 *
 * GamaServerGUIHandler.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation
 * platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.server;

import static msi.gama.runtime.server.ISocketCommand.LOAD;
import static msi.gama.runtime.server.ISocketCommand.PLAY;
import static msi.gama.runtime.server.ISocketCommand.STOP;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import msi.gama.common.interfaces.IConsoleListener;
import msi.gama.common.interfaces.IStatusDisplayer;
import msi.gama.headless.listener.LoadCommand;
import msi.gama.headless.listener.PlayCommand;
import msi.gama.headless.listener.StopCommand;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.NullGuiHandler;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.runtime.server.GamaServerConsoleListener;
import msi.gama.runtime.server.GamaServerMessage;
import msi.gama.runtime.server.GamaServerMessager;
import msi.gama.runtime.server.GamaServerStatusDisplayer;
import msi.gama.runtime.server.ISocketCommand;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Implements the behaviours to trigger when GUI events happen in a simulation run in GamaServer
 *
 */
public class GamaServerGUIHandler extends NullGuiHandler {

	/** The status. */
	IStatusDisplayer status;

	/** The dialog messager. */
	GamaServerMessager dialogMessager = new GamaServerMessager() {

		@Override
		public boolean canSendMessage(final IExperimentAgent exp) {
			if (exp == null) return false;
			var scope = exp.getScope();
			return scope != null && scope.getServerConfiguration().dialog();
		}

	};

	/**
	 * Can send runtime errors.
	 *
	 * @author Alexis Drogoul (alexis.drogoul@ird.fr)
	 * @param scope
	 *            the scope
	 * @return true, if successful
	 * @date 14 août 2023
	 */
	private boolean canSendRuntimeErrors(final IScope scope) {
		if (scope != null && scope.getExperiment() != null && scope.getExperiment().getScope() != null)
			return scope.getExperiment().getScope().getServerConfiguration().runtime();
		return true;
	}

	@Override
	public void openMessageDialog(final IScope scope, final String message) {
		DEBUG.OUT(message);
		if (!dialogMessager.canSendMessage(scope.getExperiment())) return;
		dialogMessager.sendMessage(scope.getExperiment(), message, GamaServerMessage.Type.SimulationDialog);
	}

	@Override
	public void openErrorDialog(final IScope scope, final String error) {
		DEBUG.OUT(error);
		if (!dialogMessager.canSendMessage(scope.getExperiment())) return;
		dialogMessager.sendMessage(scope.getExperiment(), error, GamaServerMessage.Type.SimulationErrorDialog);
	}

	@Override
	public void runtimeError(final IScope scope, final GamaRuntimeException g) {
		DEBUG.OUT(g);
		// removed to fix #3758
		// if (!canSendDialogMessages(scope)) return;
		if (!canSendRuntimeErrors(scope)) return;
		dialogMessager.sendMessage(scope.getExperiment(), g, GamaServerMessage.Type.SimulationError);
	}

	@Override
	public IStatusDisplayer getStatus() {
		if (status == null) { status = new GamaServerStatusDisplayer(); }
		return status;
	}

	@Override
	public IConsoleListener getConsole() {
		if (console == null) { console = new GamaServerConsoleListener(); }
		return console;

	}

	@Override
	public Map<String, ISocketCommand> getServerCommands() {
		final Map<String, ISocketCommand> cmds = new HashMap<>(super.getServerCommands());
		// We replace some commands by specialized commands
		cmds.put(LOAD, new LoadCommand());
		cmds.put(PLAY, new PlayCommand());
		cmds.put(STOP, new StopCommand());
		return Collections.unmodifiableMap(cmds);
	}

}
