<g:if test="${clinicalReports}">
  <p class="preserve-new-lines">${clinicalReports.description}</p>
  <ul>
    <g:each in="${clinicalReports.parentOf}">
    <li><strong>${it.name}</strong><p class="preserve-new-lines">${it.description}</p></li>
    </g:each>
  </ul>
</g:if>
<g:else>
  Clinical tests are not available
</g:else>
