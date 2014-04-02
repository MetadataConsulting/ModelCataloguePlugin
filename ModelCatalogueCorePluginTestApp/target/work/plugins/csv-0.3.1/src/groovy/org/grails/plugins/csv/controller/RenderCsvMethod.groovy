package org.grails.plugins.csv.controller

import org.grails.plugins.csv.CSVWriter

class RenderCsvMethod {

	final controller

	RenderCsvMethod(controller) {
	   this.controller = controller
	}

	void call(Map args, Closure columns) {
		if (args.rows == null) {
			throw new IllegalArgumentException("The 'rows' argument must be provided")
		}

		def response = controller.response

		if (args.filename) {
			def classifier = (args.attachment == false) ? 'inline' : 'attachment'
			response.setHeader("Content-Disposition", "$classifier; filename=\"$args.filename\";")
		}

		response.setContentType("text/csv")

		new CSVWriter(response.writer, columns).writeAll(args.rows)
	}

}