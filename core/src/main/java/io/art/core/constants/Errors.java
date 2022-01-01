/*
 * ART
 *
 * Copyright 2019-2022 ART
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.art.core.constants;

public interface Errors {
    String CONTEXT_ALREADY_INITIALIZED = "Context already initialized";
    String MODULE_WAS_NOT_FOUND = "Module ''{0}'' was not found in context";
    String COULD_NOT_FIND_AVAILABLE_PORT_AFTER_ATTEMPTS = "Could not find an available ''{0}'' port in the range [{1,number,#}, {2,number,#}] after {3,number,#} attempts";
    String COULD_NOT_FIND_AVAILABLE_PORTS_IN_THE_RANGE = "Could not find {0,number,#} available ''{1}'' ports in the range [{2,number,#}, {3,number,#}]";
    String MIME_TYPE_MUST_NOT_BE_EMPTY = "Type must not be empty";
    String MIME_SUBTYPE_MUST_NOT_BE_EMPTY = "Subtype must not be empty";
    String MIME_DOES_NOT_CONTAIN_SLASH = "Does not contain '/'";
    String MIME_DOES_NOT_CONTAIN_SUBTYPE = "Does not contain subtype after '/'";
    String WILDCARD_TYPE_IS_LEGAL_ONLY_FOR_ALL_MIME_TYPES = "Wildcard type is legal only in '*/*' (all mime types)";
    String INVALID_TOKEN = "Invalid token character ''{0}'' in token ''{1}''";
    String PARAMETER_ATTRIBUTE_MUST_NOT_BE_EMPTY = "Parameter 'attribute' must not be empty";
    String PARAMETER_VALUE_MUST_NOT_BE_EMPTY = "Parameter 'emit' must not be empty";
    String FILE_PATH_NOT_VALID = "File path is not valid: ''{0}''";
    String UNABLE_TO_CREATE_DIRECTORY = "Unable to create directory: ''{0}''";
    String UNKNOWN_DURATION_TIME_UNITS = "Unknown duration time units: ''{0}''";
    String METHOD_NOT_IMPLEMENTED = "Method not implemented: ''{0}''";
    String DATA_FORMAT_IS_NULL = "Argument 'dataFormat' is null";
    String DUPLICATE_KEY = "Duplicate key {0}";
    String IMPOSSIBLE_MESSAGE = "Occurred impossible situation. Possible bug inside core or generation mechanism";
    String MANAGED_VALUE_IS_NULL = "Managed loaded value is null";
    String UNKNOWN_PROCESSOR_ARCHITECTURE = "Unknown processor architecture: {0}";
    String CONTEXT_NOT_INITIALIZED = "Context is not initialized";
    String EXECUTOR_SERVICE_WAS_NOT_TERMINATED = "Executor service was not terminated during: {0}";
    String CONFIGURATION_PATH_DOES_NOT_EXIST = "Configuration path does not exist: '{0}'";
}
