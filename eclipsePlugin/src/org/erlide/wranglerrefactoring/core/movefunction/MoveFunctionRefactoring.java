package org.erlide.wranglerrefactoring.core.movefunction;

import org.erlide.jinterface.rpc.RpcException;
import org.erlide.runtime.backend.RpcResult;
import org.erlide.runtime.backend.exceptions.ErlangRpcException;
import org.erlide.wranglerrefactoring.core.RefactoringParameters;
import org.erlide.wranglerrefactoring.core.WranglerRefactoring;

import com.ericsson.otp.erlang.OtpErlangBoolean;
import com.ericsson.otp.erlang.OtpErlangList;

public class MoveFunctionRefactoring extends WranglerRefactoring {

	boolean isNewModule;

	public void setIsNewModule(boolean b) {
		isNewModule = b;
	}

	public MoveFunctionRefactoring(RefactoringParameters parameters) {
		super(parameters);
	}

	@Override
	public String getName() {
		return "Move function";
	}

	@Override
	protected RpcResult sendRPC(String filePath, OtpErlangList searchPath)
			throws ErlangRpcException, RpcException {
		return managedBackend.rpc("wrangler", "move_fun_eclipse", "siisxx",
				filePath, parameters.getStartLine(), parameters
						.getStartColoumn(), newName, new OtpErlangBoolean(
						isNewModule), searchPath);
	}
}
