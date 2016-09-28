<g:if test="${phenotypes}">
  <p class="preserve-new-lines">${phenotypes.description}</p>
  <g:each in="${phenotypes.parentOf}">
    <h3><a href="http://purl.obolibrary.org/obo/${it.modelCatalogueId?.replace(':', '_')}" onclick="window.open(this.href);return false;">${it.name}</a></h3>
    <p class="preserve-new-lines">${it.description}</p>
  </g:each>
</g:if>
<g:else>
  Phenotypes are not available
</g:else>
