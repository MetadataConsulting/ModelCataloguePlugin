<g:if test="${eligibility}">
  <p class="preserve-new-lines">${eligibility.eligibility.description}</p>

  <h3>Inclusion Criteria</h3>
  <p class="preserve-new-lines">${eligibility.inclusionCriteria?.description}</p>

  <h3>Exclusion Criteria</h3>
  <p class="preserve-new-lines">${eligibility.exclusionCriteria?.description}</p>

  <h3>Prior Genetic Testing</h3>
  <p class="preserve-new-lines">${eligibility.pgt?.description}</p>

  <h3>Genes</h3>
  <p class="preserve-new-lines">${eligibility.genes?.description}</p>

  <h3>Closing Statement</h3>
  <p class="preserve-new-lines">${eligibility.cs?.description}</p>
</g:if>
<g:else>
  Eligibility criteria is not available
</g:else>
