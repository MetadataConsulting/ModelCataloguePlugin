function selectAllCheckboxes(attName) {
    var elements = document.getElementsByTagName('input');
    for ( var i = 0; i < elements.length; i++ ) {
        var element = elements[i];
        if ( element.getAttribute("type") === 'checkbox' && element.getAttribute("name") === attName ) {
            element.setAttribute('checked', 'checked');
        }
    }
}
function unselectAllCheckboxes(attName) {
    var elements = document.getElementsByTagName('input');
    for ( var i = 0; i < elements.length; i++ ) {
        var element = elements[i];
        if ( element.getAttribute("type") === 'checkbox' && element.getAttribute("name") === attName ) {
            element.removeAttribute('checked')
        }
    }
}