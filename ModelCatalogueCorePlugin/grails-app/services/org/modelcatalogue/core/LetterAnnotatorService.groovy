package org.modelcatalogue.core

import org.modelcatalogue.core.util.ClassificationFilter
import org.modelcatalogue.letter.annotator.AnnotatedLetter
import org.modelcatalogue.letter.annotator.CandidateTerm
import org.modelcatalogue.letter.annotator.Highlighter
import org.modelcatalogue.letter.annotator.LetterAnnotator
import org.modelcatalogue.letter.annotator.TermOccurrence
import org.modelcatalogue.letter.annotator.lucene.LuceneLetterAnnotator

class LetterAnnotatorService {

    static transactional = false

    ClassificationService classificationService

    def annotateLetter(Set<Classification> classifications, String letter, OutputStream assetStream) {
        // language=HTML
        assetStream << """
        <html>
            <head>
                <!-- Latest compiled and minified CSS -->
                <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css" integrity="sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ==" crossorigin="anonymous">

                <!-- Optional theme -->
                <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css" integrity="sha384-aUGj/X2zp5rLCbBxumKTCw2Z50WgIr1vs/PFN4praOTvYXWlVyh2UtNUU0KAUhAX" crossorigin="anonymous">

                <style>
                    body {
                        margin-top: 50px;
                    }

                    .popover-content {
                        white-space: pre-line;
                    }

                    .table {
                        font-size: smaller;
                    }

                </style>


                <!-- Latest jQuery-->
                <script src="https://code.jquery.com/jquery-2.1.4.min.js"></script>

                <!-- Latest compiled and minified JavaScript -->
                <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js" integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ==" crossorigin="anonymous"></script>
                <script type="text/javascript">
                    \$(function () {
                      \$('[data-toggle="popover"]').popover({trigger: 'hover'})
                    })
                </script>
            </head>
            <body>
               <div class="container">
                   <div class="row">
                        <div class="col-md-offset-1 col-md-10">
                            <pre>"""

        LetterAnnotator annotator = new LuceneLetterAnnotator()

        for (Classification classification in classifications) {
            List<Model> models = classificationService.classified(Model, ClassificationFilter.includes(classification)).list(
                    sort: 'versionNumber',
                    order: 'desc'
            )

            models.eachWithIndex { Model m, idx ->
                log.info "[${idx + 1}/${models.size()}] Adding $m.name to index"
                annotator.addCandidate(CandidateTerm.create(m.name).with { CandidateTerm.Builder builder ->
                    description m.description
                    url(m.getDefaultModelCatalogueId(true))
                    title "$m.name ($classification.name)"
                    extensions 'data-toggle', 'popover'
                    extensions 'id', m.getId()
                    extensions 'data-classification', classification.name
                    extensions 'placement', 'bottom'
                    builder
                });
            }

        }

        log.info "Finding candidate terms"
        AnnotatedLetter annotatedLetter = annotator.annotate(letter, Highlighter.HTML)

        assetStream << annotatedLetter.highlighted
        log.info "Annotated letter exported"

        assetStream << """</pre>
                        </div>
                    </div>
        """

        if (annotatedLetter.occurrences) {
            assetStream << """
            <div class="row">
                <div class="col-md-offset-1 col-md-10">
                    <h4 id="occurrences">Terms Occurrences</h4>
                    <table class="table table-striped">
            """

            for (TermOccurrence occurrence in annotatedLetter.occurrences) {
                assetStream << """<tr>"""

                assetStream << """</td><td class="col-md-2 text-right">${occurrence.occurrence}</td>"""

                assetStream << """
                    <td class="col-md-10"><a href="#${occurrence.term.extensions.id}">${occurrence.term.term}</a>
                """

                if (occurrence.term.url) {
                    assetStream << """ <a href="${occurrence.term.url}" target="_blank"><span class="glyphicon glyphicon-share"></span></a>"""
                }

                if (occurrence.term.extensions.'data-classification') {
                    assetStream << """ <span class="text-muted">${occurrence.term.extensions.'data-classification'}</span>"""
                }

                assetStream << """</td>"""


                assetStream << """</tr>"""
            }

            assetStream << """</table>
                        </div>
                    </div>
            """
        }

        assetStream << """</div>
            </body>
        </html>
        """
    }
}
