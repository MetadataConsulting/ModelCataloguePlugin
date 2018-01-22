<asset:javascript src="mdx.js"/>

<script type="text/javascript">
    function unSelectAllMappingSuggestionIdsCheckboxes() {
        unselectAllCheckboxes('mappingSuggestionIds');
        var btn = document.getElementById('selectAll');
        btn.innerHTML = 'Toggle All';
        btn.setAttribute('onclick', 'javascript:selectAllMappingSuggestionIdsCheckboxes();');
    }

    function selectAllMappingSuggestionIdsCheckboxes() {
        selectAllCheckboxes('mappingSuggestionIds');
        var btn = document.getElementById('selectAll');
        btn.innerHTML = 'Toggle All';
        btn.setAttribute('onclick', 'javascript:unSelectAllMappingSuggestionIdsCheckboxes();');
    }
</script>

<a id="selectAll"
   class="btn btn-default"
   onclick="javascript:selectAllMappingSuggestionIdsCheckboxes();">Toggle All</a>