/*
 *    Copyright 2019 ART
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.art.rsocket.constants;

public interface RsocketModuleConstants {
    String RSOCKET_MODULE_ID = "RSOCKET_MODULE";
    String EXECUTE_RSOCKET_FUNCTION = "EXECUTE_RSOCKET_FUNCTION";
    String SERVICE_ID = "serviceId";
    String METHOD_ID = "methodId";
    String REQUEST_DATA = "requestData";
    String RSOCKET_SERVICE_TYPE = "RSOCKET";
    String RSOCKET_COMMUNICATION_SERVICE_TYPE = "RSOCKET_COMMUNICATION";
    String RSOCKET_COMMUNICATION_TARGET_CONFIGURATION_NOT_FOUND = "RSocket communication target configuration was not found for serviceId: ''{0}''";
    String RSOCKET_SERVER_THREAD = "rsocket-server-bootstrap-thread";
    String BINARY_MIME_TYPE = "application/binary";
    int DEFAULT_RSOCKET_TCP_PORT = 9000;
    int DEFAULT_RSOCKET_WEB_SOCKET_PORT = 10000;
    String REACTIVE_SERVICE_EXCEPTION_ERROR_CODE = "REACTIVE_SERVICE_EXCEPTION";

    enum RsocketDataFormat {
        PROTOBUF,
        JSON,
        XML
    }

    enum RsocketTransport {
        TCP,
        WEB_SOCKET
    }

    interface ExceptionMessages {
        String SERVICE_ID_IS_EMPTY = "Service id in payload is empty";
        String METHOD_ID_IS_EMPTY = "Method id in payload is empty";
        String SERVICE_NOT_EXISTS = "Service with id ''{0}'' does not exists in service registry";
        String METHOD_NOT_EXISTS = "Rsocket method with id ''{0}'' for service ''{1}'' does not exists in rsocketService";
        String SERVICE_NOT_SUPPORTED_RSOCKET = "Service with id ''{0}'' has not 'RSOCKET' service type";
        String UNSUPPORTED_DATA_FORMAT = "Unsupported payload data format: ''{0}''";
        String UNSUPPORTED_TRANSPORT = "Unsupported RSocket transport: ''{0}''";
        String RSOCKET_RESTART_FAILED = "Rsocket restart failed";
        String INVALID_RSOCKET_COMMUNICATION_CONFIGURATION = "Some required fields in RSocket communication configuration are null: ";
    }

    interface LoggingMessages {
        String RSOCKET_TCP_ACCEPTOR_STARTED_MESSAGE = "RSocket TCP acceptor started in {0}[ms]";
        String RSOCKET_WS_ACCEPTOR_STARTED_MESSAGE = "RSocket WS acceptor started in {0}[ms]";
        String RSOCKET_TCP_COMMUNICATOR_STARTED_MESSAGE = "RSocket TCP communicator started";
        String RSOCKET_WS_COMMUNICATOR_STARTED_MESSAGE = "RSocket WebSocket communicator started";
        String RSOCKET_RESTARTED_MESSAGE = "RSocket Server restarted in {0}[ms]";
        String RSOCKET_LOADED_SERVICE_MESSAGE = "RSocket service loaded: ''{0}:{1,number,#}'' - ''{2}''.''{3}''";
    }
}
