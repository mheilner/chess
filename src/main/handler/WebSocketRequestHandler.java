package handler;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

@WebSocket
public class WebSocketRequestHandler {

    private final Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        // Determine the command type and deserialize accordingly
        CommandTypeWrapper commandTypeWrapper = gson.fromJson(message, CommandTypeWrapper.class);
        UserGameCommand command = deserializeCommand(commandTypeWrapper, message);

        // Process the command
        ServerMessage response = processCommand(command);

        // Serialize and send the response
        session.getRemote().sendString(gson.toJson(response));
    }

    private UserGameCommand deserializeCommand(CommandTypeWrapper commandTypeWrapper, String json) {
        // Based on the commandType, deserialize to the specific subclass
        switch (commandTypeWrapper.getCommandType()) {
            case JOIN_PLAYER:
                return gson.fromJson(json, JoinPlayerCommand.class);
            case JOIN_OBSERVER:
                return gson.fromJson(json, JoinObserverCommand.class);
            case LEAVE:
                return gson.fromJson(json, LeaveCommand.class);
            case MAKE_MOVE:
                return gson.fromJson(json, MakeMoveCommand.class);
            case RESIGN:
                return gson.fromJson(json, ResignCommand.class);
            default:
                throw new IllegalArgumentException("Unknown command type");
        }
    }

    private ServerMessage processCommand(UserGameCommand command) {
        // Implement your command processing logic here
        // This will likely involve calling your game service methods
        // and creating a ServerMessage based on the outcome

        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                return null;
//                return processJoinPlayerCommand((JoinPlayerCommand) command);
//            case JOIN_OBSERVER:
//                return processJoinObserverCommand((JoinObserverCommand) command);
//            case MAKE_MOVE:
//                return processMakeMoveCommand((MakeMoveCommand) command);
//            case LEAVE:
//                return processLeaveCommand((LeaveCommand) command);
//            case RESIGN:
//                return processResignCommand((ResignCommand) command);
            default:
                return new ErrorMessage("Unknown command type");
        }
    }




    private static class CommandTypeWrapper {
        private UserGameCommand.CommandType commandType;

        public UserGameCommand.CommandType getCommandType() {
            return commandType;
        }
    }
}
