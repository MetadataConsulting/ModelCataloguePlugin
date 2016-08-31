<g:if test="${phenotypes}">
  <p class="preserve-new-lines">${phenotypes.description}</p>
  <g:each in="${phenotypes.parentOf}">
    %{--TODO: fix the URL when migrated to just HPO IDs--}%
    <h3><a href="${it.modelCatalogueId}" onclick="window.open(this.href);return false;">${it.name}</a></h3>
    <p class="preserve-new-lines">${it.description}</p>
  </g:each>
</g:if>
<g:else>
  Phenotypes are not available
</g:else>
