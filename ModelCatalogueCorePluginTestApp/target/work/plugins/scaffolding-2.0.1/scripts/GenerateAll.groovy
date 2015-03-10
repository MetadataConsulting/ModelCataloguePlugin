/*
 * Copyright 2004-2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Generates a CRUD controller and matching views for a given domain class
 *
 * @author Graeme Rocher
 *
 * @since 0.4
 */

includeTargets << new File(scaffoldingPluginDir, 'scripts/_GrailsGenerate.groovy')

generateViews = true
generateController = true

target (generateAll: "Generates a CRUD interface (controller + views) for a domain class") {
	depends(checkVersion, parseArguments, packageApp)

	promptForName(type: "Domain Class")

	try {
		String name = argsMap["params"][0]
		if (!name || name == "*") {
			uberGenerate()
		}
		else {
			generateForName = name
			generateForOne()
		}
	}
	catch (Exception e) {
		logError "Error running generate-all", e
	}
}

setDefaultTarget 'generateAll'
